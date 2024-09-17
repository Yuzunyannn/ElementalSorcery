package yuzunyannn.elementalsorcery.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.ESStorageKeyEnum;
import yuzunyannn.elementalsorcery.api.entity.IHasMaster;
import yuzunyannn.elementalsorcery.api.mantra.CastStatus;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.mantra.MantraCasterFlags;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.target.WorldObjectBlock;
import yuzunyannn.elementalsorcery.api.util.target.WorldObjectEntity;
import yuzunyannn.elementalsorcery.api.util.target.WorldTarget;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.grimoire.mantra.IMantraProgressable;
import yuzunyannn.elementalsorcery.render.effect.IEffectBinder;
import yuzunyannn.elementalsorcery.render.effect.IEffectBinder.IEffectBinderGetter;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTSaver;
import yuzunyannn.elementalsorcery.util.world.CasterHelper;
import yuzunyannn.elementalsorcery.util.world.EntityMasterBinder;
import yuzunyannn.elementalsorcery.util.world.SuperMasterBinder;

public class EntityAutoMantra extends EntityMantraBase implements IEffectBinderGetter, IHasMaster {

	/**
	 * @author yuzun
	 *
	 */
	public static class AutoMantraConfig implements INBTSerializable<NBTTagCompound> {

		public static final int DIRECTTRACK_MOVE = 0;
		public static final int DIRECTTRACK_FOLLOW = 1;

		public static final int BLOCKTRACK_DIRECT = 0;
		public static final int BLOCKTRACK_DIRECT_REVERSE = 1;
		public static final int BLOCKTRACK_UNDERFOOT = 2;
		public static final int BLOCKTRACK_RAMDOM = 3;

		public static final int ENTITYTRACK_DIRECT = 0;
		public static final int ENTITYTRACK_RADNOM_ENEMY = 1;
		public static final int ENTITYTRACK_RADNOM_FRIEND = 2;

		public int directTrack = DIRECTTRACK_MOVE;
		public int blockTrack = BLOCKTRACK_DIRECT;
		public int entityTrack = ENTITYTRACK_DIRECT;

		public EntityMasterBinder target = new EntityMasterBinder().setDataKey("t");
		public Vec3d moveVec = Vec3d.ZERO;
		public float randomRange = 4;
		public boolean excludeUser = true;
		public boolean userElement = false;

		public void setTarget(EntityLivingBase target, double speed) {
			this.target.setMaster(target);
			moveVec = new Vec3d(0, speed, 0);
			this.directTrack = DIRECTTRACK_FOLLOW;
		}

		public void setMoveVec(Vec3d moveVec) {
			this.moveVec = moveVec;
			this.directTrack = DIRECTTRACK_MOVE;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTSaver saver = new NBTSaver();
			if (directTrack != 0) saver.write("d", (byte) directTrack);
			if (blockTrack != 0) saver.write("b", (byte) blockTrack);
			if (entityTrack != 0) saver.write("e", (byte) entityTrack);
			if (randomRange != 0) saver.write("r", randomRange);
			if (excludeUser) saver.write("eu", excludeUser);
			saver.write("m", moveVec);
			if (directTrack == DIRECTTRACK_FOLLOW || blockTrack == BLOCKTRACK_UNDERFOOT)
				target.writeDataToNBT(saver.tag());
			if (userElement) saver.write("ue", userElement);
			return saver.tag();
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			NBTSaver saver = new NBTSaver(nbt);
			directTrack = saver.nint("d");
			blockTrack = saver.nint("b");
			entityTrack = saver.nint("e");
			target.readDataFromNBT(nbt);
			moveVec = saver.vec3d("m");
			randomRange = saver.nfloat("r");
			excludeUser = saver.nboolean("eu");
			userElement = saver.nboolean("ue");
		}

	}

	public interface IMantraElementSupplier {
		IElementInventory supplyElementInventory();
	}

	public EntityAutoMantra(World worldIn) {
		super(worldIn);
	}

	public EntityAutoMantra(World worldIn, AutoMantraConfig config, EntityLivingBase user, Mantra mantra,
			NBTTagCompound metaData) {
		this(worldIn, config, new WorldObjectEntity(user), mantra, metaData);
	}

	public EntityAutoMantra(World worldIn, AutoMantraConfig config, TileEntity user, Mantra mantra,
			NBTTagCompound metaData) {
		this(worldIn, config, new WorldObjectBlock(user), mantra, metaData);
	}

	public EntityAutoMantra(World worldIn, AutoMantraConfig config, EntityLivingBase user, Mantra mantra,
			NBTTagCompound metaData, CastStatus state) {
		this(worldIn, config, new WorldObjectEntity(user), mantra, metaData, state);
	}

	public EntityAutoMantra(World worldIn, AutoMantraConfig config, IWorldObject user, Mantra mantra,
			NBTTagCompound metaData) {
		this(worldIn, config, user, mantra, metaData, CastStatus.BEFORE_SPELLING);
	}

	// 这个构造方法是给予某些不通过魔法书进行处理的
	public EntityAutoMantra(World worldIn, AutoMantraConfig config, @Nullable IWorldObject user, Mantra mantra,
			NBTTagCompound metaData, CastStatus state) {
		super(worldIn, mantra, metaData, state);
		this.config = config;
		this.user.setMaster(user);
	}

	protected SuperMasterBinder user = new SuperMasterBinder();
	protected IElementInventory elementInv = new ElementInventory(4);
	/** 强效 */
	protected float potent = 0;
	public float potentPoint;
	/** 持续市场 */
	public int spellingTick = 0;
	/** 配置 */
	public AutoMantraConfig config = new AutoMantraConfig();
	public Vec3d orient = new Vec3d(0, 1, 0);
	public boolean byLoadedFlag;

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		user.readDataFromNBT(nbt);
		this.potent = nbt.getFloat("potent");
		this.potentPoint = nbt.getFloat("potentPoint");
		this.config.deserializeNBT(nbt.getCompoundTag("_config"));
		orient = NBTHelper.getVec3d(nbt, "orient");
		spellingTick = nbt.getInteger("sTick");
		if (!config.userElement) elementInv.deserializeNBT(nbt.getCompoundTag(ESStorageKeyEnum.ELEMENT_INV));
	}

	protected void writeEntityToNBT(NBTTagCompound nbt, boolean isSend) {
		super.writeEntityToNBT(nbt, isSend);
		user.writeDataToNBT(nbt);
		nbt.setFloat("potent", potent);
		nbt.setFloat("potentPoint", potentPoint);
		nbt.setTag("_config", config.serializeNBT());
		nbt.setInteger("sTick", spellingTick);
		NBTHelper.setVec3d(nbt, "orient", orient);
		if (!config.userElement) nbt.setTag(ESStorageKeyEnum.ELEMENT_INV, elementInv.serializeNBT());
	}

	public void setSpellingTick(int spellingTick) {
		this.spellingTick = spellingTick;
	}

	public void setOrient(Vec3d orient) {
		this.orient = orient;
	}

	@Override
	protected void restoreUser() {
		user.restoreMaster(world);
	}

	@Override
	protected IWorldObject getUser() {
		IWorldObject master = user.getMaster();
		return master == null ? new WorldObjectEntity(this) : master;
	}

	@Override
	protected boolean restore() {
		IWorldObject caster = user.getMaster();
		if (caster == null) this.restoreUser();
		return true;
	}

	@Override
	protected void onLockUser() {
		IWorldObject master = user.getMaster();
		if (master != null) {
			Entity entity = master.toEntity();
			if (entity != null) entity.timeUntilPortal = 20;
		}
		this.timeUntilPortal = 20;
	}

	protected void onSpelling() {
		super.onSpelling();
	}

	protected void onEndSpelling() {
		super.onEndSpelling();
	}

	public double moveScale = 0;

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();

		if (state != CastStatus.SPELLING) return;

		if (this.spellingTick > 0) this.spellingTick--;

		if (this.config.directTrack == AutoMantraConfig.DIRECTTRACK_MOVE)
			this.orient = this.orient.add(this.config.moveVec).normalize();
		else {
			if (moveScale == 0) moveScale = this.config.moveVec.length();
			EntityLivingBase target = this.config.target.tryGetMaster(world);
			if (target != null) {
				Vec3d t = target.getPositionVector().add(0, target.height * 0.85, 0).subtract(this.getPositionVector());
				Vec3d dt = t.subtract(orient).scale(moveScale);
				this.orient = this.orient.add(dt).normalize();
			}
		}
	}

	@Override
	public boolean canContinueSpelling() {
		if (!config.userElement) {
			if (tick % 20 == 0) {
				if (elementInv.isEmpty()) return false;
			}
		} else {
			if (tick % 20 == 0) {
				IWorldObject master = user.toughGetMaster();
				if (master == null) return false;
			}
		}
		return this.spellingTick > 0;
	}

	@Override
	public IElementInventory getElementInventory() {
		if (config.userElement) {
			IWorldObject master = user.getMaster();
			if (master == null) return this.elementInv;
			IMantraElementSupplier supplier = master.to(IMantraElementSupplier.class);
			if (supplier != null) return supplier.supplyElementInventory();
		}
		return this.elementInv;
	}

	public void addPotentPoint(float point) {
		this.potentPoint = this.potentPoint + point;
		if (this.potentPoint <= 0) {
			this.potentPoint = 0;
			this.potent = 0;
		}
	}

	@Override
	public void iWantGivePotent(float potent, float point) {
		if (this.potent == 0) potentPoint = 0;
		this.potent = (potent * point + this.potent * potentPoint) / (point + potentPoint);
		addPotentPoint(point);
	}

	@Override
	public float iWantBePotent(float point, boolean justTry) {
		float rPoint = Math.min(potentPoint, point);
		if (potentPoint <= 0) return 0;
		float potent = this.potent * (rPoint / point);
		if (justTry) return potent;
		addPotentPoint(-rPoint);
		return potent;
	}

	@Override
	public Vec3d iWantDirection() {
		return orient;
	}

	@Override
	public WorldTarget iWantBlockTarget() {
		if (this.config.blockTrack == AutoMantraConfig.BLOCKTRACK_DIRECT) {
			EntityHelper.setLookOrient(this, this.orient);
			return CasterHelper.findLookBlockResult(this, 128, false);
		} else if (this.config.blockTrack == AutoMantraConfig.BLOCKTRACK_DIRECT_REVERSE) {
			EntityHelper.setLookOrient(this, this.orient.scale(-1));
			return CasterHelper.findLookBlockResult(this, 128, false);
		}
		throw new UnsupportedOperationException(this.config.blockTrack + "模式还未实现");
	}

	@Override
	public <T extends Entity> WorldTarget iWantEntityTarget(Class<T> cls) {
		if (this.config.entityTrack == AutoMantraConfig.ENTITYTRACK_DIRECT) {
			EntityHelper.setLookOrient(this, this.orient);
			WorldTarget worldTarget = CasterHelper.findLookTargetResult(e -> {
				if (this.config.excludeUser && user.is(e)) return false;
				return cls.isAssignableFrom(e.getClass());
			}, this, 128);
			return worldTarget;
		}
		throw new UnsupportedOperationException(this.config.entityTrack + "模式还未实现");
	}

	@Override
	public IWorldObject iWantCaster() {
		return this;
	}

	@Override
	public IWorldObject iWantRealCaster() {
		IWorldObject user = this.getUser();
		return user == null ? this : user;
	}

	@Override
	public Object getCasterFlag(int flagType) {
		switch (flagType) {
		case MantraCasterFlags.AUTO_MODE:
			return true;
		}
		return null;
	}

	@Override
	public IEffectBinder getEffectBinder() {
		return new IEffectBinder() {
			@Override
			public Vec3d getPosition() {
				return EntityAutoMantra.this.getPositionVector();
			}

			@Override
			public Vec3d getDirection() {
				return orient;
			}

			@Override
			public IEffectBinder fixToSpell() {
				return this;
			}
		};
	}

	@Override
	public EntityLivingBase getMaster() {
		IWorldObject master = user.getMaster();
		if (master == null) return null;
		return master.toEntityLiving();
	}

	@Override
	public boolean isOwnerless() {
		if (!user.is(Entity.class)) return true;
		return user.isOwnerless();
	}

	public double tryGetMantraProgress() {
		if (state != CastStatus.BEFORE_SPELLING && state != CastStatus.SPELLING) return 1;
		if (mantra instanceof IMantraProgressable)
			return ((IMantraProgressable) mantra).getProgressRate(world, mantraData, this);
		return -1;
	}

	public int tryGetMantraColor() {
		return mantra.getColor(mantraData);
	}
}
