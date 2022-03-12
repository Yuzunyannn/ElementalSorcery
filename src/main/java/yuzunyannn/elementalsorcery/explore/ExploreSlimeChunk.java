package yuzunyannn.elementalsorcery.explore;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ExploreSlimeChunk implements IExploreHandle {

	public static boolean isSlimeChunk(World world, BlockPos pos) {
		Chunk chunk = world.getChunk(pos);
		return chunk.getRandomWithSeed(987234911L).nextInt(10) == 0;
	}

	@Override
	public boolean explore(NBTTagCompound data, World world, BlockPos pos, int level, IBlockState state,
			EntityLivingBase portrait) {
		if (!data.hasKey("slime")) {
			boolean can = world.provider.getDimension() == 0;
			can = can && isSlimeChunk(world, pos);
			data.setBoolean("slime", can);
			return false;
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addExploreInfo(NBTTagCompound data, List<String> tooltip) {
		boolean slime = data.getBoolean("slime");
		if (slime) tooltip.add(TextFormatting.DARK_GREEN + I18n.format("info.slime.chunk"));
	}

	@Override
	public boolean hasExplore(NBTTagCompound data) {
		return data.hasKey("slime");
	}

}
