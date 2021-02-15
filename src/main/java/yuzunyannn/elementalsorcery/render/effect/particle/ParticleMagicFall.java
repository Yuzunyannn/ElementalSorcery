package yuzunyannn.elementalsorcery.render.effect.particle;

import java.util.List;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.ColorHelper;

@SideOnly(Side.CLIENT)
public class ParticleMagicFall extends Particle {

	private float fadeTargetRed;
	private float fadeTargetGreen;
	private float fadeTargetBlue;

	public ParticleMagicFall(World worldIn, Vec3d position) {
		super(worldIn, position.x, position.y, position.z, 0, 0, 0);
		this.particleMaxAge = 50;
		this.particleScale = rand.nextFloat() * 0.25f + 0.75f;
		this.motionY = rand.nextFloat() * 0.5f;
		this.motionX = rand.nextGaussian() * 0.175f;
		this.motionZ = rand.nextGaussian() * 0.175f;
	}

	public void setColor(int color) {
		Vec3d c = ColorHelper.color(color);
		this.setRBGColorF((float) c.x, (float) c.y, (float) c.z);
	}

	public void setColorFade(int color) {
		Vec3d c = ColorHelper.color(color);
		fadeTargetRed = (float) c.x;
		fadeTargetGreen = (float) c.y;
		fadeTargetBlue = (float) c.z;
	}

	public void setMotionH(double x, double z) {
		this.motionX = x;
		this.motionZ = z;
	}

	public void setMotionY(double y) {
		this.motionY = y;
	}

	@Override
	public boolean shouldDisableDepth() {
		return true;
	}

	@Override
	public int getBrightnessForRender(float partialTicks) {
		return 15728880;
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.setParticleTextureIndex(160 + (8 - 1 - this.particleAge * 8 / this.particleMaxAge));
		if (this.particleAge++ >= this.particleMaxAge) this.setExpired();

		if (this.particleAge > this.particleMaxAge / 2) {
			this.setAlphaF(1.0F
					- ((float) this.particleAge - (float) (this.particleMaxAge / 2)) / (float) this.particleMaxAge);
			this.particleRed += (this.fadeTargetRed - this.particleRed) * 0.2F;
			this.particleGreen += (this.fadeTargetGreen - this.particleGreen) * 0.2F;
			this.particleBlue += (this.fadeTargetBlue - this.particleBlue) * 0.2F;
		}

		this.motionY = this.motionY - 0.1f;

		this.move();

		this.motionY *= 0.975;
		this.motionX *= 0.925;
		this.motionZ *= 0.925;

	}

	public void move() {

		double originX = this.motionX;
		double originY = this.motionY;
		double originZ = this.motionZ;
		double x = originX;
		double y = originY;
		double z = originZ;

		List<AxisAlignedBB> list = this.world.getCollisionBoxes(null, this.getBoundingBox().expand(x, y, z));

		for (AxisAlignedBB axisalignedbb : list) y = axisalignedbb.calculateYOffset(this.getBoundingBox(), y);
		this.setBoundingBox(this.getBoundingBox().offset(0.0D, y, 0.0D));
		for (AxisAlignedBB axisalignedbb1 : list) x = axisalignedbb1.calculateXOffset(this.getBoundingBox(), x);
		this.setBoundingBox(this.getBoundingBox().offset(x, 0.0D, 0.0D));
		for (AxisAlignedBB axisalignedbb2 : list) z = axisalignedbb2.calculateZOffset(this.getBoundingBox(), z);
		this.setBoundingBox(this.getBoundingBox().offset(0.0D, 0.0D, z));

		this.resetPositionToBB();

		if (originY != y) {
			this.motionY = -this.motionY;
		}

		if (originX != x) {
			this.motionX = -this.motionX;
		}

		if (originZ != z) {
			this.motionZ = -this.motionZ;
		}
	}
}
