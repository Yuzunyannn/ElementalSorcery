package yuzunyannn.elementalsorcery.block.device;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.block.container.BlockContainerNormal;
import yuzunyannn.elementalsorcery.tile.device.TileMantraEmitter;

public class BlockMantraEmitter extends BlockContainerNormal {

	public BlockMantraEmitter() {
		super(Material.ROCK, "mantraEmitter", 7.5F, MapColor.QUARTZ);
		this.setHarvestLevel("pickaxe", 2);
		autoDrop = true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMantraEmitter();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing == EnumFacing.DOWN) return false;
		if (worldIn.isRemote) return true;
//		TileEntity tile = worldIn.getTileEntity(pos);
//		if (tile instanceof TileStaticMultiBlock) {
//			if (((TileStaticMultiBlock) tile).isAndCheckIntact()) {
//				playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_SUPREME_TABLE, worldIn, pos.getX(),
//						pos.getY(), pos.getZ());
//				return true;
//			}
//		}
//		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_INVENTORY_WORKBENCH, worldIn, pos.getX(),
//				pos.getY(), pos.getZ());
		return true;
	}

}
