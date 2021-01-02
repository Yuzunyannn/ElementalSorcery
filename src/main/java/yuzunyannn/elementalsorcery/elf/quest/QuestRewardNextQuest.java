package yuzunyannn.elementalsorcery.elf.quest;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.item.ItemQuest;

public class QuestRewardNextQuest extends QuestReward {

	protected Quest quest;

	public QuestRewardNextQuest quest(Quest quest) {
		this.quest = quest;
		return this;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("quest", quest.serializeNBT());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		quest = new Quest(nbt.getCompoundTag("quest"));
	}

	@Override
	public void reward(Quest quest, EntityPlayer player) {
		ElfPostOffice postOffice = ElfPostOffice.getPostOffice(player.world);
		EntityLivingBase fakeSender = new EntityElf(player.world);

		ArrayList<QuestCondition> preConditions = this.quest.getType().getPreconditions();
		for (QuestCondition con : preConditions)
			if (con instanceof QuestConditionDelegate) ((QuestConditionDelegate) con).delegate(player);

		List<ItemStack> list = new ArrayList<>();
		list.add(ItemQuest.createQuest(this.quest));
		postOffice.pushParcel(fakeSender, player, list);
	}

}
