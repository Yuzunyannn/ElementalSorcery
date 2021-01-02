package yuzunyannn.elementalsorcery.explore;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ExploreBlockData implements IExploreHandle {

	@Override
	public boolean explore(NBTTagCompound data, World world, BlockPos pos, int level, IBlockState state,
			EntityLivingBase portrait) {
		if (state == null) return true;
		if (!data.hasKey("hardness")) {
			data.setFloat("hardness", state.getBlock().getBlockHardness(state, world, pos));
			return false;
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addExploreInfo(NBTTagCompound data, List<String> tooltip) {
		if (!this.hasExplore(data)) return;
		float hardness = data.getInteger("hardness");
		if (hardness == -1) tooltip.add(TextFormatting.GREEN + I18n.format("info.attr.hardness", "∞"));
		else tooltip.add(TextFormatting.GREEN + I18n.format("info.attr.hardness", String.format("%.2f", hardness)));
	}

	@Override
	public boolean hasExplore(NBTTagCompound data) {
		return data.hasKey("hardness");
	}

}
