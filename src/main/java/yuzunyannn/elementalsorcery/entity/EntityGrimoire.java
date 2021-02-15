package yuzunyannn.elementalsorcery.entity;

import java.util.List;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
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
import yuzunyannn.elementalsorcery.grimoire.MantraEffectFlags;
import yuzunyannn.elementalsorcery.grimoire.WantedTargetResult;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;
import yuzunyannn.elementalsorcery.network.MessageEntitySync;
import yuzunyannn.elementalsorcery.render.item.RenderItemGrimoireInfo;
import yuzunyannn.elementalsorcery.util.ExceptionHelper;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;
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

	public EntityGrimoire(World worldIn, EntityLivingBase user, Mantra mantra, NBTTagCompound metaData) {
		this(worldIn, user, mantra, metaData, STATE_BEFORE_SPELLING);
	}

	// 这个构造方法是给予某些不通过魔法书进行处理的
	public EntityGrimoire(World worldIn, EntityLivingBase user, Mantra mantra, NBTTagCompound metaData, byte state) {
		super(worldIn);
		this.user = user;
		if (user != null) this.userUUID = user.getUniqueID();
		this.mantra = mantra;
		this.metaData = metaData == null ? new NBTTagCompound() : metaData;
		this.state = state;
		this.initMantraData();
		this.restoreGrimoire();// 最开始直接进行还原，初始发送信息使用
	}

	private void initMantraData() {
		if (mantra == null) return;
		this.mantraData = mantra.getData(this.metaData, world, this);
	}

	@Override
	public void setDead() {
		if (!isDead) this.onDead();
		super.setDead();
	}

	public static final byte STATE_BEFORE_SPELLING = 0;
	public static final byte STATE_SPELLING = 1;
	public static final byte STATE_AFTER_SPELLING = 2;

	protected byte state = STATE_BEFORE_SPELLING;
	protected int tick;
	/** 使用者，如果服务器关闭后尽可能会被还原，但是STATE_AFTER_SPELLING中可能存在为null的时候 */
	protected EntityLivingBase user;
	protected UUID userUUID;
	/** 咒文 */
	protected Mantra mantra;
	protected IMantraData mantraData;
	/** 魔导书 */
	public Grimoire grimoire;
	public ItemStack grimoireStack = ItemStack.EMPTY;
	public NBTTagCompound grimoireDataFromServer;
	/** 原始数据 */
	protected NBTTagCompound metaData;

	/** 客户端 */

	@Override
	protected void entityInit() {
		this.setSize(0.1f, 0.1f);
	}

	public IMantraData getMantraData() {
		return mantraData;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		userUUID = compound.getUniqueId("user");
		tick = compound.getInteger("tick");
		state = compound.getByte("state");
		mantra = Mantra.REGISTRY.getValue(new ResourceLocation(compound.getString("mantra")));
		if (mantra == null) {
			ElementalSorcery.logger.warn("EntityGrimoire恢复数据时出现了找不到mantra的异常！");
			return;
		}
		this.initMantraData();
		if (mantraData != null) mantraData.deserializeNBT(compound.getCompoundTag("mData"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		this.writeEntityToNBT(compound, false);
	}

	protected void writeEntityToNBT(NBTTagCompound compound, boolean isSend) {
		compound.setUniqueId("user", userUUID);
		compound.setInteger("tick", tick);
		compound.setByte("state", state);
		compound.setString("mantra", mantra.getRegistryName().toString());
		if (mantraData != null) {
			NBTTagCompound nbt = isSend ? mantraData.serializeNBTForSend() : mantraData.serializeNBT();
			if (nbt != null && !nbt.hasNoTags()) compound.setTag("mData", nbt);
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeEntityToNBT(nbt, true);
		if (metaData != null) nbt.setTag("oData", metaData);
		if (grimoire != null) nbt.setTag("gData", grimoire.serializeNBT());
		ByteBufUtils.writeTag(buffer, nbt);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		NBTTagCompound nbt = ByteBufUtils.readTag(additionalData);
		if (nbt.hasKey("oData", NBTTag.TAG_COMPOUND)) metaData = nbt.getCompoundTag("oData");
		if (nbt.hasKey("gData", NBTTag.TAG_COMPOUND)) grimoireDataFromServer = nbt.getCompoundTag("gData");
		readEntityFromNBT(nbt);
	}

	/** 需要精准还原，所以使用uuid */
	protected void restoreUser() {
		List<EntityLivingBase> list = world.getEntities(EntityLivingBase.class, (e) -> {
			return e.getUniqueID().equals(userUUID);
		});
		if (list.isEmpty()) return;
		user = list.get(0);
	}

	protected void restoreGrimoire() {
		// 释放法术后处理，魔法书已经不是必须的了
		if (state == STATE_AFTER_SPELLING) grimoire = new Grimoire();
		else {
			ItemStack hold = user.getHeldItem(EnumHand.MAIN_HAND);
			grimoireStack = hold;
			grimoire = hold.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
			if (world.isRemote) {
				if (grimoireDataFromServer != null && grimoire != null) grimoire.deserializeNBT(grimoireDataFromServer);
			}
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
		this.posY = user.posY;
		this.posZ = user.posZ;
	}

	@Override
	public void onUpdate() {
		try {
			this.onEntityUpdate();
		} catch (Exception e) {
			String msg = "魔导书使用过程中出现异常！";
			ElementalSorcery.logger.warn("魔导书使用过程中出现异常！", e);
			ExceptionHelper.warnSend(world, msg);
			super.setDead();
		}
	}

	@Override
	public void onEntityUpdate() {
		this.lastTickPosX = this.prevPosX = this.posX;
		this.lastTickPosY = this.prevPosY = this.posY;
		this.lastTickPosZ = this.prevPosZ = this.posZ;
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
	}

	@SideOnly(Side.CLIENT)
	protected void closeBook() {
		if (user == null) return;
		EventClient.addTickTask(() -> {
			if (grimoire == null) return ITickTask.END;
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
		EventServer.addTask(() -> {
			if (user != null) {
				// 使用者有开始用了
				if (user.getHeldItemMainhand().getCapability(Grimoire.GRIMOIRE_CAPABILITY, null) == grimoire) {
					if (user.isHandActive()) return;
				}
			}
			grimoire.saveState(grimoireStack);
		}, 10);
	}

	protected void onAfterSpelling() {
		boolean flag = mantra.afterSpelling(world, mantraData, this);
		if (flag) return;
		this.setDead();
	}

	@Override
	public void stopCaster() {
		this.setDead();
	}

	@Override
	public void sendToClient(NBTTagCompound nbt) {
		if (world.isRemote) return;
		MessageEntitySync.sendToClient(this, nbt);
	}

	@Override
	public void onRecv(NBTTagCompound data) {
		mantra.recvData(world, mantraData, this, data);
	}

	@Override
	public ElementStack iWantSomeElement(ElementStack need, boolean consume) {
		if (user instanceof EntityPlayer && ((EntityPlayer) user).isCreative()) {
			need = need.copy();
			need.setPower(1000);
			return need;
		}
		IElementInventory inv = grimoire.getInventory();
		if (inv == null) return ElementStack.EMPTY;
		ElementStack estack = inv.extractElement(need, true);
		if (estack.arePowerfulAndMoreThan(need)) {
			if (consume) return inv.extractElement(need, false);
			return estack;
		}
		if (need.isMagic()) return this.tryChangeToMagic(inv, need, consume);
		return ElementStack.EMPTY;
	}

	protected ElementStack tryChangeToMagic(IElementInventory inv, ElementStack need, boolean consume) {
		int startIndex = rand.nextInt(inv.getSlots());
		for (int i = 0; i < inv.getSlots(); i++) {
			ElementStack es = inv.getStackInSlot((i + startIndex) % inv.getSlots());
			if (es.isEmpty()) continue;
			int count = findNeedCountForChangeToMagic(es.copy(), need);
			if (count > 0) {
				if (consume) es.shrink(count);
				es = es.copy();
				es.setCount(count);
				return es.becomeMagic(world);
			}
		}
		return ElementStack.EMPTY;
	}

	/** 寻找某个元素转化到给定魔法量所需要的数量 */
	private int findNeedCountForChangeToMagic(ElementStack example, ElementStack need) {
		// 尝试全部转化，如果不能满足直接返回
		ElementStack origin = example.copy();
		example = example.becomeMagic(world);
		if (!example.arePowerfulAndMoreThan(need)) return -1;
		// 认为转化是线性的，进行处理
		float rate = example.getCount() / (float) origin.getCount();
		int needCount = MathHelper.ceil(need.getCount() / rate);
		if (needCount > origin.getCount()) return -1;
		origin.setCount(needCount);
		example = origin.becomeMagic(world);
		if (example.arePowerfulAndMoreThan(need)) return needCount;
		// 不符合线性模型，或者是递减线性模型，就不找了
		return -1;
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
	public WantedTargetResult iWantBlockTarget() {
		if (user != null) {
			RayTraceResult rt = WorldHelper.getLookAtBlock(world, user, 128);
			if (rt == null) return WantedTargetResult.EMPTY;
			if (rt.getBlockPos() == null) return WantedTargetResult.EMPTY;
			return new WantedTargetResult(rt.getBlockPos(), rt.sideHit, rt.hitVec);
		} else {
			BlockPos pos = this.getPosition();
			pos = BlockHelper.tryFindAnySolid(world, pos, 6, 5, 5);
			if (pos == null) return WantedTargetResult.EMPTY;
			return new WantedTargetResult(pos, EnumFacing.UP,
					new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5));
		}
	}

	@Override
	public <T extends Entity> WantedTargetResult iWantLivingTarget(Class<T> cls) {
		if (user != null) {
			RayTraceResult rt = WorldHelper.getLookAtEntity(world, user, 64, cls);
			if (rt == null) return WantedTargetResult.EMPTY;
			return new WantedTargetResult(rt.entityHit, rt.hitVec);
		} else {
			final int size = 2;
			AxisAlignedBB aabb = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size,
					posZ + size);
			List<T> list = world.getEntitiesWithinAABB(cls, aabb);
			if (list.isEmpty()) return WantedTargetResult.EMPTY;
			T find = list.get(rand.nextInt(list.size()));
			return new WantedTargetResult(find, find.getPositionVector().addVector(0, find.height, 0));
		}
	}

	@Override
	public Entity iWantCaster() {
		return user == null ? this : user;
	}

	@Override
	public Entity iWantDirectCaster() {
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffectFlags(MantraEffectFlags flag) {
		return true;
	}

}
