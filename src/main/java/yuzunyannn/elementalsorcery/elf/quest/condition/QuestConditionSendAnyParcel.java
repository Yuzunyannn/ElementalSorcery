package yuzunyannn.elementalsorcery.elf.quest.condition;

import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.QuestData;
import yuzunyannn.elementalsorcery.elf.quest.QuestTrigger;
import yuzunyannn.elementalsorcery.elf.quest.QuestTriggers;
import yuzunyannn.elementalsorcery.elf.quest.loader.ParamObtain;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class QuestConditionSendAnyParcel extends QuestCondition {

	protected int needCount = 1;

	public QuestConditionSendAnyParcel needCount(int count) {
		this.needCount = Math.max(count, 1);
		return this;
	}

	@Override
	public void initWithConfig(JsonObject json, Map<String, Object> context) {
		needCount(ParamObtain.parser(json, "value", context, Number.class).intValue());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("ncount", needCount);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		needCount = Math.max(nbt.getInteger("ncount"), 1);
	}

	@Override
	public boolean onCheck(Quest task, EntityLivingBase entity) {
		QuestData data = task.getData();
		return data.getInteger("count") >= needCount;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest task, EntityLivingBase player, boolean dynamic) {
		String times = Integer.toString(needCount);
		int count = task.getData().getInteger("count");
		if (dynamic) {
			StringBuilder builder = new StringBuilder();
			builder.append(times);
			if (count >= needCount) builder.append(TextFormatting.GREEN);
			else builder.append(TextFormatting.DARK_RED);
			builder.append('(').append(count).append(')').append(TextFormatting.RESET);
			times = builder.toString();
		}
		return I18n.format("quest.use.send.parcel", times);
	}

	@Override
	public <T> void onTrigger(Quest quest, EntityLivingBase player, QuestTrigger<T> type, T data) {
		if (type != QuestTriggers.SEND_PARCEL) return;
		quest.getData().growInteger("count", 1);
	}

}
