package yuzunyannn.elementalsorcery.elf.quest;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class QuestRewardExp extends QuestReward {

	public static QuestRewardExp create(int exp) {
		return REGISTRY.newInstance(QuestRewardExp.class).exp(exp);
	}

	protected int exp = 0;

	public QuestRewardExp exp(int coin) {
		this.exp = coin;
		return this;
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
	public void reward(Quest quest, EntityPlayer player) {
		WorldHelper.createExpBall(player, exp);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest task, EntityLivingBase player) {
		return I18n.format("quest.reward.exp", exp);
	}
}
