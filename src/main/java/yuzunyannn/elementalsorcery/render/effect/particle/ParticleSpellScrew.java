package yuzunyannn.elementalsorcery.render.effect.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleSpellScrew extends Particle {

	public ParticleSpellScrew(World worldIn, Vec3d from) {
		super(worldIn, from.x, from.y, from.z);
		this.setParticleTextureIndex((int) (Math.random() * 26.0D + 1.0D + 224.0D));
		this.motionX = 0;
		this.motionY = 0.1;
		this.motionZ = 0;
		this.particleScale = 2;
		this.particleAlpha = 1;
	}

	public void setSpeedH(Vec3d vec) {
		this.motionX = vec.x;
		this.motionZ = vec.z;
	}

	public void onUpdate() {
		// 上一次的
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.prevParticleAngle = this.particleAngle;
		// 这次的坐标
		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		// 阻力
		this.motionY *= 0.925;
		this.particleScale -= 0.04f;
		this.particleAngle += 3.14f / 100;
		if (this.particleScale <= 0) this.setExpired();
	}

	public int getBrightnessForRender(float partialTick) {
		return 15728640;
	}

}
