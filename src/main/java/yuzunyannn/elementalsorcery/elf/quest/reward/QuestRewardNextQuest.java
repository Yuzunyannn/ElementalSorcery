package yuzunyannn.elementalsorcery.elf.quest.reward;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.QuestType;
import yuzunyannn.elementalsorcery.elf.quest.Quests;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestConditionDelegate;
import yuzunyannn.elementalsorcery.elf.quest.loader.ParamObtain;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.item.ItemQuest;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class QuestRewardNextQuest extends QuestReward {

	protected ResourceLocation nextId;

	protected boolean isDelegate;

	public QuestRewardNextQuest quest(ResourceLocation id) {
		this.nextId = id;
		return this;
	}

	@Override
	public void initWithConfig(JsonObject json, Map<String, Object> context) {
		String id = ParamObtain.parser(json.needString("id", "next", "value"), context).toString();
		if (json.hasBoolean("delegate")) isDelegate = json.getBoolean("delegate");
		quest(TextHelper.toESResourceLocation(id));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setString("next", nextId.toString());
		if (isDelegate) nbt.setBoolean("delegate", isDelegate);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		nextId = new ResourceLocation(nbt.getString("next"));
		isDelegate = nbt.getBoolean("delegate");
	}

	@Override
	public void onReward(Quest quest, EntityLivingBase player) {
		if (!(player instanceof EntityPlayer)) return;

		Quest nextQuest = Quests.createQuest(nextId, player, quest);
		if (nextQuest == null) return;
		QuestType type = nextQuest.getType();
		if (type.sustain > 0) nextQuest.setEndTime(player.world.getWorldTime() + type.sustain);
		else nextQuest.setEndTime(player.world.getWorldTime() + 24000 + player.world.rand.nextInt(8) * 24000);

		if (isDelegate) type.addPrecondition(QuestConditionDelegate.create(player));

		ElfPostOffice postOffice = ElfPostOffice.getPostOffice(player.world);
		EntityLivingBase fakeSender = new EntityElf(player.world);
		List<ItemStack> list = new ArrayList<>();
		list.add(ItemQuest.createQuest(nextQuest));
		postOffice.pushParcel(fakeSender, (EntityPlayer) player, list);
	}

}
