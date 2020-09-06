package yuzunyannn.elementalsorcery.entity;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.event.ITickTask;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.Mantra;
import yuzunyannn.elementalsorcery.init.MantraRegister;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.network.MessageEntitySync;
import yuzunyannn.elementalsorcery.render.item.RenderItemGrimoireInfo;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class EntityGrimoire extends Entity implements IEntityAdditionalSpawnData, ICaster, MessageEntitySync.IRecvData {

	public static void start(World world, EntityLivingBase user, Mantra mantra, NBTTagCompound originData) {
		user.setActiveHand(EnumHand.MAIN_HAND);
		if (world.isRemote) return;
		if (mantra == null) return;
		EntityGrimoire e = new EntityGrimoire(world, user, mantra, originData);
		e.lockUser();
		world.spawnEntity(e);
	}

	public EntityGrimoire(World worldIn) {
		super(worldIn);
	}

	public EntityGrimoire(World worldIn, EntityLivingBase user, Mantra mantra, NBTTagCompound originData) {
		super(worldIn);
		this.user = user;
		this.userUUID = user.getUniqueID().toString();
		this.mantra = mantra;
		this.originData = originData == null ? new NBTTagCompound() : originData;
		this.initMantraData();
	}

	private void initMantraData() {
		if (mantra == null) return;
		this.mantraData = mantra.getData(this.originData, world, this);
	}

	@Override
	public void setDead() {
		if (!isDead) this.onDead();
		super.setDead();
	}

	public static final int STATE_BEFORE_SPELLING = 0;
	public static final int STATE_SPELLING = 1;
	public static final int STATE_AFTER_SPELLING = 2;

	protected byte state = STATE_BEFORE_SPELLING;
	protected int tick;
	/** 使用者，如果服务器关闭后尽可能会被还原，但是STATE_AFTER_SPELLING中可能存在为null的时候 */
	protected EntityLivingBase user;
	protected String userUUID;
	/** 咒文 */
	protected Mantra mantra;
	protected IMantraData mantraData;
	/** 魔导书 */
	public Grimoire grimoire;
	public ItemStack grimoireStack = ItemStack.EMPTY;
	/** 原始数据 */
	protected NBTTagCompound originData;

	@Override
	protected void entityInit() {
		this.setSize(0.1f, 0.1f);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		userUUID = compound.getString("user");
		tick = compound.getInteger("tick");
		state = compound.getByte("state");
		mantra = MantraRegister.instance.getValue(new ResourceLocation(compound.getString("mantra")));
		if (mantra == null) {
			ElementalSorcery.logger.warn("EntityGrimoire恢复数据时出现了找不到mantra的异常！");
			return;
		}
		this.initMantraData();
		if (mantraData != null) mantraData.deserializeNBT(compound.getCompoundTag("mData"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setString("user", userUUID);
		compound.setInteger("tick", tick);
		compound.setByte("state", state);
		compound.setString("mantra", mantra.getRegistryName().toString());
		if (mantraData != null) {
			NBTTagCompound nbt = mantraData.serializeNBT();
			if (nbt != null && !nbt.hasNoTags()) compound.setTag("mData", nbt);
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeEntityToNBT(nbt);
		if (originData != null) nbt.setTag("oData", originData);
		ByteBufUtils.writeTag(buffer, nbt);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		NBTTagCompound nbt = ByteBufUtils.readTag(additionalData);
		if (nbt.hasKey("oData", 10)) originData = nbt.getCompoundTag("oData");
		readEntityFromNBT(nbt);
	}

	/** 需要精准还原，所以使用uuid */
	protected void restoreUser() {
		List<EntityLivingBase> list = world.getEntities(EntityLivingBase.class, (e) -> {
			return e.getUniqueID().toString().equals(userUUID);
		});
		if (list.isEmpty()) return;
		user = list.get(0);
	}

	protected void restoreGrimoire() {
		ItemStack hold = user.getHeldItem(EnumHand.MAIN_HAND);
		// 释放法术后处理，魔法书已经不是必须的了
		if (state == STATE_AFTER_SPELLING) grimoire = new Grimoire();
		else {
			grimoireStack = hold;
			grimoire = hold.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		}
	}

	protected boolean restore() {
		// 还原user
		if (user == null) {
			this.restoreUser();
			if (user == null) canNoUser: {
				if (state == STATE_AFTER_SPELLING && !mantra.mustUser()) break canNoUser;
				ElementalSorcery.logger.warn("魔导书还原使用者出现异常！", userUUID);
				return false;
			}
		}
		// 还原魔法书
		if (grimoire == null) {
			restoreGrimoire();
			if (grimoire == null) {
				ElementalSorcery.logger.warn("魔导书还原使grimoire出现异常！", userUUID);
				return false;
			}
		}
		return true;
	}

	protected void lockUser() {
		user.timeUntilPortal = 20;
		this.timeUntilPortal = 20;
		this.posX = user.posX;
		this.posY = user.posY + user.height * 3 / 4;
		this.posZ = user.posZ;
	}

	@Override
	public void onUpdate() {
		try {
			this.onEntityUpdate();
		} catch (Exception e) {
			ElementalSorcery.logger.warn("魔导书使用过程中出现异常！", e);
			super.setDead();
		}
	}

	@Override
	public void onEntityUpdate() {
		// 还原一些内容
		if (mantra == null || !this.restore()) {
			this.setDead();
			return;
		}
		tick++;
		if (state == STATE_BEFORE_SPELLING) {
			this.lockUser();
			this.onStartSpelling();
			state = STATE_SPELLING;
		} else {
			if (state == STATE_SPELLING) {
				if (!user.isHandActive() || user.isDead) {
					if (world.isRemote) this.closeBook();
					this.onEndSpelling();
					state = STATE_AFTER_SPELLING;
					return;
				}
				this.lockUser();
				// 客户端开书
				if (world.isRemote) {
					RenderItemGrimoireInfo info = grimoire.getRenderInfo();
					info.open();
					info.update();
				}
				this.onSpelling();
			} else {
				this.onAfterSpelling();
			}
		}
	}

	protected void onDead() {
		// 客户端关书
		if (world.isRemote) this.closeBook();
	}

	@SideOnly(Side.CLIENT)
	protected void closeBook() {
		if (user == null) return;
		EventClient.addTickTask(() -> {
			RenderItemGrimoireInfo info = grimoire.getRenderInfo();
			info.close();
			boolean flag = info.update() || user.isHandActive() || user.isDead;
			if (flag) info.reset();
			return flag ? ITickTask.END : ITickTask.SUCCESS;
		});
	}

	protected void onStartSpelling() {
		mantra.startSpelling(world, mantraData, this);
	}

	protected void onSpelling() {
		mantra.onSpelling(world, mantraData, this);
	}

	protected void onEndSpelling() {
		mantra.endSpelling(world, mantraData, this);
		if (world.isRemote) return;
		if (grimoireStack.isEmpty()) return;
		// 延迟一下，等待合书动画
		EventServer.addTickTask(() -> {
			if (user != null) {
				// 使用者有开始用了
				if (user.getHeldItemMainhand().getCapability(Grimoire.GRIMOIRE_CAPABILITY, null) == grimoire) {
					if (user.isHandActive()) return;
				}
			}
			grimoire.save(grimoireStack);
		}, 10);
	}

	protected void onAfterSpelling() {
		boolean flag = mantra.afterSpelling(world, mantraData, this);
		if (flag) return;
		this.setDead();
	}

	@Override
	public void sendToClient(NBTTagCompound nbt) {
		if (world.isRemote) return;
		MessageEntitySync message = new MessageEntitySync(this, nbt);
		TargetPoint point = new TargetPoint(world.provider.getDimension(), posX, posY, posZ, 64);
		ESNetwork.instance.sendToAllAround(message, point);
	}

	@Override
	public void onRecv(NBTTagCompound data) {
		mantra.recvData(world, mantraData, this, data);
	}

	@Override
	public ElementStack iWantSomeElement(ElementStack need, boolean consume) {
		IElementInventory inv = grimoire.getInventory();
		if (inv == null) return ElementStack.EMPTY;
		ElementStack estack = inv.extractElement(need, true);
		if (estack.arePowerfulAndMoreThan(need)) {
			if (consume) return inv.extractElement(need, false);
			return estack;
		}
		return ElementStack.EMPTY;
	}

	@Override
	public int iWantKnowCastTick() {
		return tick;
	}

	private boolean canStand(BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		return block.isReplaceable(world, pos);
	}

	@Override
	public BlockPos iWantFoothold() {
		if (user != null) {
			RayTraceResult rt = WorldHelper.getLookAtBlock(world, user, 128);
			if (rt == null) return null;
			BlockPos pos = new BlockPos(rt.hitVec);
			if (canStand(pos)) pos = pos.offset(rt.sideHit, -1);
			if (canStand(pos.up()) && canStand(pos.up(2))) return pos.up();
			pos = pos.offset(rt.sideHit, 1);
			if (canStand(pos) && canStand(pos.up())) return pos;
		} else {
			BlockPos pos = this.getPosition();
			if (!canStand(pos)) return null;
			if (canStand(pos.down())) return pos.down();
		}
		return null;
	}

	@Override
	public Entity iWantCaster() {
		return user == null ? this : user;
	}

	@Override
	public Entity iWantDirectCaster() {
		return this;
	}
}
