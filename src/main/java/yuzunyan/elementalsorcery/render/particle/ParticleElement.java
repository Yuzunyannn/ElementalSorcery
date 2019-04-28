package yuzunyan.elementalsorcery.render.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleSimpleAnimated;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleElement extends ParticleSimpleAnimated {

	static public final float g = -0.025f;
	static public final double at_high_speed = 0.05;

	private boolean main = false;
	private boolean upping = true;
	private final ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;

	private Vec3d ori;
	private Vec3d tar;
	private double r;
	private double theta = 0;
	private double dtheta = 0;

	public ParticleElement(World world, Vec3d from, Vec3d to) {
		super(world, from.x, from.y, from.z, 160, 8, -0.004F);
		this.main = true;
		this.particleScale *= 0.75F;
		this.particleMaxAge = 48 + this.rand.nextInt(12);
		this.motionX = 0;
		this.motionZ = 0;
		this.motionY = 0;
		if (from.y > to.y) {
			this.main = false;
		} else {
			this.setParticleTextureIndex(160);
			double high = to.y - from.y;
			this.motionY = Math.sqrt(at_high_speed * at_high_speed - 2 * g * high);

			tar = to.subtract(from);
			tar = new Vec3d(tar.x, 0, tar.z);
			r = tar.lengthVector() / 2;

			ori = new Vec3d(from.x, to.y, from.z);
			this.particleAge = 0;
		}
	}

	private ParticleElement(ParticleElement particle) {
		super(particle.world, particle.posX, particle.posY, particle.posZ, 160, 8, -0.004F);
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.particleScale *= 0.75F;
		this.particleMaxAge = 48 + this.rand.nextInt(12);
	}

	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX,
			float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}

	public void onUpdate() {
		if (main)
			this.mainUpdate();
		else
			super.onUpdate();
		boolean spawn = main ? (this.particleAge % 2 == 0)
				: (this.particleAge < this.particleMaxAge / 2 && (this.particleAge + this.particleMaxAge) % 2 == 0);
		if (spawn) {
			ParticleElement particlefirework$spark = new ParticleElement(this);
			particlefirework$spark.setAlphaF(0.99F);
			particlefirework$spark.setRBGColorF(this.particleRed, this.particleGreen, this.particleBlue);
			particlefirework$spark.particleAge = particlefirework$spark.particleMaxAge / 2;
			this.effectRenderer.addEffect(particlefirework$spark);
		}

	}

	public void mainUpdate() {

		if (this.particleAge % 4 == 0)
			this.particleAge = 0;
		this.particleAge++;

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;

		if (this.upping) {
			this.motionY += g;
			if (this.motionY <= at_high_speed) {
				this.upping = false;
				this.dtheta = at_high_speed / r;
			}
		} else {
			this.theta += this.dtheta;
			this.dtheta += 0.0025f;
			this.posY = MathHelper.sin((float) this.theta) * r + ori.y;
			double factor = (r - MathHelper.cos((float) this.theta) * r) / (r * 2);
			Vec3d pos = this.ori.add(this.tar.scale(factor));
			this.posX = pos.x;
			this.posZ = pos.z;
			if (this.theta >= Math.PI) {
				this.setExpired();
			}
		}

	}
}
