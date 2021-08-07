package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.IBinder;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectTreatMove;

@SideOnly(Side.CLIENT)
public class EffectTreatEntity extends Effect {

	public static void show(World world, Vec3d pos, NBTTagCompound nbt) {
		int id = nbt.getInteger("target");
		Entity target = world.getEntityByID(id);
		int[] colors = nbt.getIntArray("colors");
		if (colors.length <= 0) colors = new int[] { 0xe61818, 0xfa6f6f, 0xa00a0a };
		Vec3d vec = target.getPositionVector().addVector(0, target.height / 2, 0);
		EffectTreatEntity te = new EffectTreatEntity(world, vec).setColors(colors);
		if (target instanceof EntityLivingBase) te.bindEntity(target);
		Effect.addEffect(te);
	}

	public int[] colors;
	public float width = 0.5f;
	public IBinder binder;

	public EffectTreatEntity(World world, Vec3d vec) {
		super(world, vec.x, vec.y, vec.z);
	}

	public EffectTreatEntity setColors(int[] colors) {
		this.colors = colors;
		return this;
	}

	public EffectTreatEntity setWidth(float width) {
		this.width = width;
		return this;
	}

	public EffectTreatEntity bindEntity(Entity entity) {
		binder = new IBinder.EntityBinder(entity, entity.height / 2f * 1.25f);
		this.setWidth(entity.width / 2f);
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
		if (this.lifeTime % 4 == 0) {
			Vec3d vec = this.getPositionVector();
			vec = vec.addVector(rand.nextGaussian() * width, rand.nextGaussian() * width, rand.nextGaussian() * width);
			EffectTreatMove move = new EffectTreatMove(world, vec);
			move.setColor(this.getColor());
			addEffect(move);
		}
		if (this.lifeTime % 6 == 0) {
			Vec3d vec = this.getPositionVector();
			vec = vec.addVector(rand.nextGaussian() * width, rand.nextGaussian() * width, rand.nextGaussian() * width);
			EffectElementMove move = new EffectElementMove(world, vec);
			move.g = 0;
			move.motionY = 0.01f;
			move.setColor(this.getColor());
			addEffect(move);
		}
	}

}
