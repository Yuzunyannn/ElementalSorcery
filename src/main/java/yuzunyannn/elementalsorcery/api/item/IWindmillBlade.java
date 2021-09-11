package yuzunyannn.elementalsorcery.api.item;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IWindmillBlade {

	void bladeUpdate(World world, BlockPos pos, ItemStack stack, float speed, int tick);

	@Nullable
	@SideOnly(Side.CLIENT)
	default ResourceLocation getWindmillBladeSkin() {
		return null;
	}
}
