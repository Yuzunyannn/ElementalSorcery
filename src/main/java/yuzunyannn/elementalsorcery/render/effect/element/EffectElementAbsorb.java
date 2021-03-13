package yuzunyannn.elementalsorcery.render.effect.element;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.render.effect.IBinder;

public class EffectElementAbsorb extends EffectElement {

	/** 位置 */
	public IBinder binder = null;
	/** 静止到飞行时间 */
	public int startTick = 0;

	public EffectElementAbsorb(World world, Vec3d from, IBinder to) {
		super(world, from.x, from.y, from.z);
		this.binder = to;
		startTick = rand.nextInt(20) + 20;
		randMotion(startTick / 80.0f);
		this.alpha = 0;
	}

	public EffectElementAbsorb(World world, Vec3d from, Vec3d to) {
		this(world, from, new IBinder.VecBinder(to));
	}

	public EffectElementAbsorb(World world, Vec3d from, Entity to) {
		this(world, from, new IBinder.EntityBinder(to, 0));
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
		this.prevAlpha = this.alpha;
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;

		if (startTick <= 0) {
			Vec3d to = binder.getPosition();
			Vec3d at = this.getPositionVector();
			Vec3d tar = to.subtract(at);
			double len = tar.lengthVector();
			if (len < 4) {
				this.alpha = (float) (len / 4);
			}
			// 结束
			if (len < 0.75) {
				this.lifeTime = 0;
				return;
			}
			tar = tar.normalize().scale(0.1);
			this.motionX += tar.x;
			this.motionY += tar.y;
			this.motionZ += tar.z;

		} else {
			this.alpha += (1 - alpha) * 0.1f;
			startTick--;
		}

		double factor = 0.9;
		this.motionX *= factor;
		this.motionY *= factor;
		this.motionZ *= factor;

	}

}
