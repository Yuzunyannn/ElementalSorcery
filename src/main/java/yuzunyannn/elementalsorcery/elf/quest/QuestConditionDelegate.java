package yuzunyannn.elementalsorcery.elf.quest;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class QuestConditionDelegate extends QuestCondition {

	protected String name = "";

	public QuestConditionDelegate delegate(EntityPlayer player) {
		this.name = player == null ? "" : player.getName();
		return this;
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
	public boolean check(Quest task, EntityPlayer player) {
		return checkResult = player.getName().equals(name);
	}

	@Override
	public void finish(Quest task, EntityPlayer player) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest quest, EntityLivingBase player, boolean dynamic) {
		boolean red = dynamic && !checkResult;
		if (red) return TextFormatting.DARK_RED + I18n.format("quest.delegate", name);
		return I18n.format("quest.delegate", name);
	}

}
