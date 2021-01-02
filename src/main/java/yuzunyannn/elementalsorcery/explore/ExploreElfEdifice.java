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
import yuzunyannn.elementalsorcery.block.BlockElfSapling;

public class ExploreElfEdifice implements IExploreHandle {

	@Override
	public boolean explore(NBTTagCompound data, World world, BlockPos pos, int level, IBlockState state,
			EntityLivingBase portrait) {
		if (!data.hasKey("elfTree")) {
			boolean can = world.provider.getDimension() == 0;
			can = can && BlockElfSapling.chunkCanGrow(world, pos);
			data.setBoolean("elfTree", can);
			return false;
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addExploreInfo(NBTTagCompound data, List<String> tooltip) {
		boolean elfTree = data.getBoolean("elfTree");
		if (elfTree) tooltip.add(TextFormatting.GREEN + I18n.format("info.can.grow.elf.edifice"));
	}

	@Override
	public boolean hasExplore(NBTTagCompound data) {
		return data.hasKey("elfTree");
	}

}
