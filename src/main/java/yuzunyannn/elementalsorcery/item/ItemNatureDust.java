package yuzunyannn.elementalsorcery.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.entity.EntityExploreDust;

public class ItemNatureDust extends Item {

	public ItemNatureDust() {
		this.setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;
		for (EnumType type : EnumType.values()) items.add(new ItemStack(this, 1, type.getMetadata()));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.natureDust." + EnumType.byMetadata(stack.getMetadata()).getName();
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		int lev = stack.getMetadata();
		if (lev <= 0) return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		if (worldIn.isRemote) return EnumActionResult.SUCCESS;
		EntityExploreDust ed = new EntityExploreDust(worldIn, lev);
		ed.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
		worldIn.spawnEntity(ed);
		if (!player.isCreative()) {
			stack.shrink(1);
			player.setHeldItem(hand, stack);
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	static public enum EnumType implements IStringSerializable {
		NATURE("nature"),
		EXPLORE("explore"),
		EXPLORE_ADV("exploreAdv");

		final String name;

		EnumType(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getMetadata() {
			return this.ordinal();
		}

		static public EnumType byMetadata(int meta) {
			return EnumType.values()[0x3 & meta];
		}
	}
}
