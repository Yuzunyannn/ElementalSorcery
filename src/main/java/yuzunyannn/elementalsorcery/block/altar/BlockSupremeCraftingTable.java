package yuzunyannn.elementalsorcery.block.altar;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.block.container.BlockContainerNormal;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.altar.TileStaticMultiBlock;
import yuzunyannn.elementalsorcery.tile.altar.TileSupremeCraftingTable;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;
import yuzunyannn.elementalsorcery.util.item.IItemStackHandlerInventory;

public class BlockSupremeCraftingTable extends BlockContainerNormal {

	public BlockSupremeCraftingTable() {
		super(Material.ROCK, "supremeCraftingTable", 7.5F);
		this.setHarvestLevel("pickaxe", 2);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileSupremeCraftingTable();
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof IItemStackHandlerInventory && !worldIn.isRemote) {
			IItemHandler itemHandler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			BlockHelper.drop(itemHandler, worldIn, pos);
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing == EnumFacing.DOWN)
			return false;
		if (worldIn.isRemote)
			return true;
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileStaticMultiBlock) {
			if (((TileStaticMultiBlock) tile).isIntact()) {
				playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_SUPREME_CRAFTING_TABLE, worldIn,
						pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
		}
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_INVENTORY_WORKBENCH, worldIn, pos.getX(),
				pos.getY(), pos.getZ());
		return true;
	}

}
