package yuzunyannn.elementalsorcery.api;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import yuzunyannn.elementalsorcery.api.element.Element;

public class ESObjects {

	public static Items ITEMS;;
	public static Blocks BLOCKS;
	public static Elements ELEMENTS;
	public static Village VILLAGE;
	public static CreativeTabs CREATIVE_TABS;

	static public class Items {
		public Item KYANITE;
		public Item MAGICAL_PIECE;
		public Item MAGICAL_ENDER_EYE;
		public Item KYANITE_PICKAXE;
		public Item KYANITE_AXE;
		public Item KYANITE_SPADE;
		public Item KYANITE_HOE;
		public Item KYANITE_SWORD;
		public Item TINY_KNIFE;
		public Item SPELLBOOK;
		public Item SPELLBOOK_ARCHITECTURE;
		public Item SPELLBOOK_ENCHANTMENT;
		public Item SPELLBOOK_LAUNCH;
		public Item SPELLBOOK_ELEMENT;
		public Item ARCHITECTURE_CRYSTAL;
		public Item ELEMENT_CRYSTAL;
		public Item MAGIC_CRYSTAL;
		public Item PARCHMENT;
		public Item MAGIC_PAPER;
		public Item SPELL_PAPER;
		public Item SPELL_CRYSTAL;
		public Item SPELLBOOK_COVER;
		public Item SCROLL;
		public Item MANUAL;
		public Item MAGIC_RULER;
		public Item ITEM_CRYSTAL;
		public Item MAGIC_STONE;
	}

	static public class Blocks {

		public Block ESTONE;
		public Block ESTONE_SLAB;
		public Block ESTONE_STAIRS;
		public Block ASTONE;
		public Block STAR_STONE;
		public Block STAR_SAND;

		public Block ELEMENTAL_CUBE;
		public Block HEARTH;
		public Block SMELT_BOX;
		public Block SMELT_BOX_IRON;
		public Block SMELT_BOX_KYANITE;
		public Block KYANITE_ORE;
		public Block KYANITE_BLOCK;
		public Block MAGIC_PLATFORM;
		public Block ABSORB_BOX;
		public Block INVALID_ENCHANTMENT_TABLE;
		public Block ELEMENT_WORKBENCH;
		public Block DECONSTRUCT_BOX;
		public Block MAGIC_DESK;
		public Block ELEMENT_CRAFTING_TABLE;
		public Block DECONSTRUCT_ALTAR_TABLE;
		public Block STELA;
		public Block LANTERN;
		public Block BUILDING_ALTAR;
		public Block ANALYSIS_ALTAR;
		public Block SUPREME_CRAFTING_TABLE;
		public Block MAGIC_TORCH;
		public Block STONE_MILL;
		public Block MELT_CAULDRON;
		public Block MD_MAGIC_GEN;
		public Block MD_HEARTH;
		public Block MD_RUBBLE_REPAIR;
		public Block MD_INFUSION;
		public Block MD_TRANSFER;
	}

	static public class Elements {
		public Element VOID;
		public Element MAGIC;

		public Element ENDER;
		public Element FIRE;
		public Element WATER;
		public Element AIR;
		public Element EARTH;
		public Element METAL;
		public Element WOOD;
		public Element KNOWLEDGE;
	}

	static public class Village {
		public VillagerRegistry.VillagerProfession ES_VILLEGER;
	}
}
