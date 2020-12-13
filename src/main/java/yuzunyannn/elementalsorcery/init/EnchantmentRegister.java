package yuzunyannn.elementalsorcery.init;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.enchant.EnchantmentGatherSouls;

public class EnchantmentRegister {

	private static void register(String id, Enchantment enchantment) {
		ForgeRegistries.ENCHANTMENTS.register(enchantment.setRegistryName(ElementalSorcery.MODID, id));
	}

	static public void registerAll() {
		register("gather_souls", new EnchantmentGatherSouls());
	}
}
