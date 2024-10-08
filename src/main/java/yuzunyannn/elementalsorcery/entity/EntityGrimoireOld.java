package yuzunyannn.elementalsorcery.entity;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.CastStatus;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.ICasterObject;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.target.WorldObjectEntity;
import yuzunyannn.elementalsorcery.api.util.target.WorldTarget;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.logics.ITickTask;
import yuzunyannn.elementalsorcery.network.MessageEntitySync;
import yuzunyannn.elementalsorcery.render.item.RenderItemGrimoireInfo;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.ExceptionHelper;
import yuzunyannn.elementalsorcery.util.world.CasterHelper;

@Deprecated
public class EntityGrimoireOld extends Entity
		implements IEntityAdditionalSpawnData, ICaster, ICasterObject, MessageEntitySync.IRecvData {

	public static void start(World world, EntityLivingBase user, Grimoire grimoire) {
		user.setActiveHand(EnumHand.MAIN_HAND);
		if (world.isRemote) return;
		Grimoire.Info info = grimoire.getSelectedInfo();
		if (info == null) return;
		EntityGrimoireOld e = new EntityGrimoireOld(world, user, info.getMantra(), info.getData());
		e.lockUser();
		world.spawnEntity(e);
	}

	public EntityGrimoireOld(World worldIn) {
		super(worldIn);
	}

	public EntityGrimoireOld(World worldIn, EntityLivingBase user, Mantra mantra, NBTTagCompound metaData) {
		this(worldIn, user, mantra, metaData, CastStatus.BEFORE_SPELLING);
	}

	// 这个构造方法是给予某些不通过魔法书进行处理的
	public EntityGrimoireOld(World worldIn, @Nullable EntityLivingBase user, Mantra mantra, NBTTagCompound metaData,
			CastStatus state) {
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

	@Override
	public boolean isAlive() {
		return !isDead;
	}

	protected CastStatus state = CastStatus.BEFORE_SPELLING;
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
	public CastStatus getCastStatus() {
		return state;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		userUUID = nbt.getUniqueId("user");
		tick = nbt.getInteger("tick");
		state = CastStatus.fromIndex(nbt.getByte("state"));
		mantra = Mantra.REGISTRY.getValue(new ResourceLocation(nbt.getString("mantra")));
		if (mantra == null) {
			ESAPI.logger.warn("EntityGrimoire恢复数据时出现了找不到mantra的异常！");
			return;
		}
		this.initMantraData();
		if (mantraData != null) mantraData.deserializeNBT(nbt.getCompoundTag("mData"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		this.writeEntityToNBT(compound, false);
	}

	protected void writeEntityToNBT(NBTTagCompound nbt, boolean isSend) {
		if (userUUID != null) nbt.setUniqueId("user", userUUID);
		nbt.setInteger("tick", tick);
		nbt.setByte("state", state.index);
		nbt.setString("mantra", mantra.getRegistryName().toString());
		if (mantraData != null) {
			NBTTagCompound mData = isSend ? mantraData.serializeNBTForSend() : mantraData.serializeNBT();
			if (mData != null && !mData.isEmpty()) nbt.setTag("mData", mData);
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeEntityToNBT(nbt, true);
		if (metaData != null) nbt.setTag("oData", metaData);
		// if (grimoire != null) nbt.setTag("gData", grimoire.serializeNBT());
		ByteBufUtils.writeTag(buffer, nbt);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		NBTTagCompound nbt = ByteBufUtils.readTag(additionalData);
		if (nbt.hasKey("oData", NBTTag.TAG_COMPOUND)) metaData = nbt.getCompoundTag("oData");
		// if (nbt.hasKey("gData", NBTTag.TAG_COMPOUND)) grimoireDataFromServer =
		// nbt.getCompoundTag("gData");
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
		if (state == CastStatus.AFTER_SPELLING) grimoire = new Grimoire();
		else {
			ItemStack hold = user.getHeldItem(EnumHand.MAIN_HAND);
			grimoireStack = hold;
			grimoire = hold.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
			if (grimoire != null) grimoire.tryLoadState(hold);
//			if (world.isRemote) {
//				if (grimoireDataFromServer != null && grimoire != null) grimoire.deserializeNBT(grimoireDataFromServer);
//			}
		}

		if (grimoire != null) {
			Grimoire.Info info = grimoire.getSelectedInfo();
			metaData = info == null ? new NBTTagCompound() : info.getData();
		}
	}

	protected boolean restore() {
		// 还原user
		if (user == null) {
			this.restoreUser();
			if (user == null) canNoUser: {
				if (state == CastStatus.AFTER_SPELLING && !mantra.mustUser()) break canNoUser;
				ESAPI.logger.warn("魔导书还原使用者出现异常！", userUUID);
				return false;
			}
		}
		// 还原魔法书
		if (grimoire == null) {
			restoreGrimoire();
			if (grimoire == null) {
				ESAPI.logger.warn("魔导书还原使grimoire出现异常！", userUUID);
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
			ESAPI.logger.warn("魔导书使用过程中出现异常！", e);
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

		if (state == CastStatus.BEFORE_SPELLING) {
			this.lockUser();
			this.onStartSpelling();
			state = CastStatus.SPELLING;
		} else {
			if (state == CastStatus.SPELLING) {
				this.lockUser();
				// 客户端开书
				if (world.isRemote) {
					RenderItemGrimoireInfo info = grimoire.getRenderInfo();
					info.open();
					info.update();
				} else {
					if (!canContinueSpelling()) {
						world.setEntityState(this, (byte) 44);
						this.onEndSpelling();
						state = CastStatus.AFTER_SPELLING;
						return;
					}
				}
				this.onSpelling();
			} else if (state == CastStatus.AFTER_SPELLING) this.onAfterSpelling();
			else this.setDead();
		}
	}

	public boolean canContinueSpelling() {
		return canContinueSpelling(user);
	}

	public static boolean canContinueSpelling(Entity entity) {
		if (entity == null) return false;
		if (entity.isDead) return false;
		if (entity instanceof EntityLivingBase) {
			if (!((EntityLivingBase) entity).isHandActive()) return false;
		} else {
			return false;
		}
		if (ESAPI.silent.isSilent(entity, SilentLevel.SPELL)) return false;
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 44) {
			this.closeBook();
			this.onEndSpelling();
			state = CastStatus.AFTER_SPELLING;
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
		Grimoire.Info info = grimoire.getSelectedInfo();
		if (info != null && info.getMantra() == mantra) info.setData(metaData);
		grimoire.saveState(grimoireStack);

		// 延迟一下，等待合书动画
//		EventServer.addTask(() -> {
//			// 连开的情况
//			if (user != null && user.isHandActive()) {
//				if (user.getHeldItemMainhand().getCapability(Grimoire.GRIMOIRE_CAPABILITY, null) == grimoire) return;
//			}
//			grimoire.saveState(grimoireStack);
//		}, 5);
	}

	protected void onAfterSpelling() {
		boolean flag = mantra.afterSpelling(world, mantraData, this);
		if (!flag) this.state = CastStatus.END;
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
	public ElementStack iWantAnyElementSample(int seed) {
		seed = Math.abs(seed);
		IElementInventory eInv = grimoire.getInventory();
		if (eInv == null || eInv.getSlots() == 0) {
			if (EntityHelper.isCreative(user))
				return new ElementStack(Element.getElementFromIndex(seed, true), 1000, 1000);
			return ElementStack.EMPTY;
		}
		for (int i = 0; i < eInv.getSlots(); i++) {
			ElementStack estack = eInv.getStackInSlot((i + seed) % eInv.getSlots());
			if (estack.isEmpty()) continue;
			return estack.copy();
		}
		if (EntityHelper.isCreative(user)) return new ElementStack(Element.getElementFromIndex(seed, true), 1000, 1000);
		return ElementStack.EMPTY;
	}

	@Override
	public ElementStack iWantSomeElement(ElementStack need, boolean consume) {
		if (EntityHelper.isCreative(user)) {
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

	@Override
	public ElementStack iWantGiveSomeElement(ElementStack give, boolean accpet) {
		IElementInventory inv = grimoire.getInventory();
		if (inv == null) return give;
		if (!inv.insertElement(give, true)) return give;
		if (accpet) inv.insertElement(give, false);
		return ElementStack.EMPTY;
	}

	protected ElementStack tryChangeToMagic(IElementInventory inv, ElementStack need, boolean consume) {
		int startIndex = rand.nextInt(inv.getSlots());
		for (int i = 0; i < inv.getSlots(); i++) {
			ElementStack es = inv.getStackInSlot((i + startIndex) % inv.getSlots());
			if (es.isEmpty()) continue;
			int count = findNeedCountForChangeToMagic(world, es.copy(), need);
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
	public static int findNeedCountForChangeToMagic(World world, ElementStack example, ElementStack need) {
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

	@Override
	public void iWantGivePotent(float potent, float point) {
		grimoire.addPotent(potent, point);
	}

	@Override
	public float iWantBePotent(float point, boolean justTry) {
		float rPoint = Math.min(grimoire.potentPoint, point);
		float potent = grimoire.getPotent() * (rPoint / point);
		if (justTry) return potent;
		grimoire.addPotentPoint(-rPoint);
		return potent;
	}

	@Override
	public BlockPos iWantFoothold() {
		if (user != null) return CasterHelper.findFoothold(user, 128);
		else {
			BlockPos pos = this.getPosition();
			if (!CasterHelper.canStand(world, pos)) return null;
			if (CasterHelper.canStand(world, pos.down())) return pos.down();
		}
		return null;
	}

	@Override
	public WorldTarget iWantBlockTarget() {
		if (user != null) return CasterHelper.findLookBlockResult(user, 128, false);
		else {
			BlockPos pos = this.getPosition();
			pos = BlockHelper.tryFindAnySolid(world, pos, 6, 5, 5);
			if (pos == null) return WorldTarget.EMPTY;
			return new WorldTarget(pos, EnumFacing.UP, new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5));
		}
	}

	@Override
	public <T extends Entity> WorldTarget iWantEntityTarget(Class<T> cls) {
		if (user != null) return CasterHelper.findLookTargetResult(cls, user, 128);
		else {
			final int size = 2;
			AxisAlignedBB aabb = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size,
					posZ + size);
			List<T> list = world.getEntitiesWithinAABB(cls, aabb);
			if (list.isEmpty()) return WorldTarget.EMPTY;
			T find = list.get(rand.nextInt(list.size()));
			return new WorldTarget(find, find.getPositionVector().add(0, find.height, 0));
		}
	}

	@Override
	public DamageSource iWantDamageSource(ElementStack element) {
		return DamageHelper.getDamageSource(element, user == null ? this : user, this);
	}

	@Override
	public Vec3d iWantDirection() {
		if (user != null) return user.getLookVec();
		return new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).normalize();
	}

	@Override
	public IWorldObject iWantCaster() {
		return new WorldObjectEntity(user == null ? this : user);
	}

	@Override
	public ICasterObject iWantDirectCaster() {
		return this;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public void setPositionVector(Vec3d pos, boolean force) {
		if (force) this.setPositionAndUpdate(pos.x, pos.y, pos.z);
		else {
			this.posX = pos.x;
			this.posY = pos.y;
			this.posZ = pos.z;
		}
	}

	@Override
	public TileEntity toTileEntity() {
		return null;
	}

	@Override
	public Entity toEntity() {
		return this;
	}

	@Override
	public Mantra iWantMantra() {
		return this.mantra;
	}

	@Override
	public IMantraData iWantMantraData() {
		return this.mantraData;
	}

	@Override
	public Vec3d getObjectPosition() {
		return this.getPositionVector();
	}
	
	@Override
	public CapabilityObjectRef toRef() {
		return CapabilityObjectRef.of(this);
	}

}
