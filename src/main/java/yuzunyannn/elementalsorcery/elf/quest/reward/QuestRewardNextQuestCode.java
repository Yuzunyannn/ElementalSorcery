package yuzunyannn.elementalsorcery.elf.quest.reward;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestCondition;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestConditionDelegate;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.item.ItemQuest;

public class QuestRewardNextQuestCode extends QuestReward {

	protected Quest quest;

	public QuestRewardNextQuestCode quest(Quest quest) {
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
	public void onReward(Quest quest, EntityLivingBase player) {
		if (!(player instanceof EntityPlayer)) return;

		ElfPostOffice postOffice = ElfPostOffice.getPostOffice(player.world);
		EntityLiving fakeSender = new EntityElf(player.world);
		fakeSender.onInitialSpawn(fakeSender.world.getDifficultyForLocation(new BlockPos(fakeSender)), null);

		ArrayList<QuestCondition> preConditions = this.quest.getType().getPreconditions();
		for (QuestCondition con : preConditions)
			if (con instanceof QuestConditionDelegate) ((QuestConditionDelegate) con).delegate((EntityPlayer) player);

		List<ItemStack> list = new ArrayList<>();
		list.add(ItemQuest.createQuest(this.quest));
		postOffice.pushParcel(fakeSender, (EntityPlayer) player, list);
	}

}
