package yuzunyannn.elementalsorcery.item.tool;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.item.prop.ItemRelicGuardCore;

public class ItemRelicDisc extends Item {

	public ItemRelicDisc() {
		this.setTranslationKey("relicDisc");
		this.setMaxStackSize(1);
		this.setMaxDamage(512);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		EnumHand offHand = handIn == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
		ItemStack offStack = playerIn.getHeldItem(offHand);
		if (offStack.isEmpty()) return super.onItemRightClick(worldIn, playerIn, handIn);
		if (offStack.getItem() == ESObjects.ITEMS.RELIC_GUARD_CORE) {
			UUID uuid = ItemRelicGuardCore.getCoreMaster(offStack);
			if (playerIn.getUniqueID().equals(uuid)) return super.onItemRightClick(worldIn, playerIn, handIn);
			ItemStack stack = playerIn.getHeldItem(handIn);
			if (worldIn.isRemote) {
				playerIn.sendMessage(new TextComponentTranslation("info.relic.bind.master", playerIn.getName()));
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
			ItemRelicGuardCore.setCoreMaster(offStack, playerIn);
			stack.damageItem(32, playerIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

}
