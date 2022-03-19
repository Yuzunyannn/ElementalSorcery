package yuzunyannn.elementalsorcery.render.effect.batch;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

@SideOnly(Side.CLIENT)
public class EffectFragmentP2P extends EffectFragment {

	private static EffectFragmentP2P to(World world, Vec3d pos, Vec3d to, float range, int color, int tick) {
		Vec3d at = pos.add(rand.nextGaussian() * range, rand.nextGaussian() * range, rand.nextGaussian() * range);
		EffectFragmentP2P effect = new EffectFragmentP2P(world, at, to);
		effect.totalLifeTime = effect.lifeTime = tick;
		effect.color.setColor(color);
		addEffect(effect);
		return effect;
	}

	public static void show(World world, Vec3d pos, NBTTagCompound nbt) {
		if (!NBTHelper.hasVec3d(nbt, "to")) return;
		Vec3d to = NBTHelper.getVec3d(nbt, "to");
		float range = 0.5f;
		int color = nbt.getInteger("c");
		for (int i = 0; i < 32; i++) to(world, pos, to, range, color, 10 + rand.nextInt(20));
		int endFlag = nbt.getInteger("e");
		if (endFlag != 0) {
			int flag = (endFlag >> 24) & 0xff;
			int dat = endFlag & 0xffffff;
			if (flag == 1) {
				EffectFragmentP2P effect = to(world, pos, to, range, color, 20);
				effect.onEndCallback = v -> {
					EffectFragmentMove.spawnBoom(world, to, color, dat);
					return null;
				};
			}
		}
	}

	public int totalLifeTime;

	public EffectFragmentP2P(World worldIn, Vec3d startVec, Vec3d endVec) {
		super(worldIn, startVec);
		this.startVec = startVec;
		this.endVec = endVec;
		this.totalLifeTime = this.lifeTime;
	}

	public Vec3d startVec = Vec3d.ZERO;
	public Vec3d endVec = Vec3d.ZERO;

	@Override
	public void onUpdate() {
		super.onUpdate();
		float r = 1 - this.lifeTime / (float) this.totalLifeTime;
		Vec3d pos = startVec.add(endVec.subtract(startVec).scale(r));
		this.posX = pos.x;
		this.posY = pos.y;
		this.posZ = pos.z;
		this.rotate += 10f;
		if (r > 0.8) {
			r = (r - 0.8f) / 0.2f;
			this.alpha = 1 - r;
			this.scale = this.defaultScale * r;
		}
	}
}
