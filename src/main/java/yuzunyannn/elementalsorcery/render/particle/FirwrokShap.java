package yuzunyannn.elementalsorcery.render.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventClient;

@SideOnly(Side.CLIENT)
public class FirwrokShap {

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
}
