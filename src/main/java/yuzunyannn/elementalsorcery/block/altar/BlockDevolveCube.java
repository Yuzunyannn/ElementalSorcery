package yuzunyannn.elementalsorcery.block.altar;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.block.container.BlockContainerNormal;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.altar.TileDevolveCube;

public class BlockDevolveCube extends BlockContainerNormal {

	public static final AxisAlignedBB AXIS_BOX = new AxisAlignedBB(0.5 - 0.2, 0.5 - 0.3, 0.5 - 0.2, 0.5 + 0.2,
			0.5 + 0.3, 0.5 + 0.2);

	public BlockDevolveCube() {
		super(Material.GLASS, "devolveCube", 5f, MapColor.PURPLE);
		this.setLightLevel(0.75F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDevolveCube();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return true;
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_DEVOLVE_CUBE, worldIn, pos.getX(), pos.getY(),
				pos.getZ());
		return true;

	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AXIS_BOX;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

}
