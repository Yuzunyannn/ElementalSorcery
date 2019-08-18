package yuzunyannn.elementalsorcery.block;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockContainerNormal extends BlockContainer {

	protected BlockContainerNormal(Material materialIn) {
		super(materialIn);
	}

	protected BlockContainerNormal(Material materialIn, String unlocalizedName, float hardness) {
		super(materialIn);
		this.setUnlocalizedName(unlocalizedName);
		this.setHardness(hardness);
		if (materialIn == Material.ROCK)
			this.setHarvestLevel("pickaxe", 1);
		else if (materialIn == Material.WOOD)
			this.setHarvestLevel("axe", 1);
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
