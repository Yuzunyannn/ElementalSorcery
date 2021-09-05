package yuzunyannn.elementalsorcery.explore;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.tile.IStarPray;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;

public class StarPrayGrimoirePotent implements IStarPray {

	@Override
	public float priority(World world, BlockPos pos, EntityLivingBase player) {
		return 1.25f;
	}

	@Override
	public float hopeDegree(World world, BlockPos pos, EntityLivingBase entity) {
		ItemStack stack = entity.getHeldItemMainhand();
		Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		if (grimoire == null) return 0;
		return 1;
	}

	@Override
	public void doPray(World world, BlockPos pos, EntityLivingBase entity) {
		ItemStack stack = entity.getHeldItemMainhand();
		Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		grimoire.loadState(stack);
		float potent = world.rand.nextFloat() * 0.75f + 0.25f;
		grimoire.addPotent(potent, 3 / potent);
		grimoire.saveState(stack);
	}

}
