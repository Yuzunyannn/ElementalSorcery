package yuzunyan.elementalsorcery.init.registries;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import yuzunyan.elementalsorcery.init.ESInitInstance;

public class VillegeRegistries {

	public static void registerAll() {
		VillagerRegistry.VillagerProfession pro = new VillagerRegistry.VillagerProfession(
				"elementalsorcery:antique_dealer", "elementalsorcery:textures/entity/villager/es_studier.png",
				"elementalsorcery:textures/entity/zombie_villager/es_studier.png");
		ForgeRegistries.VILLAGER_PROFESSIONS.register(pro);
		VillagerRegistry.VillagerCareer paperman = new VillagerRegistry.VillagerCareer(pro, "paperman");
		paperman.addTrade(1, new EntityVillager.ListItemForEmeralds(new ItemStack(ESInitInstance.ITEMS.PARCHMENT),
				new PriceInfo(4, 8)));
		paperman.addTrade(1, new EntityVillager.ListItemForEmeralds(new ItemStack(ESInitInstance.ITEMS.SCROLL),
				new PriceInfo(12, 16)));
	}

}
