package yuzunyannn.elementalsorcery.render.effect.batch;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.IEffectBinder;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

@SideOnly(Side.CLIENT)
public class EffectElementAbsorb extends EffectElementMove {

	public static void show(World world, Vec3d pos, NBTTagCompound nbt) {
		int id = nbt.getInteger("targetEntity");
		Entity target = world.getEntityByID(id);
		Vec3d targetPos = null;
		if (target != null) targetPos = target.getPositionVector().add(0, target.height / 2, 0);
		else if (NBTHelper.hasVec3d(nbt, "targetPos")) targetPos = NBTHelper.getVec3d(nbt, "targetPos");

		if (targetPos == null) return;
		int[] colors = nbt.getIntArray("colors");
		int times = Math.max(1, nbt.getInteger("times"));

		for (int i = 0; i < times; i++) {
			EffectElementAbsorb effect = new EffectElementAbsorb(world, pos, targetPos);
			effect.setColor(colors.length == 0 ? rand.nextInt() : colors[rand.nextInt(colors.length)]);
			Effect.addEffect(effect);
		}
	}

	/** 位置 */
	public IEffectBinder binder = null;
	/** 静止到飞行时间 */
	public int startTick = 0;

	public EffectElementAbsorb(World world, Vec3d from, IEffectBinder to) {
		super(world, from);
		this.binder = to;
		startTick = rand.nextInt(10) + 10;
		randMotion(startTick / 80.0f);
		this.dalpha = this.alpha = 0;
		yDecay = xDecay = zDecay = 0.9;
	}

	public EffectElementAbsorb(World world, Vec3d from, Vec3d to) {
		this(world, from, new IEffectBinder.VecBinder(to));
	}

	public EffectElementAbsorb(World world, Vec3d from, Entity to) {
		this(world, from, new IEffectBinder.EntityBinder(to, 0));
	}

	public void randMotion(double scale) {
		Vec3d vec = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
		vec = vec.normalize().scale(scale);
		this.motionX = vec.x;
		this.motionY = vec.y;
		this.motionZ = vec.z;
	}

	@Override
	public void onUpdate() {
		this.lifeTime = 2;
		super.onUpdate();

		if (startTick <= 0) {
			Vec3d to = binder.getPosition();
			Vec3d at = this.getPositionVector();
			Vec3d tar = to.subtract(at);
			double len = tar.length();
			if (len < 4) this.alpha = (float) (len / 4);
			// 结束
			if (len < 0.75) {
				this.lifeTime = 0;
				return;
			}
			setAccelerate(tar.normalize().scale(0.05));
		} else {
			this.alpha += (1 - alpha) * 0.1f;
			startTick--;
		}
	}

}
