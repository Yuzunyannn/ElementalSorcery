package yuzunyannn.elementalsorcery.elf.quest.reward;

import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

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
	public void initWithConfig(JsonObject json, Map<String, Object> context) {
		if (json.hasBoolean("hide")) hide(json.getBoolean("hide"));
		super.initWithConfig(json, context);
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
	public void onReward(Quest quest, EntityLivingBase player) {
		if (!(player instanceof EntityPlayer)) return;
		ElfPostOffice postOffice = ElfPostOffice.getPostOffice(player.world);
		EntityLiving fakeSender = new EntityElf(player.world);
		fakeSender.onInitialSpawn(fakeSender.world.getDifficultyForLocation(new BlockPos(fakeSender)), null);
		postOffice.pushParcel(fakeSender, (EntityPlayer) player, stacks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest task, EntityLivingBase player) {
		if (hidden) return "";
		return I18n.format("quest.reward.parcel", super.getDescribe(task, player));
	}
}
