package yuzunyannn.elementalsorcery.elf.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.item.ItemQuest;

public class QuestRewardNextQuestId extends QuestReward {

	public final static Map<String, BiFunction<Quest, EntityPlayer, Quest>> CREATORS = new HashMap<>();

	static public QuestRewardNextQuestId create(String creater) {
		return QuestReward.REGISTRY.newInstance(QuestRewardNextQuestId.class).quest(creater);
	}

	protected String creater;

	public QuestRewardNextQuestId quest(String creater) {
		this.creater = creater;
		return this;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setString("creater", creater);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		creater = nbt.getString("creater");
	}

	@Override
	public void reward(Quest quest, EntityPlayer player) {
		BiFunction<Quest, EntityPlayer, Quest> creator = CREATORS.get(this.creater);
		if (creator == null) return;
		Quest nextQuest = creator.apply(quest, player);
		if (nextQuest == null) return;
		ElfPostOffice postOffice = ElfPostOffice.getPostOffice(player.world);
		EntityLivingBase fakeSender = new EntityElf(player.world);
		List<ItemStack> list = new ArrayList<>();
		list.add(ItemQuest.createQuest(nextQuest));
		postOffice.pushParcel(fakeSender, player, list);
	}

}
