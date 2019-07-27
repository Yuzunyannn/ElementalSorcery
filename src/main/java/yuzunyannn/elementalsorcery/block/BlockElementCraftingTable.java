package yuzunyannn.elementalsorcery.block;

import net.minecraft.block.BlockContainer;
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
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.altar.TileElementCraftingTable;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class BlockElementCraftingTable extends BlockContainer {
	public BlockElementCraftingTable() {
		super(Material.ROCK);
		this.setUnlocalizedName("elementCraftingTable");
		this.setHardness(5.5F);
		this.setHarvestLevel("pickaxe", 1);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileElementCraftingTable();
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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote)
			return true;
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_ELEMENT_CRAFTING_TABLE, worldIn, pos.getX(),
				pos.getY(), pos.getZ());
		return true;

	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof TileElementCraftingTable && !worldIn.isRemote) {
			IItemHandler item_handler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			BlockHelper.drop(item_handler, worldIn, pos);
		}
		super.breakBlock(worldIn, pos, state);
	}

}
