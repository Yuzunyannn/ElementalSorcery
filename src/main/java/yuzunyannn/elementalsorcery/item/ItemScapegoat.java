package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.entity.EntityScapegoat;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;

public class ItemScapegoat extends Item implements TileMDRubbleRepair.IExtendRepair {

	public ItemScapegoat() {
		this.setTranslationKey("scapegoat");
		this.setMaxDamage(64);
		this.setMaxStackSize(1);
		this.canRepair = true;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {

		pos = pos.offset(facing);
		if (!world.isAirBlock(pos)) return EnumActionResult.FAIL;
		if (!world.isAirBlock(pos.up())) return EnumActionResult.FAIL;

		AxisAlignedBB aabb = new AxisAlignedBB(pos);
		List<EntityScapegoat> entities = world.getEntitiesWithinAABB(EntityScapegoat.class, aabb);
		if (!entities.isEmpty()) return EnumActionResult.FAIL;

		if (world.isRemote) return EnumActionResult.SUCCESS;

		ItemStack stack = player.getHeldItem(hand);

		EntityScapegoat scapegoat = new EntityScapegoat(world, pos, player, stack);
		scapegoat.setHealth(Math.max(1, 64 - stack.getItemDamage()));
		scapegoat.rotationYaw = player.rotationYaw + 180;
		world.spawnEntity(scapegoat);

		if (player.isCreative()) return EnumActionResult.SUCCESS;
		stack.shrink(1);

		return EnumActionResult.SUCCESS;
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return repair.getItem() == Items.WHEAT;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment == Enchantments.MENDING;
	}

	@Override
	public ItemStack getRepairOutput(ItemStack input) {
		if (input.getItemDamage() == 0) return ItemStack.EMPTY;
		input = input.copy();
		input.setItemDamage(input.getItemDamage() - 1);
		return input;
	}

	@Override
	public int getRepairCost(ItemStack input) {
		return 5;
	}

}
