package yuzunyannn.elementalsorcery.elf.quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.NBTHelper;
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

	protected List<ItemRec> checkRes = new ArrayList<>();

	@Override
	public boolean check(Quest task, EntityPlayer player) {
		boolean isRemove = player.world.isRemote;
		boolean allOk = true;
		// 客户端要记录还缺什么东西
		if (isRemove) checkRes.clear();
		InventoryPlayer inventory = player.inventory;
		for (ItemRec need : needs) {
			// 检查物品
			ItemStack needStack = need.getItemStack();
			int count = needStack.getCount();
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				ItemStack origin = inventory.getStackInSlot(i);
				if (origin.isEmpty()) continue;
				if (!origin.isItemEqual(needStack)) continue;
				count -= origin.getCount();
				if (count <= 0) break;
			}
			if (isRemove) {
				ItemRec rec = new ItemRec(needStack.copy());
				count = Math.max(count, 0);
				rec.getItemStack().setCount(needStack.getCount() - count);
				checkRes.add(rec);
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
				if (!origin.isItemEqual(needStack)) continue;
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
				if (i < checkRes.size()) {
					ItemRec rec = checkRes.get(i);
					have = rec.getItemStack().getCount();
				}
				if (have >= count) c = c + TextFormatting.GREEN + "(" + have + ")";
				else c = c + TextFormatting.DARK_RED + "(" + have + ")";
				c += TextFormatting.RESET;
			}
			builder.append(I18n.format("quest.unit", c));
			builder.append(I18n.format(needStack.getUnlocalizedName() + ".name"));
			if (i < size - 1) builder.append("、");
		}
		return builder.toString();
	}

}
