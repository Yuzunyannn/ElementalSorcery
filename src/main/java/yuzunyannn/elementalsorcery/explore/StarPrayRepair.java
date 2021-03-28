package yuzunyannn.elementalsorcery.explore;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.tile.IStarPray;
import yuzunyannn.elementalsorcery.item.ItemScapegoat;

public class StarPrayRepair implements IStarPray {

	@Override
	public float priority(World world, BlockPos pos, EntityLivingBase player) {
		return 0.65f;
	}

	@Override
	public float hopeDegree(World world, BlockPos pos, EntityLivingBase entity) {
		ItemStack stack = entity.getHeldItemMainhand();
		if (stack.isEmpty()) return 0;
		if (stack.getHasSubtypes()) return 0;
		Item item = stack.getItem();
		float drop = entity.getLastDamageSource() == null ? 1 : 0.5f;
		float n = stack.getItemDamage() / (float) stack.getMaxDamage();
		if (n == 0) return 0;
		n = Math.max(n, 0.25f);
		if (item instanceof ItemTool) return drop * n;
		if (item instanceof ItemScapegoat) return drop * n;
		return 0;
	}

	@Override
	public void doPray(World world, BlockPos pos, EntityLivingBase entity) {
		ItemStack stack = entity.getHeldItemMainhand();
		stack.setItemDamage(0);
		world.playSound((EntityPlayer) null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_ANVIL_USE,
				SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
	}

}
