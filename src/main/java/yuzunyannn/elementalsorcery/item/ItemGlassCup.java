package yuzunyannn.elementalsorcery.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.item.IJuice;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.CapabilityProvider;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementCommon;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.MultiRets;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;
import yuzunyannn.elementalsorcery.world.Juice;
import yuzunyannn.elementalsorcery.world.Juice.JuiceMaterial;

public class ItemGlassCup extends Item {

	static public IJuice getJuice(ItemStack stack) {
		return new Juice(stack);
	}

	public ItemGlassCup() {
		this.setUnlocalizedName("glassCup");
		this.setMaxStackSize(1);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		IJuice juice = getJuice(stack);
		if (juice == null) return null;
		IElementInventory eInv = juice.getElementInventory(stack);
		if (eInv == null) return null;
		return new CapabilityProvider.ElementInventoryUseProvider(stack, eInv);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		IJuice juice = getJuice(stack);
		if (juice != null) {
			juice.addJuiceInformation(worldIn, tooltip, flagIn);
			IElementInventory eInv = ElementHelper.getElementInventory(stack);
			ElementHelper.addElementInformation(eInv, worldIn, tooltip, flagIn);

			if (ElementalSorcery.isDevelop) addDevelopInfo(worldIn, juice, eInv, tooltip);
		}
	}

	// 测试信息
	private void addDevelopInfo(World worldIn, IJuice juice, IElementInventory eInv, List<String> tooltip) {
		if (juice instanceof Juice && eInv != null) {
			Juice j = (Juice) juice;
			float water = j.getJuiceCount();
			MultiRets rets = j.drink(water, true);
			Map<JuiceMaterial, Float> drinkMap = rets.get(0, Map.class);
			for (int i = 0; i < eInv.getSlots(); i++) {
				ElementStack estack = eInv.getStackInSlot(i);
				if (estack.isEmpty()) continue;
				Element element = estack.getElement();
				if (element instanceof ElementCommon) {
					ElementCommon ec = (ElementCommon) element;
					List<PotionEffect> effects = new ArrayList<>();
					ec.addDrinkJuiceEffect(effects, worldIn, estack.copy(), water, drinkMap);
					for (PotionEffect effect : effects) tooltip.add(effect.toString());
				}
			}
		}
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		IJuice juice = getJuice(stack);
		if (juice == null) return stack;
		IElementInventory eInv = ElementHelper.getElementInventory(stack);
		juice.onDrink(worldIn, entityLiving, eInv);
		if (eInv != null) eInv.saveState(stack);
		if (entityLiving instanceof EntityPlayer) {
			((EntityPlayer) entityLiving).addStat(StatList.getObjectUseStats(this));
		}
		return stack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.DRINK;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		IJuice juice = getJuice(itemstack);
		if (juice == null) return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);

		float dis = (float) playerIn.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
		RayTraceResult ray = WorldHelper.getLookAtBlock(worldIn, playerIn, dis, true, false, false);
		if (ray != null) {
			if (juice.onContain(worldIn, playerIn, ray.getBlockPos()))
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
		}

		if (juice.canDrink(worldIn, playerIn)) {
			playerIn.setActiveHand(handIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
		}

		return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
	}

}
