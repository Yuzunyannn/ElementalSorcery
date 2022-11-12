package yuzunyannn.elementalsorcery.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.entity.IHasMaster;
import yuzunyannn.elementalsorcery.api.mantra.CastStatus;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.mantra.MantraCasterFlags;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.WorldObjectEntity;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.render.effect.IEffectBinder;
import yuzunyannn.elementalsorcery.render.effect.IEffectBinder.IEffectBinderGetter;
import yuzunyannn.elementalsorcery.util.MasterBinder;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.world.CasterHelper;

public class EntityAutoMantra extends EntityMantraBase implements IEffectBinderGetter, IHasMaster {

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

		public MasterBinder target = new MasterBinder().setDataKey("t");
		public Vec3d moveVec = Vec3d.ZERO;
		public float randomRange = 4;
		public boolean excludeUser = true;

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
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setByte("d", (byte) directTrack);
			nbt.setByte("b", (byte) blockTrack);
			nbt.setByte("e", (byte) entityTrack);
			nbt.setFloat("r", randomRange);
			nbt.setBoolean("eu", excludeUser);
			NBTHelper.setVec3d(nbt, "m", moveVec);
			if (directTrack == DIRECTTRACK_FOLLOW || blockTrack == BLOCKTRACK_UNDERFOOT) target.writeEntityToNBT(nbt);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			directTrack = nbt.getInteger("d");
			blockTrack = nbt.getInteger("b");
			entityTrack = nbt.getInteger("e");
			target.readEntityFromNBT(nbt);
			moveVec = NBTHelper.getVec3d(nbt, "m");
			randomRange = nbt.getFloat("r");
			excludeUser = nbt.getBoolean("eu");
		}

	}

	public EntityAutoMantra(World worldIn) {
		super(worldIn);
	}

	public EntityAutoMantra(World worldIn, AutoMantraConfig config, EntityLivingBase user, Mantra mantra,
			NBTTagCompound metaData) {
		this(worldIn, config, user, mantra, metaData, CastStatus.BEFORE_SPELLING);
	}

	// 这个构造方法是给予某些不通过魔法书进行处理的
	public EntityAutoMantra(World worldIn, AutoMantraConfig config, @Nullable EntityLivingBase user, Mantra mantra,
			NBTTagCompound metaData, CastStatus state) {
		super(worldIn, mantra, metaData, state);
		this.config = config;
		this.user.setMaster(user);
	}

	protected MasterBinder user = new MasterBinder();
	protected IElementInventory elementInv = new ElementInventory(4);
	/** 强效 */
	protected float potent = 0;
	public float potentPoint;
	/** 持续市场 */
	public int spellingTick = 0;
	/** 配置 */
	public AutoMantraConfig config = new AutoMantraConfig();
	public Vec3d orient = new Vec3d(0, 1, 0);

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		user.readEntityFromNBT(nbt);
		elementInv.loadState(nbt);
		this.potent = nbt.getFloat("potent");
		this.potentPoint = nbt.getFloat("potentPoint");
		this.config.deserializeNBT(nbt.getCompoundTag("_config"));
		orient = NBTHelper.getVec3d(nbt, "orient");
		spellingTick = nbt.getInteger("sTick");
	}

	protected void writeEntityToNBT(NBTTagCompound nbt, boolean isSend) {
		super.writeEntityToNBT(nbt, isSend);
		user.writeEntityToNBT(nbt);
		elementInv.saveState(nbt);
		nbt.setFloat("potent", potent);
		nbt.setFloat("potentPoint", potentPoint);
		nbt.setTag("_config", config.serializeNBT());
		nbt.setInteger("sTick", spellingTick);
		NBTHelper.setVec3d(nbt, "orient", orient);
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
		return user.getMaster() == null ? null : new WorldObjectEntity(user.getMaster());
	}

	@Override
	protected void onLockUser() {
		EntityLivingBase master = user.getMaster();
		if (master != null) master.timeUntilPortal = 20;
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
		if (this.spellingTick > 0) this.spellingTick--;

		if (this.config.directTrack == AutoMantraConfig.DIRECTTRACK_MOVE)
			this.orient = this.orient.add(this.config.moveVec).normalize();
		else {
			if (moveScale == 0) moveScale = this.config.moveVec.length();
			EntityLivingBase target = this.config.target.tryGetMaster(world);
			if (target != null) {
				Vec3d t = target.getPositionEyes(0).subtract(this.getPositionVector());
				Vec3d dt = t.subtract(orient).scale(moveScale);
				this.orient = this.orient.add(dt).normalize();
			}
		}
	}

	@Override
	public boolean canContinueSpelling() {
		if (tick % 20 == 0) if (ElementHelper.isEmpty(elementInv)) return false;
		return this.spellingTick > 0;
	}

	@Override
	public IElementInventory getElementInventory() {
		return this.elementInv;
	}

	@Override
	public void iWantGivePotent(float potent, float point) {
		this.potentPoint = this.potentPoint + point;
		if (this.potentPoint <= 0) {
			this.potentPoint = 0;
			this.potent = 0;
		}
	}

	@Override
	public float iWantBePotent(float point, boolean justTry) {
		float rPoint = Math.min(potentPoint, point);
		if (potentPoint <= 0) return 0;
		float potent = this.potent * (rPoint / point);
		if (justTry) return potent;
		iWantGivePotent(potent, -rPoint);
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
				if (this.config.excludeUser && e.getUniqueID().equals(user.getUUID())) return false;
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
		return user.getMaster();
	}

	@Override
	public boolean isOwnerless() {
		return user.isOwnerless();
	}

}
