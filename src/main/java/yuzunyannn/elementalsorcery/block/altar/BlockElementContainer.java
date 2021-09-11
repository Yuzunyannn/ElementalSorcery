package yuzunyannn.elementalsorcery.block.altar;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public abstract class BlockElementContainer extends BlockContainer {

	private static TileEntity tileTemp;

	public static void setDropTile(TileEntity tile) {
		tileTemp = tile;
	}

	public static TileEntity popDropTile() {
		TileEntity tile = tileTemp;
		tileTemp = null;
		return tile;
	}

	public static TileEntity getOrPopDropTile(IBlockAccess world, BlockPos pos) {
		TileEntity temp = popDropTile();
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null) return tile;
		return temp;
	}

	public BlockElementContainer(Material materialIn) {
		super(materialIn);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		IElementInventory inventory = ElementHelper.getElementInventory(stack);
		if (inventory == null) return;
		inventory.addInformation(worldIn, tooltip, flagIn);
	}

	// 不是完成方块哟
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	// 是透明方块哟
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	// 要有破坏进度
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}

	// 破坏方块
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity einv = worldIn.getTileEntity(pos);
		if (einv != null) setDropTile(einv);
		super.breakBlock(worldIn, pos, state);
	}

	// 掉落物
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		ItemStack stack = new ItemStack(this);
		drops.add(stack);

		TileEntity tile = getOrPopDropTile(world, pos);
		this.modifyDropStack(world, pos, stack, tile);
	}

	protected void modifyDropStack(IBlockAccess world, BlockPos pos, ItemStack stack, TileEntity originTile) {
		IElementInventory eInv = ElementHelper.getElementInventory(originTile);
		if (eInv == null) return;

		IElementInventory itemInv = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		ElementHelper.toElementInventory(eInv, itemInv);
		itemInv.getStackInSlot(0).grow(-1);
		itemInv.saveState(stack);
	}

	// 当被放置
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase player,
			ItemStack stack) {
		IElementInventory einv = BlockHelper.getElementInventory(worldIn, pos, null);
		if (einv == null) return;
		if (player instanceof EntityPlayer) {
			if (((EntityPlayer) player).isCreative()) {
				stack = stack.copy();
			}
		}
		ElementHelper.toElementInventory(ElementHelper.getElementInventory(stack), einv);
	}

}
