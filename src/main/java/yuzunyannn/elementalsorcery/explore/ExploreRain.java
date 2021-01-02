package yuzunyannn.elementalsorcery.explore;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ExploreRain implements IExploreHandle {

	@Override
	public boolean explore(NBTTagCompound data, World world, BlockPos pos, int level, IBlockState state,
			EntityLivingBase portrait) {
		if (!data.hasKey("rainfall")) {
			Biome biome = world.getBiome(pos);
			data.setFloat("rainfall", biome.getRainfall());
			return false;
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addExploreInfo(NBTTagCompound data, List<String> tooltip) {
		if (!this.hasExplore(data)) return;
		float rainfall = data.getFloat("rainfall");
		tooltip.add(TextFormatting.GREEN + I18n.format("info.rainfall", rainfall));
	}

	@Override
	public boolean hasExplore(NBTTagCompound data) {
		return data.hasKey("rainfall");
	}

}
