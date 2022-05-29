package yuzunyannn.elementalsorcery.block.container;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockContainerNormal extends BlockContainer {

	private static ThreadLocal<TileEntity> tileTemp = new ThreadLocal();

	public static void setDropTile(TileEntity tile) {
		tileTemp.set(tile);
	}

	public static TileEntity popDropTile() {
		TileEntity tile = tileTemp.get();
		tileTemp.set(null);
		return tile;
	}

	public static TileEntity getOrPopDropTile(IBlockAccess world, BlockPos pos) {
		TileEntity temp = popDropTile();
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null) return tile;
		if (temp != null && !temp.getPos().equals(pos)) return null;
		return temp;
	}

	protected BlockContainerNormal(Material materialIn) {
		super(materialIn);
	}

	protected BlockContainerNormal(Material materialIn, String unlocalizedName, float hardness, MapColor color) {
		super(materialIn, color);
		this.setTranslationKey(unlocalizedName);
		this.setHardness(hardness);
		if (materialIn == Material.WOOD) this.setHarvestLevel("axe", 1);
		else this.setHarvestLevel("pickaxe", 1);
		useNeighborBrightness = true;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}

}
