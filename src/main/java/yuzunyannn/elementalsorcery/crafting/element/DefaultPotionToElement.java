package yuzunyannn.elementalsorcery.crafting.element;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class DefaultPotionToElement implements IToElement {

	@Override
	public IToElementInfo toElement(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return null;
		boolean isPotion = nbt.hasKey("Potion", NBTTag.TAG_STRING)
				|| nbt.hasKey("CustomPotionEffects", NBTTag.TAG_LIST);
		if (!isPotion) return null;

		List<PotionEffect> potioneffects = PotionUtils.getEffectsFromStack(stack);
		if (potioneffects.isEmpty()) return null;

		List<ElementStack> estacks = new ArrayList<ElementStack>();
		ElementStack magic = ElementStack.magic(20, 100);
		estacks.add(magic);
		for (PotionEffect effect : potioneffects) {
			magic.grow((int) (MathHelper.sqrt(effect.getDuration() / 2)));
			magic.weaken(effect.getAmplifier() * 0.75f + 1);
			addExtraElementByEffect(estacks, effect);
		}

		ItemStack remain = stack.copy();
		nbt = remain.getTagCompound();
		nbt.removeTag("Potion");
		nbt.removeTag("CustomPotionEffects");

		return ToElementInfoStatic.create(1, remain, estacks.toArray(new ElementStack[estacks.size()]));
	}

	private void addExtraElementByEffect(List<ElementStack> estacks, PotionEffect effect) {
		ElementStack estack = ElementStack.EMPTY;

		Potion potion = effect.getPotion();
		if (potion == MobEffects.SPEED || potion == MobEffects.SLOWNESS || potion == MobEffects.JUMP_BOOST
				|| potion == MobEffects.WATER_BREATHING) {
			estack = new ElementStack(ESInit.ELEMENTS.AIR, 6, 25 * (1 + effect.getAmplifier()));
		} else if (potion == MobEffects.FIRE_RESISTANCE) {
			estack = new ElementStack(ESInit.ELEMENTS.WATER, 2, 10 * (1 + effect.getAmplifier()));
		} else if (potion == MobEffects.INSTANT_HEALTH || potion == MobEffects.REGENERATION) {
			estack = new ElementStack(ESInit.ELEMENTS.WOOD, 8, 30 * (1 + effect.getAmplifier()));
		} else if (potion == MobEffects.INSTANT_DAMAGE || potion == MobEffects.STRENGTH) {
			estack = new ElementStack(ESInit.ELEMENTS.FIRE, 7, 16 * (1 + effect.getAmplifier()));
		}

		if (estack.isEmpty()) return;
		for (ElementStack origin : estacks) {
			if (origin.areSameType(estack)) {
				origin.grow(estack);
				return;
			}
		}
		estacks.add(estack);
	}

}
