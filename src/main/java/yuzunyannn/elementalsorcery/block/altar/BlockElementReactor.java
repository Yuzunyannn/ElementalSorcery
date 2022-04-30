package yuzunyannn.elementalsorcery.block.altar;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.block.container.BlockContainerNormal;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor;
import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor.ReactorStatus;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class BlockElementReactor extends BlockContainerNormal {

	public BlockElementReactor() {
		super(Material.GLASS, "elementReactor", 6.5F, MapColor.GRAY);
		this.setHarvestLevel("pickaxe", 0);
		this.setSoundType(SoundType.GLASS);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileElementReactor();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return true;
		TileElementReactor reactor = BlockHelper.getTileEntity(worldIn, pos, TileElementReactor.class);
		if (reactor == null) return false;
		if (reactor.getStatus() == ReactorStatus.OFF) return false;
		player.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_ELEMENT_REACTOR, player.world, pos.getX(),
				pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileElementReactor reactor = BlockHelper.getTileEntity(worldIn, pos, TileElementReactor.class);
		if (reactor != null) reactor.onBreak();
		super.breakBlock(worldIn, pos, state);
	}
}
