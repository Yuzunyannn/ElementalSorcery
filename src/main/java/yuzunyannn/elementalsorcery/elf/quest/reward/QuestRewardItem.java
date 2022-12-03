package yuzunyannn.elementalsorcery.elf.quest.reward;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.parchment.Page;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class QuestRewardItem extends QuestReward {

	protected List<ItemStack> stacks = new ArrayList<>();

	public QuestRewardItem item(ItemStack... stacks) {
		this.stacks = Arrays.asList(stacks);
		return this;
	}

	@Override
	public void initWithConfig(JsonObject json, Map<String, Object> context) {
		stacks = ItemRecord.asItemStackList(json.needItems("value"));
		Iterator<ItemStack> iter = stacks.iterator();
		while (iter.hasNext()) if (iter.next().isEmpty()) iter.remove();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		NBTHelper.setItemList(nbt, "item", stacks);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		stacks = NBTHelper.getItemList(nbt, "item");
	}

	@Override
	public void onReward(Quest quest, EntityLivingBase player) {
		if (!(player instanceof EntityPlayer)) return;
		for (ItemStack item : stacks) ItemHelper.addItemStackToPlayer((EntityPlayer) player, item.copy());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest task, EntityLivingBase player) {
		StringBuilder builder = new StringBuilder();
		int size = stacks.size();
		for (int i = 0; i < size; i++) {
			ItemStack stack = stacks.get(i);
			int count = stack.getCount();
			String c = Integer.toString(count);
			builder.append(I18n.format("quest.unit", c));
			builder.append(getDetailDisplayName(stack));
			if (i < size - 1) builder.append("、");
		}
		return builder.toString();
	}

	@SideOnly(Side.CLIENT)
	public static String getDetailDisplayName(ItemStack stack) {
		if (stack.getItem() == ESObjects.ITEMS.PARCHMENT && Pages.isVaild(stack)) {
			Page page = Pages.getPage(stack);
			if (I18n.hasKey(page.getName())) return I18n.format(page.getName());
		}
		return stack.getDisplayName();
	}
}
