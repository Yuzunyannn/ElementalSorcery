package yuzunyannn.elementalsorcery.block.env;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.tile.dungeon.TileDungeonHaystack;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class BlockDungeonHaystack extends BlockDungeonPropBase {

	public BlockDungeonHaystack() {
		super(Material.PLANTS);
		this.setSoundType(SoundType.PLANT);
		this.setTranslationKey("dungeonHaystack");
		this.setHardness(0);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDungeonHaystack();
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) || !worldIn.isAirBlock(pos.down());
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (worldIn.isAirBlock(pos.down())) worldIn.destroyBlock(pos, true);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		TileDungeonHaystack tile = BlockHelper.getTileEntity(source, pos, TileDungeonHaystack.class);
		if (tile != null) return tile.getBoundingBox();
		return super.getBoundingBox(state, source, pos);
	}

}
