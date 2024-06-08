package yuzunyannn.elementalsorcery.block.device;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.tile.device.TileRingReader;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class BlockRingReader extends BlockDevice {

	public static final AxisAlignedBB BLOCK_AABB = new AxisAlignedBB(1.0 / 16.0, 0.0D, 1.0 / 16.0, 1 - 1.0 / 16.0,
			5.0 / 16.0, 1 - 1.0 / 16.0);

	public BlockRingReader() {
		super(Material.ROCK, "ringReader", 7.5F, MapColor.QUARTZ);
		this.setHarvestLevel("pickaxe", 2);
		autoDrop = true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileRingReader();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BLOCK_AABB;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileRingReader reader = BlockHelper.getTileEntity(worldIn, pos, TileRingReader.class);
		if (reader == null)
			return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);

		if (playerIn.isSneaking() || !reader.isOpenCover()) {
			if (worldIn.isRemote) return true;
			reader.setCoverOpen(!reader.isOpenCover());
			return true;
		}

		if (reader.isOpenCover())
			return BlockHelper.onBlockActivatedWithIGetItemStack(worldIn, pos, state, playerIn, hand, true);
		return false;
	}

}
