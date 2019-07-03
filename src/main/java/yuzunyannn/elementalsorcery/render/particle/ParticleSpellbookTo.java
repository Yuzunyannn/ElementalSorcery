package yuzunyannn.elementalsorcery.render.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleSpellbookTo extends Particle {

	Vec3d to;

	public ParticleSpellbookTo(World worldIn, Vec3d from, Vec3d to) {
		super(worldIn, from.x, from.y, from.z);
		this.setParticleTextureIndex((int) (Math.random() * 26.0D + 1.0D + 224.0D));
		this.to = to;
		this.motionX = (Math.random() - 1.0D) * 0.5;
		this.motionY = (Math.random() - 1.0D) * 0.5;
		this.motionZ = (Math.random() - 1.0D) * 0.5;
		this.particleMaxAge = 12 + this.rand.nextInt(12);
	}

	public void onUpdate() {
		// 上一次的坐标
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		// 这次的坐标
		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		// 移动
		Vec3d oto = new Vec3d(to.x, to.y, to.z);
		Vec3d tar = oto.subtract(this.posX, this.posY, this.posZ);
		double dis = tar.lengthSquared();
		// 结束
		if (dis <= 1) {
			this.setExpired();
			if (Math.random() < 0.34) {
				BlockPos pos = new BlockPos(to.x, to.y, to.z);
				ParticleSpellbookSelect effect = new ParticleSpellbookSelect(world, pos);
				effect.setRBGColorF(this.getRedColorF(), this.getGreenColorF(), this.getBlueColorF());
				Minecraft.getMinecraft().effectRenderer.addEffect(effect);
			}
		}
		// 引力
		if (dis != 0) {
			dis = MathHelper.clamp(dis, 3, 10);
			double F = 0.1 / dis;
			Vec3d a = tar.scale(F);
			this.motionX += a.x;
			this.motionY += a.y;
			this.motionZ += a.z;
		}
		// 阻力
		this.motionX *= 0.925;
		this.motionY *= 0.925;
		this.motionZ *= 0.925;

	}
}
