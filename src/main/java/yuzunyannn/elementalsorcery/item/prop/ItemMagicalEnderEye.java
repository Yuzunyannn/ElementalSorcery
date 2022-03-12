package yuzunyannn.elementalsorcery.item.prop;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEnderEye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.init.ESInit;

public class ItemMagicalEnderEye extends ItemEnderEye {

	public ItemMagicalEnderEye() {
		this.setTranslationKey("magicalEnderEye");
		this.setMaxStackSize(16);
		this.setMaxDamage(20);
	}

	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		EnumActionResult result = super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		if (!worldIn.isRemote && result == EnumActionResult.SUCCESS) {
			ItemStack stack = player.getHeldItem(hand);
			int n = (int) (Math.random() * ((float) (stack.getMaxDamage() - stack.getItemDamage()))
					/ stack.getMaxDamage() * 4 + 1);
			worldIn.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.5F, false);
			Block.spawnAsEntity(worldIn, pos.up(), new ItemStack(ESInit.ITEMS.MAGIC_PIECE, n));
		}
		return result;
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ActionResult<ItemStack> result = super.onItemRightClick(worldIn, playerIn, handIn);
		if (!worldIn.isRemote && result.getType() == EnumActionResult.SUCCESS) {
			ItemStack stack = playerIn.getHeldItem(handIn);
			int n = (int) (Math.random() * ((float) (stack.getMaxDamage() - stack.getItemDamage()))
					/ stack.getMaxDamage() * 4 + 1);
			worldIn.createExplosion(null, playerIn.posX, playerIn.posY + 1.5, playerIn.posZ, 1.5F, false);
			Block.spawnAsEntity(worldIn, playerIn.getPosition().up(),
					new ItemStack(ESInit.ITEMS.MAGIC_PIECE, n));
		}
		return result;
	}
}
