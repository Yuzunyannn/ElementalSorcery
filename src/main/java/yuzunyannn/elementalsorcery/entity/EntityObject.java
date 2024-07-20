package yuzunyannn.elementalsorcery.entity;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.GameCast;
import yuzunyannn.elementalsorcery.api.util.ICastEnv;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;

public abstract class EntityObject extends Entity implements IWorldObject {

	protected int newPosRotationIncrements;
	protected double interpTargetX;
	protected double interpTargetY;
	protected double interpTargetZ;
	protected double interpTargetYaw;
	protected double interpTargetPitch;

	public EntityObject(World worldIn) {
		super(worldIn);
	}

	@Override
	public void onEntityUpdate() {

		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.prevRotationPitch = this.rotationPitch;
		this.prevRotationYaw = this.rotationYaw;

		if (this.newPosRotationIncrements > 0) {
			double d0 = this.posX + (this.interpTargetX - this.posX) / (double) this.newPosRotationIncrements;
			double d1 = this.posY + (this.interpTargetY - this.posY) / (double) this.newPosRotationIncrements;
			double d2 = this.posZ + (this.interpTargetZ - this.posZ) / (double) this.newPosRotationIncrements;
			double d3 = MathHelper.wrapDegrees(this.interpTargetYaw - (double) this.rotationYaw);
			this.rotationYaw = (float) ((double) this.rotationYaw + d3 / (double) this.newPosRotationIncrements);
			this.rotationPitch = (float) ((double) this.rotationPitch + (this.interpTargetPitch - (double) this.rotationPitch) / (double) this.newPosRotationIncrements);
			--this.newPosRotationIncrements;
			this.setPosition(d0, d1, d2);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		}
	}

	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch,
			int posRotationIncrements, boolean teleport) {
		this.interpTargetX = x;
		this.interpTargetY = y;
		this.interpTargetZ = z;
		this.interpTargetYaw = (double) yaw;
		this.interpTargetPitch = (double) pitch;
		this.newPosRotationIncrements = posRotationIncrements;
	}

	@Override
	public World getWorld() {
		return world;
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
	public Vec3d getObjectPosition() {
		return this.getPositionVector();
	}

	@Override
	public <T> T to(Class<T> cls) {
		if (CapabilityObjectRef.class == cls) return (T) toRef();
		return GameCast.cast(ICastEnv.EMPTY, this, cls);
	}

	@Override
	public CapabilityObjectRef toRef() {
		return CapabilityObjectRef.of(this);
	}

	@Override
	public boolean isAlive() {
		return !isDead;
	}

}
