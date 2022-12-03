package yuzunyannn.elementalsorcery.elf.quest.condition;

import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.loader.ParamObtain;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestRewardExp;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class QuestConditionDelegate extends QuestCondition {

	public static QuestConditionDelegate create(EntityLivingBase player) {
		return REGISTRY.newInstance(QuestConditionDelegate.class).delegate(player);
	}

	protected String name = "";

	public QuestConditionDelegate delegate(EntityLivingBase player) {
		this.name = player == null ? "" : player.getName();
		return this;
	}

	@Override
	public void initWithConfig(JsonObject json, Map<String, Object> context) {
		Object obj = ParamObtain.parser(json.needString("value"), context);
		if (obj instanceof String) this.name = obj.toString();
		else delegate((EntityPlayer) obj);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setString("name", this.name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		name = nbt.getString("name");
	}

	boolean checkResult = false;

	@Override
	public boolean onCheck(Quest task, EntityLivingBase player) {
		return checkResult = player.getName().equals(name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest quest, EntityLivingBase player, boolean dynamic) {
		boolean red = dynamic && !checkResult;
		if (red) return TextFormatting.DARK_RED + I18n.format("quest.delegate", name);
		return I18n.format("quest.delegate", name);
	}

}
