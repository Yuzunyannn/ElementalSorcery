package yuzunyannn.elementalsorcery.render.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleSimpleAnimated;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventClient;

@SideOnly(Side.CLIENT)
public class FirewrokShap {

	public static final ParticleManager manager = Minecraft.getMinecraft().effectRenderer;

	/** 球形 */
	public static void createBall(World world, Vec3d position, double speed, int size, int[] colors, int[] fadeColours,
			boolean trail, boolean twinkle) {

		for (int i = -size; i <= size; ++i) {
			for (int j = -size; j <= size; ++j) {
				for (int k = -size; k <= size; ++k) {
					double d3 = (double) j + (EventClient.rand.nextDouble() - EventClient.rand.nextDouble()) * 0.5D;
					double d4 = (double) i + (EventClient.rand.nextDouble() - EventClient.rand.nextDouble()) * 0.5D;
					double d5 = (double) k + (EventClient.rand.nextDouble() - EventClient.rand.nextDouble()) * 0.5D;
					double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5) / speed
							+ EventClient.rand.nextGaussian() * 0.05D;
					createSpark(world, position.x, position.y, position.z, d3 / d6, d4 / d6, d5 / d6, colors,
							fadeColours, trail, twinkle);
					if (i != -size && i != size && j != -size && j != size) {
						k += size * 2 - 1;
					}
				}
			}
		}

	}

	/** 环型 */
	public static void createCircle(World world, Vec3d position, double speed, int size, int[] colours,
			int[] fadeColours, boolean trail, boolean twinkleIn) {

		for (int i = -size; i <= size; ++i) {
			for (int j = -size; j <= size; ++j) {
				double d1 = (double) i + (EventClient.rand.nextDouble() - EventClient.rand.nextDouble()) * 0.5D;
				double d2 = (double) j + (EventClient.rand.nextDouble() - EventClient.rand.nextDouble()) * 0.5D;
				double d3 = (double) MathHelper.sqrt(d1 * d1 + d2 * d2) / speed
						+ EventClient.rand.nextGaussian() * 0.05D;
				createSpark(world, position.x, position.y, position.z, d1 / d3, 0, d2 / d3, colours, fadeColours, trail,
						twinkleIn);
			}
		}

	}

	/** 单门一个 */
	public static void createSpark(World world, double x, double y, double z, double speedX, double speedY,
			double speedZ, int[] colors, int[] fadeoutColors, boolean trail, boolean flicker) {
		ParticleFirework.Spark spark = new ParticleFirework.Spark(world, x, y, z, speedX, speedY, speedZ, manager);
		spark.setAlphaF(0.99F);
		spark.setTrail(trail);
		spark.setTwinkle(flicker);
		spark.setColor(colors[EventClient.rand.nextInt(colors.length)]);
		if (fadeoutColors != null && fadeoutColors.length > 0)
			spark.setColorFade(fadeoutColors[EventClient.rand.nextInt(fadeoutColors.length)]);
		manager.addEffect(spark);
	}

	/** 单门一个 */
	public static void createSparkUniformlySpeed(World world, double x, double y, double z, double toX, double toY,
			double toZ, double speed, int[] colors, int[] fadeoutColors, boolean trail, boolean flicker) {
		Vec3d vec = new Vec3d(toX - x, toY - y, toZ - z);
		int age = MathHelper.ceil(vec.lengthVector() / speed);
		vec = vec.normalize();
		SparkUniformlySpeed spark = new SparkUniformlySpeed(world, x, y, z, vec.x * speed, vec.y * speed, vec.z * speed,
				age + 1, 0, manager);
		spark.setAlphaF(0.99F);
		spark.setTrail(trail);
		spark.setTwinkle(flicker);
		spark.setColor(colors[EventClient.rand.nextInt(colors.length)]);
		if (fadeoutColors != null && fadeoutColors.length > 0)
			spark.setColorFade(fadeoutColors[EventClient.rand.nextInt(fadeoutColors.length)]);
		manager.addEffect(spark);
	}

	/** ParticleFirework.Spark全是私有的，只能自己重新写一遍了 */
	public static class SparkUniformlySpeed extends ParticleSimpleAnimated {

		protected boolean trail;
		protected boolean twinkle;
		protected final ParticleManager effectRenderer;

		public SparkUniformlySpeed(World world, double x, double y, double z, double speedX, double speedY,
				double speedZ, int age, float yAccelIn, ParticleManager manager) {
			super(world, x, y, z, 160, 8, yAccelIn);
			this.canCollide = false;
			this.motionX = speedX;
			this.motionY = speedY;
			this.motionZ = speedZ;
			this.effectRenderer = manager;
			this.particleScale *= 0.75F;
			this.particleMaxAge = age;
			this.setBaseAirFriction(1);
		}

		public void setTrail(boolean trailIn) {
			this.trail = trailIn;
		}

		public void setTwinkle(boolean twinkleIn) {
			this.twinkle = twinkleIn;
		}

		public boolean shouldDisableDepth() {
			return true;
		}

		public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX,
				float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
			if (!this.twinkle || this.particleAge < this.particleMaxAge / 3
					|| (this.particleAge + this.particleMaxAge) / 3 % 2 == 0) {
				super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY,
						rotationXZ);
			}
		}

		public void onUpdate() {
			super.onUpdate();
			if (this.trail && this.particleAge < this.particleMaxAge / 2
					&& (this.particleAge + this.particleMaxAge) % 2 == 0) {
				SparkUniformlySpeed spark = new SparkUniformlySpeed(this.world, this.posX, this.posY, this.posZ, 0.0D,
						0.0D, 0.0D, 48 + this.rand.nextInt(12), -0.003F, this.effectRenderer);
				spark.setBaseAirFriction(0.91F);
				spark.setAlphaF(0.99F);
				spark.setRBGColorF(this.particleRed, this.particleGreen, this.particleBlue);
				spark.particleAge = spark.particleMaxAge / 2;
				spark.twinkle = this.twinkle;
				this.effectRenderer.addEffect(spark);
			}
		}

	}

	/** 元素类型的球 */
	public static void createEBall(World world, Vec3d position, double speed, int size, int[] colors) {

		for (int i = -size; i <= size; ++i) {
			for (int j = -size; j <= size; ++j) {
				for (int k = -size; k <= size; ++k) {
					double d3 = (double) j + (EventClient.rand.nextDouble() - EventClient.rand.nextDouble()) * 0.5D;
					double d4 = (double) i + (EventClient.rand.nextDouble() - EventClient.rand.nextDouble()) * 0.5D;
					double d5 = (double) k + (EventClient.rand.nextDouble() - EventClient.rand.nextDouble()) * 0.5D;
					double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5) / speed
							+ EventClient.rand.nextGaussian() * 0.05D;

					EffectElementMove e = new EffectElementMove(world, position);
					e.g = 0;
					e.yDecay = e.xDecay = e.zDecay = 0.9;
					e.setColor(colors[EventClient.rand.nextInt(colors.length)]);
					e.setVelocity(d3 / d6, d4 / d6, d5 / d6);
					Effect.addEffect(e);

					if (i != -size && i != size && j != -size && j != size) {
						k += size * 2 - 1;
					}
				}
			}
		}

	}

	/** 元素类型的环 */
	static public void createECircle(World world, Vec3d position, double speed, int size, int[] colors) {
		for (int i = -size; i <= size; ++i) {
			for (int j = -size; j <= size; ++j) {
				double d1 = (double) i + (EventClient.rand.nextDouble() - EventClient.rand.nextDouble()) * 0.5D;
				double d2 = (double) j + (EventClient.rand.nextDouble() - EventClient.rand.nextDouble()) * 0.5D;
				double d3 = (double) MathHelper.sqrt(d1 * d1 + d2 * d2) / speed
						+ EventClient.rand.nextGaussian() * 0.05D;
				EffectElementMove e = new EffectElementMove(world, position);
				e.g = 0.005;
				e.setColor(colors[EventClient.rand.nextInt(colors.length)]);
				e.setVelocity(d1 / d3, 0.1, d2 / d3);
				Effect.addEffect(e);
			}
		}
	}
}
