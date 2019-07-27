package yuzunyannn.elementalsorcery.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.tile.altar.TileSupremeCraftingTable;

public class BlockSupremeCraftingTable extends BlockContainer {

	public BlockSupremeCraftingTable() {
		super(Material.ROCK);
		this.setUnlocalizedName("supremeCraftingTable");
		this.setHardness(7.5F);
		this.setHarvestLevel("pickaxe", 2);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileSupremeCraftingTable();
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

}
