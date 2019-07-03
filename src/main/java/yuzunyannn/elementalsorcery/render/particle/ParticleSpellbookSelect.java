package yuzunyannn.elementalsorcery.render.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class ParticleSpellbookSelect extends Particle {

	BlockPos pos = null;
	Entity entity = null;

	float yaw = 0;
	float pitch = 0;

	static final float dyaw = 0.2513274f * 2;
	static final float dpitch = 0.06283f * 2;

	double xoff = 0;
	double yoff = 0;
	double zoff = 0;

	public ParticleSpellbookSelect(World worldIn, BlockPos pos) {
		super(worldIn, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 0, 0, 0);
		this.setParticleTextureIndex((int) (Math.random() * 26.0D + 1.0D + 224.0D));
		this.particleMaxAge = 50;
		this.canCollide = false;
		this.posX = this.prevPosX;
		this.posY = this.prevPosY;
		this.posZ = this.prevPosZ;
		this.pos = pos;
		this.xoff = pos.getX() + 0.5f;
		this.yoff = pos.getY() + 0.5f;
		this.zoff = pos.getZ() + 0.5f;
	}

	public ParticleSpellbookSelect(World worldIn, Entity entity) {
		super(worldIn, entity.posX, entity.posY, entity.posZ, 0, 0, 0);
		this.setParticleTextureIndex((int) (Math.random() * 26.0D + 1.0D + 224.0D));
		this.particleMaxAge = 50 * 2;
		this.canCollide = false;
		this.posX = this.prevPosX;
		this.posY = this.prevPosY;
		this.posZ = this.prevPosZ;
		this.entity = entity;
	}

	public void onUpdate() {
		if (this.particleAge++ >= this.particleMaxAge)
			this.setExpired();
		// 上一次的坐标
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		// 偏移坐标
		if (entity != null) {
			this.xoff = entity.posX;
			this.yoff = entity.posY + 1.0;
			this.zoff = entity.posZ;
		}
		// 这次的坐标
		this.yaw += dyaw;
		this.pitch += dpitch;
		this.posX = MathHelper.cos(this.yaw) * 0.75f + xoff;
		this.posY = MathHelper.sin(this.pitch) * 0.65f + yoff;
		this.posZ = MathHelper.sin(this.yaw) * 0.75f + zoff;
	}

	public void setColor(int color) {
		this.setColor(new Vec3d(((color >> 16) & 0xff) / 255.0, ((color >> 8) & 0xff) / 255.0,
				((color >> 0) & 0xff) / 255.0));
	}

	public void setColor(Vec3d color) {
		this.setRBGColorF((float) color.x, (float) color.y, (float) color.z);
	}
}
