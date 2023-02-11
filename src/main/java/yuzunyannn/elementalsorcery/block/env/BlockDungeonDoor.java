package yuzunyannn.elementalsorcery.block.env;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.tile.TileDungeonDoor;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;

public class BlockDungeonDoor extends BlockContainer {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockDungeonDoor() {
		super(Material.ROCK);
		this.setSoundType(SoundType.GLASS);
		this.setTranslationKey("dungeonDoorCore");
		this.setHardness(32f);
		this.setLightLevel(4);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDungeonDoor();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.byHorizontalIndex(meta & 3);
		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()));
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te,
			ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, te, stack);
		onHarvestDoor(worldIn, player, pos);
	}

	public static void onHarvestDoor(World worldIn, EntityPlayer player, BlockPos pos) {
		if (worldIn.isRemote) return;
		if (EntityHelper.isCreative(player)) return;
		EventServer.addWorldTask(worldIn, (w) -> {
			for (EnumFacing facing : EnumFacing.VALUES) {
				BlockPos at = pos.offset(facing);
				IBlockState blockState = w.getBlockState(at);
				if (blockState.getBlock() instanceof BlockDungeonDoorExpand
						|| blockState.getBlock() instanceof BlockDungeonDoor) {
					onHarvestDoor(worldIn, player, at);
					w.destroyBlock(at, false);
				}
			}
		});
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileDungeonDoor tile = BlockHelper.getTileEntity(worldIn, pos, TileDungeonDoor.class);
		if (tile != null) tile.onBreak();
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileDungeonDoor tile = BlockHelper.getTileEntity(worldIn, pos, TileDungeonDoor.class);
		if (tile != null) {
			tile.onBlockActivated(playerIn, hand, facing, state.getValue(FACING));
			return true;
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

}
