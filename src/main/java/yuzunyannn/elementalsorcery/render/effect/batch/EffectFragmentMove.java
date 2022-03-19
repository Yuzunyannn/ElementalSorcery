package yuzunyannn.elementalsorcery.render.effect.batch;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

@SideOnly(Side.CLIENT)
public class EffectFragmentMove extends EffectFragment {

	@Deprecated
	public static void show(World world, Vec3d pos, NBTTagCompound nbt) {
		if (!NBTHelper.hasVec3d(nbt, "to")) return;
		Vec3d to = NBTHelper.getVec3d(nbt, "to");
		float range = 0.5f;
		for (int i = 0; i < 32; i++) {
			Vec3d at = pos.add(rand.nextGaussian() * range, rand.nextGaussian() * range, rand.nextGaussian() * range);
			EffectFragmentMove effect = new EffectFragmentMove(world, at);
			effect.lifeTime = 10 + rand.nextInt(20);
			effect.endLifeTick = 5;
			Vec3d toVec = to.subtract(at);
			Vec3d speed = toVec.add(toVec.normalize()).scale(1f / effect.lifeTime);
			effect.color.setColor(nbt.getInteger("c"));
			effect.xDecay = effect.yDecay = effect.zDecay = 0.5;

			effect.xAccelerate = speed.x * (1 - effect.xDecay) / effect.xDecay;
			effect.yAccelerate = speed.y * (1 - effect.yDecay) / effect.yDecay;
			effect.zAccelerate = speed.z * (1 - effect.zDecay) / effect.zDecay;
			addEffect(effect);
		}
	}

	public static void spawnBoom(World world, Vec3d pos, int color, float size) {
		int count = (int) Math.min(128, size * size * 16);
		for (int i = 0; i < count; i++) {
			Vec3d at = pos.add(MathHelper.clamp(rand.nextGaussian(), -1, 1) * size, 0,
					MathHelper.clamp(rand.nextGaussian(), -1, 1) * size);
			EffectFragmentMove effect = new EffectFragmentMove(world, at);
			effect.color.setColor(color);
			effect.yDecay = 0.8;
			effect.motionY = Effect.rand.nextGaussian() * 0.1 * size;
			Effect.addEffect(effect);
		}
	}

	public EffectFragmentMove(World worldIn, Vec3d pos) {
		super(worldIn, pos);
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

		if (this.lifeTime < endLifeTick) {
			this.alpha = this.lifeTime / (float) endLifeTick;
			this.scale = this.defaultScale * this.alpha;
		}
	}

}
