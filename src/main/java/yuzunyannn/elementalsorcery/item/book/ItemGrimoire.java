package yuzunyannn.elementalsorcery.item.book;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.client.IRenderLayoutFix;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.grimoire.AttackCaster;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.ExceptionHelper;

public class ItemGrimoire extends Item implements IRenderLayoutFix {

	public ItemGrimoire() {
		this.setTranslationKey("grimoire");
		this.setMaxStackSize(1);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			ItemStack stack = new ItemStack(this);
			Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
			grimoire.setCapacityMax(Short.MAX_VALUE);
			grimoire.saveState(stack);
			items.add(stack);
		}
	}

	public IElementInventory initElementInventory(ItemStack stack) {
		return new ElementInventory(4);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new Grimoire.Provider(this.initElementInventory(stack));
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BLOCK;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
		if (player.isHandActive()) return false;
		return super.onDroppedByPlayer(item, player);
	}

	@Override
	public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
		return oldStack.getItem() == newStack.getItem();
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		// 没有能力的，可能是别的内容
		if (!stack.hasCapability(Grimoire.GRIMOIRE_CAPABILITY, null))
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		// 必须主手
		if (handIn != EnumHand.MAIN_HAND) return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		// 开始释放！
		grimoire.tryLoadState(stack);
		// 获取咒文
		Grimoire.Info info = grimoire.getSelectedInfo();
		Mantra mantra = info == null ? null : info.getMantra();
		if (mantra == null) return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		if (!mantra.canStart(playerIn)) return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		// 开始
		EntityGrimoire.start(worldIn, playerIn, grimoire);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity targetEntity) {
		float f = player.getCooledAttackStrength(0);
		if (f != 1) return false;

		Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		if (grimoire == null) return false;
		grimoire.loadState(stack);
		Grimoire.Info info = grimoire.getInfo(grimoire.getSelected());
		if (info == null) return false;

		try {
			AttackCaster caster = new AttackCaster(player, grimoire);
			boolean attack = info.getMantra().canPotentAttack(player.world, stack, caster, targetEntity);
			if (!attack) return false;

			info.getMantra().potentAttack(player.world, stack, caster, targetEntity);
			if (!player.world.isRemote) grimoire.saveState(stack);
			player.resetCooldown();
		} catch (Exception e) {
			ESAPI.logger.warn("强效攻击出现异常", e);
			ExceptionHelper.warnSend(player.world, "强效攻击出现异常");
			return false;
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			tooltip.add(TextFormatting.GOLD + I18n.format("info.grimoire.nothing"));
			return;
		}

		Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		if (grimoire == null) return;
		grimoire.loadState(stack);

		int cap = Math.max(0, grimoire.getCapacityMax() - grimoire.getCapacity());
		tooltip.add(I18n.format("info.grimoire.blank", cap));
		// 咒文信息
		if (grimoire.isEmpty()) tooltip.add(TextFormatting.GOLD + I18n.format("info.grimoire.nothing"));
		else {
			tooltip.add(TextFormatting.GOLD + I18n.format("info.grimoire.record", grimoire.size()));
			Grimoire.Info info = grimoire.getInfo(grimoire.getSelected());
			if (info != null) {
				Mantra m = info.getMantra();
				if (m == null) tooltip.add(TextFormatting.AQUA + I18n.format("info.grimoire.error"));
				else {
					String name = m.getDisplayName() + TextFormatting.RESET + TextFormatting.AQUA;
					tooltip.add(TextFormatting.AQUA + I18n.format("info.grimoire.current", name));
					info.getMantra().addInformation(stack, worldIn, tooltip, flagIn);
				}
			} else tooltip.add(TextFormatting.AQUA + I18n.format("info.grimoire.error"));
		}
		// 元素信息
		IElementInventory inventory = grimoire.getInventory();
		if (inventory == null) return;
		tooltip.add(TextFormatting.YELLOW + I18n.format("info.grimoire.element"));
		boolean has = ElementHelper.addElementInformation(inventory, worldIn, tooltip, flagIn);
		if (!has) tooltip.add(I18n.format("info.none"));

		if (ESAPI.isDevelop) {
			tooltip.add("Power: " + grimoire.getPotent() + " - " + grimoire.potentPoint);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void fixLauout(ItemStack stack) {
		GlStateManager.translate(-0.15, 0.45, 0);
		GlStateManager.rotate(-90, 1, 0, 0);
		GlStateManager.scale(0.75, 0.75, 0.75);
	}

}
