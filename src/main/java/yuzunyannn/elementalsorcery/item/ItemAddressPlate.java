package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemAddressPlate extends Item {

	public ItemAddressPlate() {
		this.setHasSubtypes(true);
		this.setUnlocalizedName("addressPlate");
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			items.add(new ItemStack(this, 1, 0));
			items.add(new ItemStack(this, 1, 1));
		}
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return;
		String address = nbt.getString("address");
		String signature = nbt.getString("signature");
		tooltip.add(TextFormatting.GREEN + I18n.format("info.address", address));
		if (!signature.isEmpty()) tooltip.add(TextFormatting.GOLD + I18n.format("info.signature", signature));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.addressPlate." + EnumType.byMetadata(stack.getMetadata()).getName();
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	static public enum EnumType implements IStringSerializable {
		NORMAL("normal"),
		VIP("vip");

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
