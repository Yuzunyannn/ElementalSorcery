package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
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
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class ItemGrimoire extends Item {

	public ItemGrimoire() {
		this.setUnlocalizedName("grimoire");
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
					String name = I18n.format(m.getUnlocalizedName() + ".name");
					tooltip.add(TextFormatting.AQUA + I18n.format("info.grimoire.current", name));
					String describe = m.getUnlocalizedName() + ".describe";
					if (I18n.hasKey(describe)) tooltip.add(I18n.format(describe));
				}
			} else tooltip.add(TextFormatting.AQUA + I18n.format("info.grimoire.error"));
		}
		// 元素信息
		IElementInventory inventory = grimoire.getInventory();
		if (inventory == null) return;
		tooltip.add(TextFormatting.YELLOW + I18n.format("info.grimoire.element"));
		boolean has = ElementHelper.addElementInformation(inventory, worldIn, tooltip, flagIn);
		if (!has) tooltip.add(I18n.format("info.none"));
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
		grimoire.loadState(stack);
		// 获取咒文
		NBTTagCompound originData = Grimoire.getOriginNBT(stack);
		Mantra mantra = Mantra.getFromNBT(originData);
		if (mantra == null) return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		if (!mantra.canStart(playerIn)) return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		// 开始
		EntityGrimoire.start(worldIn, playerIn, mantra, originData);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

}
