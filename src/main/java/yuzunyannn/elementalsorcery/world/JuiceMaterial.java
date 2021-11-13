package yuzunyannn.elementalsorcery.world;

import java.util.Map;
import java.util.TreeMap;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.block.BlockElfFruit;
import yuzunyannn.elementalsorcery.init.ESInit;

public enum JuiceMaterial {

	WATER("water", 1, 0, ItemStack.EMPTY, false),
	ELF_FRUIT("EF", 120, 0x055f11, new ItemStack(ESInit.BLOCKS.ELF_FRUIT, 1, BlockElfFruit.MAX_STATE), true),
	APPLE("AP", 80, 0xffd368, Items.APPLE, true),
	MELON("ME", 200, 0xbd2417, Items.MELON, true),
	SUGAR("SU", 5, 0, Items.SUGAR, false),
	COCO("CO", 10, 0x704425, new ItemStack(Items.DYE, 1, 3), false);

	static final Map<String, JuiceMaterial> keysMap = new TreeMap<>();

	static {
		for (JuiceMaterial material : values()) {
			keysMap.put(material.key, material);
		}
	}

	final public String key;
	final public float occupancy;
	final public boolean isMain;
	final public int color;
	final public ItemStack item;

	JuiceMaterial(String key, float occupancy, int color, ItemStack item, boolean isMain) {
		this.key = key;
		this.occupancy = occupancy;
		this.isMain = isMain;
		this.item = item;
		this.color = color;
	}

	JuiceMaterial(String key, float occupancy, int color, Item item, boolean isMain) {
		this(key, occupancy, color, new ItemStack(item), isMain);
	}

	public static JuiceMaterial fromKey(String key) {
		return keysMap.get(key);
	}

}
