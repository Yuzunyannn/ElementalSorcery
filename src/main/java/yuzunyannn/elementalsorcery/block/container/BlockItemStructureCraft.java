package yuzunyannn.elementalsorcery.block.container;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;

public abstract class BlockItemStructureCraft extends BlockContainerNormal {

	public BlockItemStructureCraft(String unlocalizedName) {
		super(Material.ROCK, unlocalizedName, 3.5F);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return face == EnumFacing.UP ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return true;
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_ITEM_STRUCTURE_CRAFT, worldIn, pos.getX(),
				pos.getY(), pos.getZ());
		return true;
	}

}
