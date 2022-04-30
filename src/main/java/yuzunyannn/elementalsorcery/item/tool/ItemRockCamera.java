package yuzunyannn.elementalsorcery.item.tool;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.explore.ExploreManagement;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.ItemNatureDust;
import yuzunyannn.elementalsorcery.item.ItemNatureDust.EnumType;
import yuzunyannn.elementalsorcery.item.crystal.ItemNatureCrystal;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.scrappy.EffectFlash;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemRockCamera extends Item {

	public ItemRockCamera() {
		this.setTranslationKey("rockCamera");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
		RayTraceResult result = WorldHelper.getLookAtBlock(world, player, 16);
		if (result == null) return new ActionResult(EnumActionResult.PASS, player.getHeldItem(handIn));
		int level = 2;
		if (!player.isCreative()) {
			ItemStack dust = findDust(player, handIn);
			if (dust.isEmpty()) return new ActionResult(EnumActionResult.PASS, player.getHeldItem(handIn));
			dust.shrink(1);
			level = dust.getMetadata();
		}
		this.photograph(world, player, result.getBlockPos(), level);
		if (player instanceof EntityPlayerMP)
			ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "use:rockCamera");
		return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
	}

	public void photograph(World world, EntityPlayer player, BlockPos at, int level) {
		if (world.isAirBlock(at)) return;
		if (world.isRemote) {
			this.showFlashEffect(player);
			return;
		}
		ItemStack crystal = new ItemStack(ESInit.ITEMS.NATURE_CRYSTAL);
		NBTTagCompound data = ItemNatureCrystal.getOrCreateData(crystal);
		IBlockState state = world.getBlockState(at);
		for (int i = 0; i < 256; i++) {
			boolean ok = ExploreManagement.instance.explore(data, world, at, level, state, null);
			if (ok) break;
		}
		ItemHelper.addItemStackToPlayer(player, crystal);
	}

	public ItemStack findDust(EntityPlayer player, EnumHand handIn) {
		// 优先反手
		EnumHand hand = handIn == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
		ItemStack item = player.getHeldItem(hand);
		if (item.getItem() == ESInit.ITEMS.NATURE_DUST
				&& item.getMetadata() != ItemNatureDust.EnumType.NATURE.getMetadata())
			return item;
		// 否则从包里找
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			item = player.inventory.getStackInSlot(i);
			if (item.isEmpty()) continue;
			if (item.getItem() == ESInit.ITEMS.NATURE_DUST
					&& item.getMetadata() != ItemNatureDust.EnumType.NATURE.getMetadata())
				return item;
		}
		return ItemStack.EMPTY;
	}

	@SideOnly(Side.CLIENT)
	public void showFlashEffect(EntityPlayer player) {
		if (player == Minecraft.getMinecraft().player && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
			EffectFlash flash = new EffectFlash(player.world);
			Effect.addEffect(flash);
		}
	}

}
