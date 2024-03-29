package yuzunyannn.elementalsorcery.init;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class OreDictionaryRegistries {

	public static final void registerAll() {
		ESObjects.Items ITEMS = ESObjects.ITEMS;
		ESObjects.Blocks BLOCKS = ESObjects.BLOCKS;
		OreDictionary.registerOre("kyanite", ITEMS.KYANITE);
		OreDictionary.registerOre("oreKyanite", BLOCKS.KYANITE_ORE);
		OreDictionary.registerOre("blockKyanite", BLOCKS.KYANITE_BLOCK);
		OreDictionary.registerOre("plankWood", new ItemStack(BLOCKS.ELF_PLANK, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(BLOCKS.ELF_PLANK, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("oreScarletCrystal", BLOCKS.SCARLET_CRYSTAL_ORE);
		OreDictionary.registerOre("scarletCrystal", ESObjects.ITEMS.SCARLET_CRYSTAL);
		OreDictionary.registerOre("chipIceRock", ESObjects.ITEMS.ICE_ROCK_CHIP);
		OreDictionary.registerOre("sand", BLOCKS.STAR_SAND);
	}
}
