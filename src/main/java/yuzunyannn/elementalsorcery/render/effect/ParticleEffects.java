package yuzunyannn.elementalsorcery.render.effect;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.particle.ParticleMagicFall;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;
import yuzunyannn.elementalsorcery.util.RandomHelper;

public class ParticleEffects {

	@SideOnly(Side.CLIENT)
	public static void showShow(World world, Vec3d pos, NBTTagCompound nbt) {
		int id = nbt.getByte("type");
		switch (id) {
		case 0:
			magicBlast(world, pos, nbt.getByte("lev"));
			break;
		}
	}

	@SideOnly(Side.CLIENT)
	public static void magicBlast(World world, Vec3d vec, int level) {

		float ln = level * MathHelper.sqrt(level);
		Random rand = RandomHelper.rand;
		for (int i = 0; i < 25 * ln; i++) {
			Vec3d t = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
			Vec3d pos = vec.add(t.normalize().scale(0.2));
			ParticleMagicFall p = new ParticleMagicFall(world, pos);
			if (level >= 2) {
				p.setMotionH(rand.nextGaussian() * (0.175f + 0.04 * (level - 2)),
						rand.nextGaussian() * (0.175f + 0.04 * (level - 2)));
				p.setMotionY(rand.nextDouble() * (0.5f + 0.3 * (level - 2)));
			}
			p.setColor(TileMDBase.PARTICLE_COLOR[0]);
			p.setColorFade(TileMDBase.PARTICLE_COLOR_FADE[0]);
			FirewrokShap.manager.addEffect(p);
		}
		FirewrokShap.createBall(world, vec, 0.15 * level, level, TileMDBase.PARTICLE_COLOR,
				TileMDBase.PARTICLE_COLOR_FADE, false, false);

	}

}
