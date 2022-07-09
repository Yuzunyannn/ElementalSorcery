package yuzunyannn.elementalsorcery.render.effect.crack;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;

@SideOnly(Side.CLIENT)
public class EffectFragmentCrackMove extends EffectFragmentCrack {

	public static void spawnBoom(World world, Vec3d pos, int color, float size) {
		int count = (int) Math.min(128, size * size * 16);
		for (int i = 0; i < count; i++) {
			Vec3d at = pos.add(MathHelper.clamp(rand.nextGaussian(), -1, 1) * size, 0,
					MathHelper.clamp(rand.nextGaussian(), -1, 1) * size);
			EffectFragmentCrackMove effect = new EffectFragmentCrackMove(world, at);
			effect.color.setColor(color);
			effect.yDecay = 0.8;
			effect.motionY = Effect.rand.nextGaussian() * 0.1 * size;
			Effect.addEffect(effect);
		}
	}

	public EffectFragmentCrackMove(World worldIn, Vec3d pos) {
		super(worldIn, pos);
		this.prevAlpha = this.alpha = 1;
	}

	public int endLifeTick = 20;

	public double xDecay = 0.96;
	public double yDecay = 1;
	public double zDecay = 0.96;

	public double xAccelerate = 0;
	public double yAccelerate = 0;
	public double zAccelerate = 0;

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.posX += motionX;
		this.posY += motionY;
		this.posZ += motionZ;

		motionX += xAccelerate;
		motionY += yAccelerate;
		motionZ += zAccelerate;

		motionX *= xDecay;
		motionY *= yDecay;
		motionZ *= zDecay;

		this.rotate += 10f;

		if (this.lifeTime < endLifeTick) this.scale = this.defaultScale * this.lifeTime / (float) endLifeTick;
	}

	public void setVelocity(Vec3d v) {
		motionX = v.x;
		motionY = v.y;
		motionZ = v.z;
	}

	public void setAccelerate(Vec3d acce) {
		xAccelerate = acce.x;
		yAccelerate = acce.y;
		zAccelerate = acce.z;
	}

}
