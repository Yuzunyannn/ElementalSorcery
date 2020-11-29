package yuzunyannn.elementalsorcery.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.tile.TileLifeDirt;

public class BlockLifeDirt extends Block {

	public BlockLifeDirt() {
		super(Material.GROUND);
		this.setSoundType(SoundType.GROUND);
		this.setUnlocalizedName("lifeDirt");
		this.setHardness(0.5F);
		this.setHarvestLevel("shovel", 0);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ItemStack itemStack = TileLifeDirt.getPlant(stack);
		if (itemStack.isEmpty()) return;
		tooltip.add(TextFormatting.YELLOW + I18n.format(itemStack.getUnlocalizedName() + ".name"));
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		TileLifeDirt tile = TileLifeDirt.checkAndCreate(stack);
		if (tile == null) return;
		worldIn.setTileEntity(pos, tile);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		worldIn.removeTileEntity(pos);
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction,
			IPlantable plantable) {
		return direction == EnumFacing.UP;
	}
}
