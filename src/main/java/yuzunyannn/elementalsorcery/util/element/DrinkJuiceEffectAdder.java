package yuzunyannn.elementalsorcery.util.element;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.world.JuiceMaterial;

public class DrinkJuiceEffectAdder {

	public final World world;
	public final List<PotionEffect> effects;
	public final Map<JuiceMaterial, Float> drinkMap;
	public final float timeFactor;
	public final float powerFactor;
	public final ElementStack estack;

	protected Map<JuiceMaterial, Float> powerMap = new TreeMap();

	protected JuiceMaterial markMaterial;
	protected float markStart;

	protected Potion pPotion;
	protected int pTickFragment;
	protected float pLevelInterval;

	public DrinkJuiceEffectAdder(List<PotionEffect> effects, World world, ElementStack estack,
			Map<JuiceMaterial, Float> drinkMap, float timeFactor, float powerFactor) {
		this.world = world;
		this.effects = effects;
		this.drinkMap = drinkMap;
		this.timeFactor = timeFactor;
		this.powerFactor = powerFactor;
		this.estack = estack;
	}

	private float toFloat(Float f) {
		return f == null ? 0 : f.floatValue();
	}

	public DrinkJuiceEffectAdder mark(JuiceMaterial mark, float start) {
		this.markMaterial = mark;
		this.markStart = start;
		return this;
	}

	public DrinkJuiceEffectAdder endMark() {
		this.markMaterial = null;
		return this;
	}

	public DrinkJuiceEffectAdder updatePowers() {
		float apple = toFloat(drinkMap.get(JuiceMaterial.APPLE));
		float melon = toFloat(drinkMap.get(JuiceMaterial.MELON));
		float fruit = toFloat(drinkMap.get(JuiceMaterial.ELF_FRUIT));
		powerMap.put(JuiceMaterial.APPLE, apple * estack.getPower() * powerFactor);
		powerMap.put(JuiceMaterial.MELON, melon * estack.getPower() * powerFactor);
		powerMap.put(JuiceMaterial.ELF_FRUIT, fruit * estack.getPower() * powerFactor);
		return this;
	}

	public float getPower(JuiceMaterial material) {
		return toFloat(powerMap.get(material));
	}

	/** 准备药水，写入上下文 */
	public DrinkJuiceEffectAdder preparatory(Potion potion, int secFragment, float levelInterval) {
		pPotion = potion;
		pTickFragment = secFragment * 20;
		pLevelInterval = levelInterval;
		return this;
	}

	/** 检查主能量，并标记检测标签 */
	public DrinkJuiceEffectAdder check(JuiceMaterial material, float powerMin) {
		float power = getPower(material);
		if (power < powerMin) return endMark();
		return mark(material, powerMin);
	}

	/** 检测附能量比例，并标记检测标签 */
	public DrinkJuiceEffectAdder checkRatio(JuiceMaterial material, float min, float max) {
		if (markMaterial == null) return this;
		float power = getPower(markMaterial);
		float powerSub = getPower(material);
		float r = powerSub / power;
		if (r < min || r >= max) return endMark();
		return this;
	}

	/** 如果存在检测标签，对元素进行下降 */
	public DrinkJuiceEffectAdder descend(JuiceMaterial material, float n, float f) {
		if (markMaterial == null) return this;
		float power = getPower(material);
		powerMap.put(markMaterial, Math.max(0, (power - n) * f));
		return this;
	}

	/** 如果存在检测标签，将准备好的效果加入list */
	public DrinkJuiceEffectAdder join() {
		if (markMaterial == null) return this;
		float whatPower = getPower(markMaterial);
		int power = pLevelInterval == 0 ? 0 : (int) Math.min(3, (whatPower - markStart) / pLevelInterval);
		int time = MathHelper.floor(pTickFragment * timeFactor);
		effects.add(new PotionEffect(pPotion, time, power));
		return this;
	}
}
