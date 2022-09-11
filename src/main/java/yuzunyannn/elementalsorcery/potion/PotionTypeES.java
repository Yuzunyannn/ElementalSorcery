package yuzunyannn.elementalsorcery.potion;

import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;

public class PotionTypeES extends PotionType {

	public static PotionTypeES create(String baseName, PotionEffect... effects) {
		return new PotionTypeES(baseName, effects);
	}

	public PotionTypeES(String baseName, PotionEffect... effects) {
		super(baseName, effects);
	}

	@Override
	public String getNamePrefixed(String prefix) {
		return "es." + super.getNamePrefixed(prefix);
	}
}
