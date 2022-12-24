package yuzunyannn.elementalsorcery.entity;

import java.util.List;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
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
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.network.MessageEntitySync;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.ExceptionHelper;
import yuzunyannn.elementalsorcery.util.world.CasterHelper;

public abstract class EntityMantraBase extends Entity
		implements IEntityAdditionalSpawnData, ICaster, ICasterObject, MessageEntitySync.IRecvData {

	protected CastStatus state = CastStatus.BEFORE_SPELLING;
	protected int tick;
	/** 咒文 */
	protected Mantra mantra;
	protected IMantraData mantraData;
	/** 原始数据 */
	protected NBTTagCompound metaData;

	public EntityMantraBase(World worldIn) {
		super(worldIn);
	}

	public EntityMantraBase(World worldIn, Mantra mantra, NBTTagCompound metaData, CastStatus state) {
		super(worldIn);
		this.mantra = mantra;
		this.metaData = metaData == null ? new NBTTagCompound() : metaData;
		this.state = state;
		this.initMantraData();
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
	protected void entityInit() {
		this.setSize(0.05f, 0.05f);
		this.height = 0.05f;
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
		ByteBufUtils.writeTag(buffer, nbt);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		NBTTagCompound nbt = ByteBufUtils.readTag(additionalData);
		if (nbt.hasKey("oData", NBTTag.TAG_COMPOUND)) metaData = nbt.getCompoundTag("oData");
		readEntityFromNBT(nbt);
	}

	/** 尝试还原一些数据 */
	protected boolean restore() {
		IWorldObject caster = this.getUser();
		if (caster == null) {
			this.restoreUser();
			caster = this.getUser();
			if (caster == null) canNoUser: {
				if (state == CastStatus.AFTER_SPELLING && !mantra.mustUser()) break canNoUser;
				// ESAPI.logger.warn("还原使用者出现异常！");
				return false;
			}
		}
		return true;
	}

	@Override
	public void onUpdate() {
		try {
			this.onEntityUpdate();
		} catch (Exception e) {
			String msg = "咒文使用过程中出现异常！";
			ESAPI.logger.warn("咒文使用过程中出现异常！", e);
			ExceptionHelper.warnSend(world, msg);
			this.state = CastStatus.END;
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
			this.onLockUser();
			this.onStartSpelling();
			state = CastStatus.SPELLING;
		} else {
			if (state == CastStatus.SPELLING) {
				this.onLockUser();
				if (!world.isRemote) {
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

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 44) {
			this.onEndSpelling();
			state = CastStatus.AFTER_SPELLING;
		}
	}

	@Override
	public void stopCaster() {
		this.state = CastStatus.END;
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
		IElementInventory eInv = this.getElementInventory();
		Entity userEntity = getUser() != null ? getUser().asEntity() : null;
		if (eInv == null || eInv.getSlots() == 0) {
			if (EntityHelper.isCreative(userEntity))
				return new ElementStack(Element.getElementFromIndex(seed, true), 1000, 1000);
			return ElementStack.EMPTY;
		}
		for (int i = 0; i < eInv.getSlots(); i++) {
			ElementStack estack = eInv.getStackInSlot((i + seed) % eInv.getSlots());
			if (estack.isEmpty()) continue;
			return estack.copy();
		}
		if (EntityHelper.isCreative(userEntity))
			return new ElementStack(Element.getElementFromIndex(seed, true), 1000, 1000);
		return ElementStack.EMPTY;
	}

	@Override
	public ElementStack iWantSomeElement(ElementStack need, boolean consume) {
		Entity userEntity = getUser() != null ? getUser().asEntity() : null;
		if (EntityHelper.isCreative(userEntity)) {
			need = need.copy();
			need.setPower(1000);
			return need;
		}
		IElementInventory inv = this.getElementInventory();
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
		IElementInventory inv = this.getElementInventory();
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
	public BlockPos iWantFoothold() {
		IWorldObject user = this.getUser();
		if (user != null) {
			EntityLivingBase entityUser = user.asEntityLivingBase();
			if (entityUser != null) return CasterHelper.findFoothold(entityUser, 128);
		}
		BlockPos pos = this.getPosition();
		if (!CasterHelper.canStand(world, pos)) {
			if (CasterHelper.canStand(world, pos.up())) return pos.up();
			return null;
		}
		if (CasterHelper.canStand(world, pos.down())) return pos.down();
		return null;
	}

	@Override
	public WorldTarget iWantBlockTarget() {
		IWorldObject user = this.getUser();
		if (user != null) {
			EntityLivingBase entityUser = user.asEntityLivingBase();
			if (entityUser != null) return CasterHelper.findLookBlockResult(entityUser, 128, false);
		}
		BlockPos pos = this.getPosition();
		pos = BlockHelper.tryFindAnySolid(world, pos, 6, 5, 5);
		if (pos == null) return WorldTarget.EMPTY;
		return new WorldTarget(pos, EnumFacing.UP, new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5));

	}

	@Override
	public <T extends Entity> WorldTarget iWantEntityTarget(Class<T> cls) {
		IWorldObject user = this.getUser();
		if (user != null) {
			EntityLivingBase entityUser = user.asEntityLivingBase();
			if (entityUser != null) return CasterHelper.findLookTargetResult(cls, entityUser, 128);
		}
		final int size = 2;
		AxisAlignedBB aabb = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size,
				posZ + size);
		List<T> list = world.getEntitiesWithinAABB(cls, aabb);
		if (list.isEmpty()) return WorldTarget.EMPTY;
		T find = list.get(rand.nextInt(list.size()));
		return new WorldTarget(find, find.getPositionVector().add(0, find.height, 0));

	}

	@Override
	public Vec3d iWantDirection() {
		IWorldObject user = this.getUser();
		if (user != null) {
			Entity entityUser = user.asEntity();
			if (entityUser != null) return entityUser.getLookVec();
		}
		return new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).normalize();
	}

	@Override
	public DamageSource iWantDamageSource(ElementStack eStack) {
		IWorldObject user = this.getUser();
		EntityLivingBase src = user == null ? null : user.asEntityLivingBase();
		return DamageHelper.getDamageSource(eStack, src == null ? this : src, this);
	}

	@Override
	public IWorldObject iWantCaster() {
		IWorldObject user = this.getUser();
		return user == null ? this : user;
	}

	@Override
	public ICasterObject iWantDirectCaster() {
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
	public TileEntity asTileEntity() {
		return null;
	}

	@Override
	public Entity asEntity() {
		return this;
	}

	@Override
	public Vec3d getObjectPosition() {
		return this.getPositionVector();
	}

	/** 当死亡 */
	protected void onDead() {
		this.state = CastStatus.END;
	}

	public abstract IElementInventory getElementInventory();

	/** 是否可以继续施法 */
	public abstract boolean canContinueSpelling();

	protected void onStartSpelling() {
		mantra.startSpelling(world, mantraData, this);
	}

	protected void onSpelling() {
		mantra.onSpelling(world, mantraData, this);
	}

	protected void onEndSpelling() {
		mantra.endSpelling(world, mantraData, this);
	}

	protected void onAfterSpelling() {
		boolean flag = mantra.afterSpelling(world, mantraData, this);
		if (!flag) this.state = CastStatus.END;
	}

	@Nullable
	protected abstract IWorldObject getUser();

	/** 还原真正的catester */
	protected abstract void restoreUser();

	protected abstract void onLockUser();
}
