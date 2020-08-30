package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.grimoire.Mantra;
import yuzunyannn.elementalsorcery.init.MantraRegister;

public class ItemAncientPaper extends Item {

	public ItemAncientPaper() {
		this.setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;
		for (EnumType type : EnumType.values()) {
			if (type == EnumType.NORMAL) {
				for (Mantra m : MantraRegister.instance) {
					ItemStack stack = new ItemStack(this, 1, type.getMetadata());
					setMantraData(stack, m, 0, 100);
					items.add(stack);
				}
			} else items.add(new ItemStack(this, 1, type.getMetadata()));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.ancientPaper." + EnumType.byMetadata(stack.getMetadata()).getName();
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	static public enum EnumType implements IStringSerializable {
		NORMAL("normal"),
		NEW("new"),
		NEW_WRITTEN("newWritten");

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

	public static void setMantraData(ItemStack stack, Mantra mantra, int start, int end) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("id", mantra.getRegistryName().toString());
		nbt.setByte("start", (byte) MathHelper.clamp(start, 0, 100));
		nbt.setByte("end", (byte) MathHelper.clamp(end, 0, 100));
		stack.setTagCompound(nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return;
		Mantra m = Mantra.getFromNBT(nbt);
		if (m == null) return;
		String name = I18n.format(m.getUnlocalizedName() + ".name");
		tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("info.ancientPaper.mantra", name));
		if (nbt.hasKey("data")) return;
		if (nbt.hasKey("start")) {
			int start = nbt.getByte("start");
			int end = nbt.getByte("end");
			tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("info.ancientPaper.record", start, end));
		}
	}

}
