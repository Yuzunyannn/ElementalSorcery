package yuzunyan.elementalsorcery.api;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import yuzunyan.elementalsorcery.api.element.Element;

public class ESObjects {

	public static Items ITEMS;;
	public static Blocks BLOCKS;
	public static Elements ELEMENTS;
	public static CreativeTabs CREATIVE_TABS;

	static public class Items {
		public Item KYNAITE;
		public Item MAGICAL_PIECE;
		public Item MAGICAL_ENDER_EYE;
		public Item KYNAITE_PICKAXE;
		public Item KYNAITE_AXE;
		public Item KYNAITE_SPADE;
		public Item KYNAITE_HOE;
		public Item KYNAITE_SWORD;
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
	}

	static public class Blocks {

		public Block ESTONE;
		public Block ESTONE_SLAB;
		public Block ESTONE_STAIRS;

		public Block ELEMENTAL_CUBE;
		public Block HEARTH;
		public Block SMELT_BOX;
		public Block SMELT_BOX_IRON;
		public Block SMELT_BOX_KYNAITE;
		public Block KYNAITE_ORE;
		public Block KYNAITE_BLOCK;
		public Block MAGIC_PLATFORM;
		public Block ABSORB_BOX;
		public Block INVALID_ENCHANTMENT_TABLE;
		public Block ELEMENT_WORKBENCH;
		public Block DECONSTRUCT_BOX;
		public Block INFUSION_BOX;
		public Block MAGIC_DESK;
		public Block ELEMENT_CRAFTING_TABLE;
		public Block DECONSTRUCT_ALTAR_TABLE;
		public Block STELA;
		public Block LANTERN;
	}

	static public class Elements {
		public Element VOID;
		public Element ENDER;
		public Element FIRE;
		public Element WATER;
		public Element AIR;
		public Element EARTH;
		public Element METAL;
		public Element WOOD;
		public Element KNOWLEDGE;
	}
}
