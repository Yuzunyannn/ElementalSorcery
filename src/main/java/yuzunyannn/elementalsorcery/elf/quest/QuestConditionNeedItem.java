package yuzunyannn.elementalsorcery.elf.quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.item.ItemRec;

public class QuestConditionNeedItem extends QuestCondition {

	protected List<ItemRec> needs = new ArrayList<>();

	public QuestConditionNeedItem needItem(List<ItemRec> needs) {
		this.needs = needs;
		return this;
	}

	public QuestConditionNeedItem needItem(ItemRec... needs) {
		this.needs = new ArrayList<ItemRec>(Arrays.asList(needs));
		return this;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		NBTHelper.setNBTSerializableList(nbt, "need", needs);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		needs = NBTHelper.getNBTSerializableList(nbt, "need", ItemRec.class, NBTTagCompound.class);
	}

	static public boolean compare(ItemStack a, ItemStack b) {
		Item item = a.getItem();
		if (item == Items.ENCHANTED_BOOK) out: {
			if (!a.isItemEqual(b)) return false;
			NBTTagList list = ItemEnchantedBook.getEnchantments(a);
			if (list.hasNoTags()) break out;
			NBTTagCompound data = list.getCompoundTagAt(0);
			if (!data.hasKey("id", NBTTag.TAG_NUMBER)) break out;
			int id = data.getShort("id");
			int level = data.getShort("lvl");

			list = ItemEnchantedBook.getEnchantments(b);
			if (list.hasNoTags()) return false;
			for (int i = 0; i < list.tagCount(); i++) {
				data = list.getCompoundTagAt(i);
				if (data.getShort("id") == id && data.getShort("lvl") >= level) return true;
			}
			return false;
		}
		return a.isItemEqual(b);
	}

	@SideOnly(Side.CLIENT)
	static public String getStackShow(ItemStack stack) {
		Item item = stack.getItem();
		if (item == Items.ENCHANTED_BOOK) out: {
			NBTTagList list = ItemEnchantedBook.getEnchantments(stack);
			if (list.hasNoTags()) break out;
			NBTTagCompound data = list.getCompoundTagAt(0);
			Enchantment enchantment = Enchantment.getEnchantmentByID(data.getShort("id"));
			if (enchantment == null) break out;
			return stack.getDisplayName() + "(" + I18n.format(enchantment.getName())
					+ I18n.format("enchantment.level." + data.getShort("lvl")) + ")";
		}
		return stack.getDisplayName();
	}

	protected List<ItemRec> checkResult = new ArrayList<>();

	@Override
	public boolean check(Quest task, EntityPlayer player) {
		boolean isRemote = player.world.isRemote;
		boolean allOk = true;
		// 客户端要记录还缺什么东西
		if (isRemote) checkResult.clear();
		InventoryPlayer inventory = player.inventory;
		for (ItemRec need : needs) {
			// 检查物品
			ItemStack needStack = need.getItemStack();
			int count = needStack.getCount();
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				ItemStack origin = inventory.getStackInSlot(i);
				if (origin.isEmpty()) continue;
				if (!compare(needStack, origin)) continue;
				count -= origin.getCount();
				if (count <= 0) break;
			}
			if (isRemote) {
				ItemRec rec = new ItemRec(needStack.copy());
				count = Math.max(count, 0);
				rec.getItemStack().setCount(needStack.getCount() - count);
				checkResult.add(rec);
				if (count > 0) allOk = false;
				continue;
			}
			if (count > 0) return false;
		}
		return allOk;
	}

	@Override
	public void finish(Quest task, EntityPlayer player) {
		InventoryPlayer inventory = player.inventory;
		for (ItemRec need : needs) {
			// 拿走物品
			ItemStack needStack = need.getItemStack();
			int count = needStack.getCount();
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				ItemStack origin = inventory.getStackInSlot(i);
				if (origin.isEmpty()) continue;
				if (!compare(needStack, origin)) continue;
				int drop = Math.min(count, origin.getCount());
				origin.shrink(drop);
				count -= drop;
				if (count <= 0) break;
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest quest, EntityLivingBase player, boolean dynamic) {
		StringBuilder builder = new StringBuilder();
		int size = needs.size();
		for (int i = 0; i < size; i++) {
			ItemStack needStack = needs.get(i).getItemStack();
			int count = needStack.getCount();
			String c = Integer.toString(count);
			if (dynamic) {
				int have = 0;
				if (i < checkResult.size()) {
					ItemRec rec = checkResult.get(i);
					have = rec.getItemStack().getCount();
				}
				if (have >= count) c = c + TextFormatting.GREEN + "(" + have + ")";
				else c = c + TextFormatting.DARK_RED + "(" + have + ")";
				c += TextFormatting.RESET;
			}
			builder.append(I18n.format("quest.unit", c));
			builder.append(getStackShow(needStack));
			if (i < size - 1) builder.append("、");
		}
		return builder.toString();
	}

}
