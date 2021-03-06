package yuzunyannn.elementalsorcery.elf.quest;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;

public class QuestRewardItemParcel extends QuestRewardItem {

	protected boolean hidden = false;

	public static QuestRewardItemParcel create(ItemStack... stacks) {
		return (QuestRewardItemParcel) QuestReward.REGISTRY.newInstance(QuestRewardItemParcel.class).item(stacks);
	}

	public QuestRewardItemParcel hide(boolean isHidden) {
		this.hidden = isHidden;
		return this;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("hidden", hidden);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		hidden = nbt.getBoolean("hidden");
	}

	@Override
	public void reward(Quest quest, EntityPlayer player) {
		ElfPostOffice postOffice = ElfPostOffice.getPostOffice(player.world);
		EntityLivingBase fakeSender = new EntityElf(player.world);
		postOffice.pushParcel(fakeSender, player, stacks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest task, EntityLivingBase player) {
		if (hidden) return "";
		return I18n.format("quest.reward.parcel", super.getDescribe(task, player));
	}
}
