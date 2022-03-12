package yuzunyannn.elementalsorcery.elf.quest.reward;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.elf.edifice.ElfEdificeFloor;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class QuestRewardElfTreeInvest extends QuestReward {

	protected ElfEdificeFloor type;
	protected BlockPos elfCorePos;
	protected int weight = 1;

	public QuestRewardElfTreeInvest floor(ElfEdificeFloor type, BlockPos corePos, int weight) {
		this.type = type;
		this.elfCorePos = corePos;
		this.weight = weight;
		return this;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		if (type != null) {
			nbt.setString("fId", type.getRegistryName().toString());
			NBTHelper.setBlockPos(nbt, "pos", elfCorePos);
			nbt.setInteger("weight", weight);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		type = ElfEdificeFloor.REGISTRY.getValue(new ResourceLocation(nbt.getString("fId")));
		elfCorePos = NBTHelper.getBlockPos(nbt, "pos");
		weight = nbt.getInteger("weight");
	}

	@Override
	public void onReward(Quest quest, EntityLivingBase player) {
		if (type == null) return;
		World world = player.world;
		world.getChunk(elfCorePos);
		TileElfTreeCore core = BlockHelper.getTileEntity(world, elfCorePos, TileElfTreeCore.class);
		if (core == null) return;
		core.floorInvest(type, weight);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest task, EntityLivingBase player) {
		if (type == null) return "";
		String floorName = I18n.format("floor." + type.getTranslationKey() + ".name");
		return I18n.format("quest.reward.elf.invest", floorName, weight);
	}
}
