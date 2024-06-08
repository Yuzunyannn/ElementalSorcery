package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.item.ESItemStorageEnum;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;

public class ItemMantraGem extends Item {

	static public boolean isMantraGem(ItemStack stack) {
		return stack.getItem() == ESObjects.ITEMS.MANTRA_GEM;
	}

	static public Mantra getMantraFromMantraGem(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return null;
		return Mantra.REGISTRY.getValue(new ResourceLocation(nbt.getString(ESItemStorageEnum.MANTRA_STORAGE_ID)));
	}

	static public void setMantraToMantraGem(ItemStack stack, Mantra mantra) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) stack.setTagCompound(nbt = new NBTTagCompound());
		nbt.setString(ESItemStorageEnum.MANTRA_STORAGE_ID, mantra.getRegistryName().toString());
	}

	public ItemMantraGem() {
		this.setTranslationKey("mantraGem");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		Mantra mantra = getMantraFromMantraGem(stack);
		if (mantra == null) return;
		tooltip.add(TextFormatting.AQUA + mantra.getDisplayName());
	}

}
