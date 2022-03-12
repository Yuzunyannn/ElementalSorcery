package yuzunyannn.elementalsorcery.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.element.IElementExplosion;
import yuzunyannn.elementalsorcery.api.element.IElemetJuice;
import yuzunyannn.elementalsorcery.api.element.IStarFlowerCast;
import yuzunyannn.elementalsorcery.util.element.DrinkJuiceEffectAdder;
import yuzunyannn.elementalsorcery.world.JuiceMaterial;

//简单的icon返回的元素
public abstract class ElementCommon extends Element implements IStarFlowerCast, IElementExplosion, IElemetJuice {

	ResourceLocation TEXTURE;

	public ElementCommon(int color, String resName) {
		super(color);
		TEXTURE = new ResourceLocation(ElementalSorcery.MODID, "textures/elements/" + resName + ".png");
		this.setTranslationKey(resName);
	}

	@Override
	public ResourceLocation getIconResourceLocation() {
		return TEXTURE;
	}

	protected int getStarFlowerRange(ElementStack estack) {
		return (int) Math.min(Math.ceil(16 * estack.getPower() / 1000f) + 4, 24);
	}

	@Override
	public void onDrinkJuice(World world, EntityLivingBase drinker, ElementStack estack, float water,
			Map<JuiceMaterial, Float> drinkMap) {
		if (world.isRemote) return;

		List<PotionEffect> effects = new ArrayList<>();
		addDrinkJuiceEffect(effects, world, estack, water, drinkMap);
		for (PotionEffect effect : effects) {
			drinker.addPotionEffect(effect);
		}
	}

	public void addDrinkJuiceEffect(List<PotionEffect> effects, @Nullable World world, ElementStack estack, float water,
			Map<JuiceMaterial, Float> drinkMap) {
		float fruit = Optional.ofNullable(drinkMap.get(JuiceMaterial.ELF_FRUIT)).orElse(Float.valueOf(0));
		float sugar = Optional.ofNullable(drinkMap.get(JuiceMaterial.SUGAR)).orElse(Float.valueOf(0));
		float coco = Optional.ofNullable(drinkMap.get(JuiceMaterial.COCO)).orElse(Float.valueOf(0));

		float timeUp = 1 + MathHelper.sqrt(sugar) / 4;
		float powerFactor = 0.75f + MathHelper.sqrt(coco) / 8;

		float timeBaseFactor = fruit * (estack.getCount() + 50) / 175f;

		DrinkJuiceEffectAdder helper = new DrinkJuiceEffectAdder(effects, world, estack, drinkMap,
				timeBaseFactor * timeUp, powerFactor).updatePowers();
		addDrinkJuiceEffect(helper);
	}

	protected void addDrinkJuiceEffect(DrinkJuiceEffectAdder helper) {
	}
}
