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

	private static IElementInventory einvTemp;

	public static void setDropElementInventory(IElementInventory einv) {
		einvTemp = einv;
	}

	public static IElementInventory popDropElementInventory() {
		IElementInventory einv = einvTemp;
		einvTemp = null;
		return einv;
	}

	public static IElementInventory getOrPopDropElementInventory(IBlockAccess world, BlockPos pos) {
		IElementInventory temp = popDropElementInventory();
		IElementInventory einv = BlockHelper.getElementInventory(world, pos, null);
		if (einv != null) return einv;
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
		ElementHelper.addElementInformation(inventory, worldIn, tooltip, flagIn);
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
		IElementInventory einv = BlockHelper.getElementInventory(worldIn, pos, null);
		if (einv != null) setDropElementInventory(einv);
		super.breakBlock(worldIn, pos, state);
	}

	// 掉落物
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		ItemStack stack = new ItemStack(this);
		drops.add(stack);
		IElementInventory eInv = getOrPopDropElementInventory(world, pos);
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
