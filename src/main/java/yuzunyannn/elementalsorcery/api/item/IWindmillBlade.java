package yuzunyannn.elementalsorcery.api.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;

public interface IWindmillBlade {

	void bladeUpdate(World world, BlockPos pos, ItemStack stack, List<ElementStack> outList, float speed, int tick);

	float bladeWindScale(World world, BlockPos pos, ItemStack stack);

	@Nullable
	@SideOnly(Side.CLIENT)
	default ResourceLocation getWindmillBladeSkin() {
		return null;
	}
}
