package yuzunyannn.elementalsorcery.block.altar;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.material.MapColor;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.block.container.BlockContainerNormal;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public abstract class BlockElementContainer extends BlockContainerNormal {

	public BlockElementContainer(Material materialIn, String unlocalizedName, float hardness, MapColor color) {
		super(materialIn, unlocalizedName, hardness, color);
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

	// 破坏方块
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity einv = worldIn.getTileEntity(pos);
		if (einv != null) BlockContainerNormal.setDropTile(einv);
		super.breakBlock(worldIn, pos, state);
	}

	// 掉落物
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		ItemStack stack = new ItemStack(this);
		drops.add(stack);

		TileEntity tile = BlockContainerNormal.getOrPopDropTile(world, pos);
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
		EntityLivingBase attacker = explosion.getExplosivePlacedBy();
		doExploded(world, pos, einv, attacker);
		try {
			dropBlockAsItemWithChance(world, pos, state, 0.75f, 0);
		} catch (Exception e) {}
	}

	public static void doExploded(World world, BlockPos pos, IElementInventory einv, EntityLivingBase attacker) {
		if (einv == null) return;
		Vec3d at = new Vec3d(pos).add(0.5, 0.5, 0.5);
		for (int i = 0; i < einv.getSlots(); i++) {
			ElementStack stack = einv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			stack = stack.copy();
			stack.grow(50);
			stack.setPower(stack.getPower() + 125);
			if (ElementExplosion.doExplosion(world, at, stack, attacker) != null)
				einv.setStackInSlot(i, ElementStack.EMPTY);
		}
	}

}
