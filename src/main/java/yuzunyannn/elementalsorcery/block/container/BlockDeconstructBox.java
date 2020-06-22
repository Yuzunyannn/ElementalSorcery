package yuzunyannn.elementalsorcery.block.container;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.TileDeconstructBox;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

@Deprecated
public class BlockDeconstructBox extends BlockContainer {

	public BlockDeconstructBox() {
		super(Material.ROCK);
		this.setUnlocalizedName("deconstructBox");
		this.setHardness(5.5F);
		this.setHarvestLevel("pickaxe", 1);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDeconstructBox();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	// 方块被破坏
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof TileDeconstructBox && !worldIn.isRemote) {
			IItemHandler item_handler;
			item_handler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			BlockHelper.drop(item_handler, worldIn, pos);
			item_handler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
			BlockHelper.drop(item_handler, worldIn, pos);
		}
		super.breakBlock(worldIn, pos, state);
	}

	// 点击
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing == EnumFacing.DOWN) return false;
		if (worldIn.isRemote) return true;
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_DECONSTRUCT_BOX, worldIn, pos.getX(), pos.getY(),
				pos.getZ());
		return true;
	}

}
