package yuzunyannn.elementalsorcery.elf.quest.reward;

import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.elf.ElfConfig;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.loader.ParamObtain;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class QuestRewardFame extends QuestReward {

	protected float fame = 0;

	public QuestRewardFame fame(float coin) {
		this.fame = coin;
		return this;
	}

	public float getFame() {
		return fame;
	}

	@Override
	public void initWithConfig(JsonObject json, Map<String, Object> context) {
		fame(ParamObtain.parser(json, "value", context, Number.class).floatValue());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setFloat("fame", fame);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		fame = nbt.getFloat("fame");
	}

	@Override
	public void onReward(Quest quest, EntityLivingBase player) {
		ElfConfig.changeFame(player, fame);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest task, EntityLivingBase player) {
		return I18n.format("quest.reward.fame", fame);
	}
}
