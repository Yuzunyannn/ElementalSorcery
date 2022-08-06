package yuzunyannn.elementalsorcery.block.altar;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.block.container.BlockContainerNormal;
import yuzunyannn.elementalsorcery.tile.altar.TileDisintegrateStela;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class BlockDisintegrateStela extends BlockContainerNormal {

	public BlockDisintegrateStela() {
		super(Material.ROCK, "disintegrateStela", 5.5F, MapColor.QUARTZ);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDisintegrateStela();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (ESAPI.isDevelop && playerIn.isCreative() && playerIn.isSneaking()) {
			TileDisintegrateStela.doOverloadExplosion(worldIn, pos);
			return true;
		}
		if (BlockHelper.onBlockActivatedWithIGetItemStack(worldIn, pos, state, playerIn, hand, true)) {
			if (playerIn instanceof EntityPlayerMP)
				ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) playerIn, "use:disintegrate");
			return true;
		}
		return false;
	}

}
