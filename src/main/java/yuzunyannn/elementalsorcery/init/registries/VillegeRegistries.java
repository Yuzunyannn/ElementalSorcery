package yuzunyannn.elementalsorcery.init.registries;

import java.util.Random;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.worldgen.VillageESHall;

public class VillegeRegistries {

	public static void registerAll() {
		// 新的职业
		ForgeRegistries.VILLAGER_PROFESSIONS.register(ESInitInstance.VILLAGE.ES_VILLEGER);
		initPro(ESInitInstance.VILLAGE.ES_VILLEGER);
		// 新的村庄建筑
		MapGenStructureIO.registerStructureComponent(VillageESHall.class, "VESuvs");
		VillagerRegistry.instance().registerVillageCreationHandler(new VillageESHall.VillageCreationHandler());

	}

	private static final void initPro(VillagerRegistry.VillagerProfession pro) {
		// 卖纸商
		VillagerRegistry.VillagerCareer paperman = new VillagerRegistry.VillagerCareer(pro, "paperman");
		paperman.addTrade(1,
				new EntityVillager.ListItemForEmeralds(new ItemStack(ESInitInstance.ITEMS.PARCHMENT),
						new PriceInfo(8, 12)),
				new EntityVillager.ListItemForEmeralds(new ItemStack(ESInitInstance.ITEMS.SCROLL),
						new PriceInfo(24, 32)));
		// 元素知识提供者
		VillagerRegistry.VillagerCareer eslearner = new VillagerRegistry.VillagerCareer(pro, "eslearner");
		eslearner.addTrade(1, new ESLearnerTrade());
		// 元素魔法商
		VillagerRegistry.VillagerCareer magicallearner = new VillagerRegistry.VillagerCareer(pro, "magicallearner");
		magicallearner.addTrade(1,
				new EntityVillager.ListItemForEmeralds(new ItemStack(ESInitInstance.ITEMS.MAGIC_PIECE),
						new PriceInfo(16, 32)),
				new EntityVillager.ListItemForEmeralds(new ItemStack(ESInitInstance.ITEMS.MAGIC_PIECE, 4),
						new PriceInfo(64, 64)));
	}

	public static class ESLearnerTrade implements EntityVillager.ITradeList {

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
			final int n = 3;
			int div = Pages.getCount() / n;
			int at = 0;
			for (int i = 0; i < n - 1; i++) {
				int id = random.nextInt(div) + 1 + at;
				at += div;
				recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 5),
						ItemParchment.getParchment(Pages.getPage(id).getId())));
			}
			int id = random.nextInt(div + (Pages.getCount() % n)) + at;
			recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 5),
					ItemParchment.getParchment(Pages.getPage(id).getId())));
			if (random.nextFloat() < 0.5)
				recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 16),
						new ItemStack(ESInitInstance.BLOCKS.STELA)));
		}

	}

}
