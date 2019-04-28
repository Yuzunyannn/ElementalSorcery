package yuzunyan.elementalsorcery.init.registries;

import net.minecraftforge.oredict.OreDictionary;
import yuzunyan.elementalsorcery.init.ESInitInstance;

public class OreDictionaryRegistries {
	public static final void registerAll() {
		OreDictionary.registerOre("kynaite", ESInitInstance.ITEMS.KYNAITE);
		OreDictionary.registerOre("blockKynaite", ESInitInstance.BLOCKS.KYNAITE_BLOCK);
	}
}
