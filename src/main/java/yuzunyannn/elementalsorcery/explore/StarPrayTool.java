package yuzunyannn.elementalsorcery.explore;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import yuzunyannn.elementalsorcery.api.tile.IStarPray;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class StarPrayTool implements IStarPray {

	@Override
	public float priority(World world, BlockPos pos, EntityLivingBase player) {
		return 0.5f;
	}

	@Override
	public float hopeDegree(World world, BlockPos pos, EntityLivingBase entity) {
		ItemStack stack = entity.getHeldItemMainhand();
		if (!stack.isEmpty()) return 0;
		float drop = entity.getLastDamageSource() == null ? 1 : 0.5f;
		stack = entity.getHeldItemOffhand();
		return drop * 1;
	}

	@Override
	public void doPray(World world, BlockPos pos, EntityLivingBase entity) {
		ItemStack stack = entity.getHeldItemOffhand();
		if (BlockHelper.isOre(stack) || BlockHelper.hasKeyInOreDictionary(stack, "stone"))
			entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
		else if (BlockHelper.hasKeyInOreDictionary(stack, "wood"))
			entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_AXE));
		else {
			Block block = Block.getBlockFromItem(stack.getItem());
			if (block instanceof IPlantable || stack.getItem() instanceof IPlantable)
				entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_HOE));
			else {
				boolean hasPickaxe = false;
				if (entity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) entity;
					for (int i = 0; i < player.inventory.getSizeInventory() && i < 36; i++) {
						ItemStack tool = player.inventory.getStackInSlot(i);
						if (tool.isEmpty()) continue;
						if (tool.getItem() instanceof ItemTool)
							hasPickaxe = ((ItemTool) tool.getItem()).getToolClasses(tool).contains("pickaxe");
						if (hasPickaxe) break;
					}
				}
				if (hasPickaxe) entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_SHOVEL));
				else entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
			}
		}
	}

}
