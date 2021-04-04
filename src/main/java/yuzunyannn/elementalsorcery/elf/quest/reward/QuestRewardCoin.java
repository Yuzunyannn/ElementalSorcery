package yuzunyannn.elementalsorcery.elf.quest.reward;

import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.loader.ParamObtain;
import yuzunyannn.elementalsorcery.item.ItemElfPurse;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class QuestRewardCoin extends QuestReward {

	protected int coin = 0;

	public QuestRewardCoin coin(int coin) {
		this.coin = coin;
		return this;
	}

	@Override
	public void initWithConfig(JsonObject json, Map<String, Object> context) {
		coin(ParamObtain.parser(json, "value", context, Number.class).intValue());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("coin", coin);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		coin = nbt.getInteger("coin");
	}

	@Override
	public void onReward(Quest quest, EntityLivingBase player) {
		if (player instanceof EntityPlayer) {
			ItemElfPurse.insert((EntityPlayer) player, coin);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest task, EntityLivingBase player) {
		return I18n.format("quest.reward.coin", coin);
	}
}
