package yuzunyannn.elementalsorcery.api.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public interface IWindmillBlade {

	/**
	 * @return true表示需要将stack数据更新到客户端
	 */
	boolean bladeUpdate(World world, BlockPos pos, ItemStack stack, List<ElementStack> outList, float speed, int tick);

	float bladeWindScale(World world, BlockPos pos, ItemStack stack);

	boolean canTwirl(World world, BlockPos pos, ItemStack stack);

	void bladePitch(World world, Vec3d pos, ItemStack stack, IWindmillBladeController blate);

	@Nullable
	@SideOnly(Side.CLIENT)
	default ResourceLocation getWindmillBladeSkin() {
		return null;
	}

	@SideOnly(Side.CLIENT)
	default boolean isWindmillBladeSkinNeedBlend() {
		return false;
	}
}
