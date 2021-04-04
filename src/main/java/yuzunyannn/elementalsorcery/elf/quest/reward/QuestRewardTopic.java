package yuzunyannn.elementalsorcery.elf.quest.reward;

import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.loader.ParamObtain;
import yuzunyannn.elementalsorcery.elf.research.Researcher;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class QuestRewardTopic extends QuestReward {

	public static QuestRewardTopic create(String type, int count) {
		return REGISTRY.newInstance(QuestRewardTopic.class).set(type, count);
	}

	protected int count = 0;
	protected String type = "";

	public QuestRewardTopic set(String type, int count) {
		this.count = count;
		this.type = type;
		return this;
	}

	@Override
	public void initWithConfig(JsonObject json, Map<String, Object> context) {
		int value = ParamObtain.parser(json, "value", context, Number.class).intValue();
		set(json.needString("topic"), value);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("count", count);
		nbt.setString("type", type);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		count = nbt.getInteger("count");
		type = nbt.getString("type");
	}

	@Override
	public void onReward(Quest quest, EntityLivingBase player) {
		Researcher.research(player, type, count);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest task, EntityLivingBase player) {
		return "";
	}
}
