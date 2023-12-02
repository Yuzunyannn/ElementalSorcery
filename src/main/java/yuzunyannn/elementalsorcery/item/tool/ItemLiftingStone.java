package yuzunyannn.elementalsorcery.item.tool;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraEnderTeleport;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemLiftingStone extends Item implements TileMDRubbleRepair.IExtendRepair {

	public ItemLiftingStone() {
		this.setTranslationKey("liftingStone");
		this.setMaxStackSize(1);
		this.setMaxDamage(128);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		BlockPos pos = new BlockPos(playerIn.posX, playerIn.posY - 0.5, playerIn.posZ);
		IBlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() != Blocks.END_STONE) return super.onItemRightClick(worldIn, playerIn, handIn);

		ItemStack stack = playerIn.getHeldItem(handIn);
		if (stack.getItemDamage() >= stack.getMaxDamage())
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);

		if (worldIn.isRemote) return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

		BlockPos nextPos = null;
		final int MAX_FIND_LENGTH = 32;
		BlockPos at = pos;
		EnumFacing facing = playerIn.isSneaking() ? EnumFacing.DOWN : EnumFacing.UP;

		for (int i = 0; i < MAX_FIND_LENGTH; i++) {
			at = at.offset(facing);
			if (worldIn.getBlockState(at).getBlock() != Blocks.END_STONE) continue;
			nextPos = at;
			break;
		}

		if (nextPos == null) return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);

		nextPos = nextPos.up();
		Vec3d to = new Vec3d(nextPos).add(0.5, 0, 0.5);

		if (!WorldHelper.canMoveEntityTo(playerIn, to))
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);

		stack.setItemDamage(stack.getItemDamage() + 1);

		EventServer.allowInDungeonTeleport = true;
		MantraEnderTeleport.doEnderTeleport(worldIn, playerIn, to);
		MantraEnderTeleport.playEnderTeleportEffect(worldIn, playerIn, to);
		EventServer.allowInDungeonTeleport = false;

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("info.dungeon.liftingStone"));
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	@Override
	public ItemStack getRepairOutput(ItemStack input) {
		if (input.getItemDamage() <= 0) return ItemStack.EMPTY;
		input = input.copy();
		input.setItemDamage(input.getItemDamage() - 1);
		return input;
	}

	@Override
	public int getRepairCost(ItemStack input) {
		return 200;
	}
}
