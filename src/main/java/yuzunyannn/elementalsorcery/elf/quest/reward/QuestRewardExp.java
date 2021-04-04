package yuzunyannn.elementalsorcery.elf.quest.reward;

import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.loader.ParamObtain;
import yuzunyannn.elementalsorcery.util.json.JsonObject;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class QuestRewardExp extends QuestReward {

	public static QuestRewardExp create(int exp) {
		return REGISTRY.newInstance(QuestRewardExp.class).exp(exp);
	}

	protected int exp = 0;

	public QuestRewardExp exp(int exp) {
		this.exp = exp;
		return this;
	}

	@Override
	public void initWithConfig(JsonObject json, Map<String, Object> context) {
		exp(ParamObtain.parser(json, "value", context, Number.class).intValue());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("exp", exp);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		exp = nbt.getInteger("exp");
	}

	@Override
	public void onReward(Quest quest, EntityLivingBase player) {
		WorldHelper.createExpBall(player, exp);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest task, EntityLivingBase player) {
		return I18n.format("quest.reward.exp", exp);
	}
}
