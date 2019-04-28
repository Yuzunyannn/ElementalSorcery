package yuzunyan.elementalsorcery.render.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleSpellbook extends Particle {

	Entity entity = null;
	// 引力常数
	public static final double G = 0.0025f;

	public ParticleSpellbook(World worldIn, Vec3d position, Vec3d speed, Entity entity) {
		super(worldIn, position.x, position.y, position.z, speed.x, speed.y, speed.z);
		this.setParticleTextureIndex((int) (Math.random() * 26.0D + 1.0D + 224.0D));
		this.particleMaxAge = (int) (Math.random() * 15.0D) + 40;
		this.posX = this.prevPosX;
		this.posY = this.prevPosY;
		this.posZ = this.prevPosZ;
		this.entity = entity;
		this.canCollide = false;
	}

	public void onUpdate() {
		if (this.particleAge++ >= this.particleMaxAge)
			this.setExpired();
		// 上一次的坐标
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		// 计算速度
		Vec3d at = entity.getPositionVector().addVector(0, 1.0, 0);
		Vec3d distance = at.subtract(posX, posY, posZ);
		double F = G * distance.lengthSquared();
		Vec3d a = distance.normalize().scale(F);
		this.motionX += a.x;
		this.motionY += a.y;
		this.motionZ += a.z;
		// 这次的坐标
		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
	}

	public void setColor(int color) {
		this.setColor(new Vec3d(((color >> 16) & 0xff) / 255.0, ((color >> 8) & 0xff) / 255.0,
				((color >> 0) & 0xff) / 255.0));
	}

	public void setColor(Vec3d color) {
		this.setRBGColorF((float) color.x, (float) color.y, (float) color.z);
	}

}
