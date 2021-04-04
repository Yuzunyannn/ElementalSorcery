package yuzunyannn.elementalsorcery.elf.quest.condition;

import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.loader.ParamObtain;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class QuestConditionNeedFame extends QuestCondition {

	protected float fame;

	public QuestConditionNeedFame needFame(float fame) {
		this.fame = fame;
		return this;
	}

	public float getFame() {
		return fame;
	}

	@Override
	public void initWithConfig(JsonObject json, Map<String, Object> context) {
		needFame(ParamObtain.parser(json, "value", context, Number.class).floatValue());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setFloat("fame", fame);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		fame = nbt.getFloat("fame");
	}

	boolean checkResult = false;

	@Override
	public boolean onCheck(Quest task, EntityLivingBase player) {
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return checkResult = true;
		return checkResult = adventurer.getFame() > fame;
	}

	@Override
	public void onCheckDataSync(Quest quest, EntityLivingBase player, NBTTagCompound sendData) {
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return;
		sendData.setFloat("fame", adventurer.getFame());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onRecvDataSync(Quest quest, EntityLivingBase player, NBTTagCompound sendData) {
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return;
		adventurer.setFame(sendData.getFloat("fame"));
		this.onCheck(quest, player);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest quest, EntityLivingBase player, boolean dynamic) {
		boolean red = dynamic && !checkResult;
		if (red) return TextFormatting.DARK_RED + I18n.format("quest.reward.fame.need", fame);
		return I18n.format("quest.reward.fame.need", fame);
	}

}
