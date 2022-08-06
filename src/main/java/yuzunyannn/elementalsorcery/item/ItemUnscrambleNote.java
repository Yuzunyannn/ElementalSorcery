package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class ItemUnscrambleNote extends Item {

	public static final int MAX_CAPACITY = 1000;

	public ItemUnscrambleNote() {
		this.setTranslationKey("unscrambleNote");
		this.setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = stack.getTagCompound();
		int total = nbt == null ? 0 : nbt.getInteger("total");
		tooltip.add(I18n.format("info.remain.space", Integer.toString(Math.max(MAX_CAPACITY - total, 0))));
		if (nbt == null) return;
		for (String key : nbt.getKeySet()) {
			if (key.charAt(0) != '$') continue;
			int count = nbt.getInteger(key);
			String name = key.substring(1);
			tooltip.add(TextFormatting.RED + name + ":" + count);
		}
	}

	static public int getNoteEnergy(ItemStack researchNote, EntityPlayer player) {
		if (researchNote.isEmpty()) return 0;
		NBTTagCompound nbt = researchNote.getTagCompound();
		if (nbt == null) return 0;
		return nbt.getInteger("$" + player.getName());
	}

	static public void growNoteEnergy(ItemStack researchNote, EntityPlayer player, int count, boolean force) {
		if (researchNote.isEmpty()) return;
		if (!force && researchNote.getItem() != ESObjects.ITEMS.UNSCRAMBLE_NOTE) return;
		NBTTagCompound nbt = researchNote.getTagCompound();
		if (nbt == null) researchNote.setTagCompound(nbt = new NBTTagCompound());
		// 判断是否到容量了
		int total = nbt.getInteger("total");
		if (total >= MAX_CAPACITY && count > 0) return;
		// 记录
		String key = "$" + player.getName();
		int origin = nbt.getInteger(key);
		count = Math.max(origin + count, 0);
		if (count == 0) nbt.removeTag(key);
		else nbt.setInteger(key, count);
		nbt.setInteger("total", total + count - origin);
	}

}
