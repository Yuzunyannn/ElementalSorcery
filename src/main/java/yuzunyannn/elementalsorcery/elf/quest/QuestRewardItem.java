package yuzunyannn.elementalsorcery.elf.quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class QuestRewardItem extends QuestReward {

	protected List<ItemStack> stacks = new ArrayList<>();

	public QuestRewardItem item(ItemStack... stacks) {
		this.stacks = Arrays.asList(stacks);
		return this;
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
	public void reward(Quest quest, EntityPlayer player) {
		for (ItemStack item : stacks) ItemHelper.addItemStackToPlayer(player, item.copy());
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
			builder.append(I18n.format(stack.getUnlocalizedName() + ".name"));
			if (i < size - 1) builder.append("、");
		}
		return builder.toString();
	}
}
