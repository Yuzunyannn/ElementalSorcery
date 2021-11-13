package yuzunyannn.elementalsorcery.render.effect.particle;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleWaterBubble extends net.minecraft.client.particle.ParticleBubble {

	public double yAccelerate = 0.002f;
	public double yDecay = 0.8500000238418579d;

	public ParticleWaterBubble(World worldIn, Vec3d pos, Vec3d speed) {
		super(worldIn, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
		this.particleMaxAge = (int) (10.0D / (Math.random() * 0.8D + 0.2D));
	}

	public void setScale(float scale) {
		this.particleScale = scale;
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY += yAccelerate;
		this.move(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.8500000238418579D;
		this.motionY *= yDecay;
		this.motionZ *= 0.8500000238418579D;

		if (this.particleMaxAge < 20) particleAlpha = this.particleMaxAge / 20f;
		if (this.particleMaxAge-- <= 0) this.setExpired();
	}

}
