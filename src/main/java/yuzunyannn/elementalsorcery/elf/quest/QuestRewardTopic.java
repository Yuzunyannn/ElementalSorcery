package yuzunyannn.elementalsorcery.elf.quest;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.elf.research.Researcher;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;

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
	public void reward(Quest quest, EntityPlayer player) {
		Researcher researcher = new Researcher(player);
		researcher.grow(type, count);
		researcher.save(player);
		ItemAncientPaper.sendTopicGrowMessage(player, type);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest task, EntityLivingBase player) {
		return "";
	}
}
