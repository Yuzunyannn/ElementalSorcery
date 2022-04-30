package yuzunyannn.elementalsorcery.element.explosion;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFluorspar;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.scrappy.EffectShineBlock;
import yuzunyannn.elementalsorcery.util.helper.OreHelper;

public class EEMetal extends ElementExplosion {

	public EEMetal(World world, Vec3d position, float strength, ElementStack estack) {
		super(world, position, strength, estack);
	}

	@Override
	protected float getExplosionResistance(BlockPos pos, IBlockState state) {
		return 0;
	}

	@Override
	protected void doExplosionBlockAt(BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getMaterial() == Material.AIR) return;
		int n = 20 - Math.min(14, eStack.getPower() / 75);

		int oreColor = OreHelper.getOreColor(state);
		if (oreColor != -1) {
			if (world.isRemote) shineBlock(pos, oreColor);
		} else {
			if (rand.nextInt(24) == 0) {
				if (world.isRemote) spawnEffectFromBlock(pos);
				else super.doExplosionBlockAt(pos);
			} else if (rand.nextInt(n) == 0) {
				IBlockState newBlockState = MantraFluorspar.getChange(state);
				if (newBlockState != null) {
					if (world.isRemote) spawnEffectFromBlock(pos);
					else world.setBlockState(pos, newBlockState);
				}
			}
		}
	}

	@Override
	protected void doExplosionEntityAt(Entity entity, Vec3d orient, double strength, double damage, double pound) {
		super.doExplosionEntityAt(entity, orient, strength, damage * 0.5f, pound);
	}

	@SideOnly(Side.CLIENT)
	public void shineBlock(BlockPos pos, int color) {
		int sec = 10 + eStack.getPower() / 25;
		EffectShineBlock shine = new EffectShineBlock(world, pos, sec);
		shine.setColor(color);
		Effect.addEffect(shine);
	}
}
