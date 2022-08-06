package yuzunyannn.elementalsorcery.explore;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.tile.IStarPray;

public class StarPrayItemChange implements IStarPray {

	@Override
	public float priority(World world, BlockPos pos, EntityLivingBase player) {
		return 2;
	}

	@Override
	public float hopeDegree(World world, BlockPos pos, EntityLivingBase entity) {
		ItemStack stack = entity.getHeldItemMainhand();
		if (stack.isEmpty()) return 0;
		if (stack.getItem() == ESObjects.ITEMS.STAR_BELL && stack.getMetadata() == 0) return 1;
		return 0;
	}

	@Override
	public void doPray(World world, BlockPos pos, EntityLivingBase entity) {
		ItemStack stack = entity.getHeldItemMainhand();
		Item item = stack.getItem();
		if (item == ESObjects.ITEMS.STAR_BELL) {
			stack.setItemDamage(1);
			world.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.BLOCK_NOTE_BELL,
					SoundCategory.RECORDS, 2.0F, 0);
		}
	}

}
