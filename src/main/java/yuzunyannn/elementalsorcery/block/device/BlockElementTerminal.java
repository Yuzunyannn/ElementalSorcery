package yuzunyannn.elementalsorcery.block.device;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.block.altar.BlockElementContainer;
import yuzunyannn.elementalsorcery.tile.device.TileElementTerminal;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class BlockElementTerminal extends BlockDevice {

	public BlockElementTerminal() {
		super(Material.ROCK, "elementTerminal", 2.5f, MapColor.QUARTZ);
		this.setHarvestLevel("pickaxe", 2);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileElementTerminal();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) return false;
		return false;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tileEntiy,
			ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, tileEntiy, stack);
	}

	@Override
	public boolean canDropFromExplosion(Explosion explosionIn) {
		return false;
	}

	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		IBlockState state = world.getBlockState(pos);
		IElementInventory einv = BlockHelper.getElementInventory(world, pos, null);
		super.onBlockExploded(world, pos, explosion);
		if (world.isRemote) return;
		try {
			if (einv.isEmpty()) dropBlockAsItemWithChance(world, pos, state, 1, 0);
			else {
				EntityLivingBase attacker = explosion.getExplosivePlacedBy();
				BlockElementContainer.doExploded(world, pos, einv, attacker);
				dropBlockAsItemWithChance(world, pos, state, 0.75f, 0);
			}
		} catch (Exception e) {}
	}

}
