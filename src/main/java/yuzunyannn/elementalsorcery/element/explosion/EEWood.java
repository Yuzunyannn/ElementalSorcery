package yuzunyannn.elementalsorcery.element.explosion;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraLush;
import yuzunyannn.elementalsorcery.render.effect.Effects;

public class EEWood extends ElementExplosion {

	public EEWood(World world, Vec3d position, float strength, ElementStack estack) {
		super(world, position, strength, estack);
	}

	@Override
	protected void doExplosionBlockAt(BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getMaterial() == Material.AIR) return;
		if (world.isRemote) spawnEffectFromBlock(pos);
		else MantraLush.magicToBlock(world, pos, state, eStack.getPower(), attacker);
	}

	@Override
	protected void doExplosionEntityAt(Entity entity, Vec3d orient, double strength, double damage, double pound) {

		if (!world.isRemote) {
			doDamageSource(entity, damage * 0.25f);
			if (entity instanceof EntityLivingBase) ((EntityLivingBase) entity).heal((float) damage * 0.75f);
			if (entity instanceof EntityPlayer) {
				((EntityPlayer) entity).getFoodStats().addStats((int) (damage / 4), 1);
			}
		} else {
			if (entity instanceof EntityLivingBase) {
				Effects.spawnTreatEntity(entity, new int[] { eStack.getColor(), 0xe61818 });
			}
		}

	}

}
