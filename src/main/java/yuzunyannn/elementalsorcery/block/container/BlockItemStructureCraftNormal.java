package yuzunyannn.elementalsorcery.block.container;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.block.BlockInvalidEnchantmentTable;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraftNormal;

public class BlockItemStructureCraftNormal extends BlockItemStructureCraft {

	public BlockItemStructureCraftNormal() {
		super("ISCraftNormal");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileItemStructureCraftNormal();
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BlockInvalidEnchantmentTable.AABB;
	}
}
