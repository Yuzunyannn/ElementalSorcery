package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.IBinder;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;

@SideOnly(Side.CLIENT)
public class EffectSummonEntity extends Effect {

	public static void show(World world, Vec3d pos, NBTTagCompound nbt) {
		int id = nbt.getInteger("target");
		Entity target = world.getEntityByID(id);

		int[] colors = nbt.getIntArray("colors");
		if (colors.length <= 0) colors = new int[] { 0x101313, 0x570000, 0x6b0e0e, 0xb43232 };

		if (target instanceof EntityLivingBase) {
			EffectSummonEntity effect = new EffectSummonEntity(world, target.getPositionVector());
			effect.bindEntity(target).setColors(colors);
			Effect.addEffect(effect);
		} else {
			EffectSummonEntity effect = new EffectSummonEntity(world, pos).setColors(colors);
			Effect.addEffect(effect);
		}
	}

	public int maxTick;
	public int[] colors;
	public float width = 1.414f;
	public float height = 2f;
	public IBinder binder;

	public EffectSummonEntity(World world, Vec3d vec) {
		super(world, vec.x, vec.y, vec.z);
		this.maxTick = 30;
		this.lifeTime = this.maxTick;
	}

	public EffectSummonEntity setColors(int[] colors) {
		this.colors = colors;
		return this;
	}

	public EffectSummonEntity setWidth(float width) {
		this.width = width;
		return this;
	}

	public EffectSummonEntity setHeight(float height) {
		this.height = height;
		return this;
	}

	public EffectSummonEntity bindEntity(Entity entity) {
		binder = new IBinder.EntityBinder(entity, 0);
		setWidth(entity.width * 1.414f * 2).setHeight(entity.height * 1.25f);
		return this;
	}

	public Vec3d getPositionVector() {
		if (binder != null) return binder.getPosition();
		return super.getPositionVector();
	}

	public int getColor() {
		if (colors == null || colors.length <= 0) return 0;
		return colors[rand.nextInt(colors.length)];
	}

	@Override
	public void onUpdate() {
		this.lifeTime--;
		float rate = 1 - this.lifeTime / (float) this.maxTick;
		Vec3d vec = this.getPositionVector();
		float x = MathHelper.sin(rate * 3.1415926f * 8f) * width / 2;
		float z = MathHelper.cos(rate * 3.1415926f * 8f) * width / 2;
		vec = vec.addVector(x, height * rate, z);
		EffectElementMove move = new EffectElementMove(world, vec);
		move.g = 0;
//		move.motionY = 0.01f;
		move.setColor(this.getColor());
		addEffect(move);
	}

}
