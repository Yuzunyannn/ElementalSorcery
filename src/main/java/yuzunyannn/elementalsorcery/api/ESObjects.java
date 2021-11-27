package yuzunyannn.elementalsorcery.api;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;
import yuzunyannn.elementalsorcery.item.prop.ItemArrogantWool;

public class ESObjects {

	public static Items ITEMS;
	public static Blocks BLOCKS;
	public static Elements ELEMENTS;
	public static Mantras MANTRAS;
	public static Potions POTIONS;
	public static Village VILLAGE;
	public static CreativeTabs CREATIVE_TABS;

	static public class Items {
		public Item KYANITE;
		public Item MAGIC_PIECE;
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
		public Item AZURE_CRYSTAL;
		public Item RESONANT_CRYSTAL;
		public Item PARCHMENT;
		public Item MAGIC_PAPER;
		public Item SPELL_CRYSTAL;
		public Item SPELLBOOK_COVER;
		public Item SCROLL;
		public Item MANUAL;
		public Item MAGIC_RULER;
		public Item ITEM_CRYSTAL;
		public Item MAGIC_STONE;
		public Item ORDER_CRYSTAL;
		public Item MD_BASE;
		public Item RITE_MANUAL;
		public Item RED_HANDSET;
		public Item ELF_CRYSTAL;
		public Item SUPREME_TABLE_COMPONENT;
		public Item ELF_COIN;
		public Item ELF_PURSE;
		public Item NATURE_CRYSTAL;
		public Item NATURE_DUST;
		public Item GRIMOIRE;
		public Item ANCIENT_PAPER;
		public Item QUEST;
		public Item ELF_WATCH;
		public Item MAGIC_GOLD;
		public Item MAGIC_GOLD_PICKAXE;
		public Item MAGIC_GOLD_AXE;
		public Item MAGIC_GOLD_SPADE;
		public Item MAGIC_GOLD_HOE;
		public Item MAGIC_GOLD_SWORD;
		public Item PARCEL;
		public Item ADDRESS_PLATE;
		public Item ELF_STAR;
		public Item JUMP_GEM;
		public Item UNSCRAMBLE_NOTE;
		public Item SOUL_FRAGMENT;
		public Item SOUL_WOOD_SWORD;
		public Item RELIC_GEM;
		public Item ROCK_CAMERA;
		public Item KEEPSAKE;
		public Item QUILL;
		public Item FUSION_CRYSTAL;
		public Item VORTEX;
		public Item ELEMENT_STONE;
		public Item LIFE_LEATHER;
		public Item MAGIC_BLAST_WAND;
		public Item SOUL_KILLER_SWORD;
		public Item SCAPEGOAT;
		public Item MAGIC_CORE;
		public Item SCARLET_CRYSTAL;
		public Item STAR_BELL;
		public Item APPLE_CANDY;
		public Item FAIRY_CUBE;
		public Item FAIRY_CUBE_MODULE;
		public Item RABID_LEATHER;
		public Item CUBE_CORE;
		public Item DREAD_GEM;
		public Item DEJECTED_TEAR;
		public Item MERCHANT_INVITATION;
		public Item ELEMENT_BOARD;
		public Item CUBE_DEMARCATOR;
		public Item WINDMILL_BLADE_FRAME;
		public Item WINDMILL_BLADE;
		public Item WINDMILL_BLADE_ASTONE;
		public Item WINDMILL_BLADE_WOOD;
		public Item WINDMILL_BLADE_CRYSTAL;
		public Item ELF_FRUIT_BOMB;
		public Item GLASS_CUP;
		public Item CALAMITY_GEM;
		public Item BLESSING_JADE;
		public Item ARROGANT_WOOL;
		public Item BLESSING_JADE_PIECE;
	}

	static public class Blocks {

		public Block ESTONE;
		public Block ESTONE_SLAB;
		public Block ESTONE_STAIRS;
		public Block ESTONE_PRISM;
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
		public Block INVALID_ENCHANTMENT_TABLE;
		public Block ELEMENT_WORKBENCH;
		public Block MAGIC_DESK;
		public Block ELEMENT_CRAFTING_TABLE;
		public Block DECONSTRUCT_ALTAR_TABLE;
		public Block DECONSTRUCT_ALTAR_TABLE_ADV;
		public Block RITE_TABLE;
		public Block LANTERN;
		public Block BUILDING_ALTAR;
		public Block ANALYSIS_ALTAR;
		public Block SUPREME_TABLE;
		public Block MAGIC_TORCH;
		public Block STONE_MILL;
		public Block MELT_CAULDRON;
		public Block ELF_LOG;
		public Block ELF_LEAF;
		public Block ELF_PLANK;
		public Block ELF_SAPLING;
		public Block ELF_FRUIT;
		public Block MD_MAGIC_GEN;
		public Block MD_HEARTH;
		public Block MD_RUBBLE_REPAIR;
		public Block MD_INFUSION;
		public Block MD_TRANSFER;
		public Block MD_MAGIC_SOLIDIFY;
		public Block MD_ABSORB_BOX;
		public Block MD_MAGICLIZATION;
		public Block MD_DECONSTRUCT_BOX;
		public Block MD_RESONANT_INCUBATOR;
		public Block MD_FREQUENCY_MAPPING;
		public Block MD_LIQUIDIZER;
		public Block LIFE_FLOWER;
		public Block MAGIC_POT;
		public Block LIFE_DIRT;
		public Block CRYSTAL_FLOWER;
		public Block IS_CRAFT_NORMAL;
		public Block PORTAL_ALTAR;
		public Block TRANSCRIBE_TABLE;
		public Block TRANSCRIBE_INJECTION;
		public Block ELF_TREE_CORE;
		public Block ELF_BEACON;
		public Block RESEARCHER;
		public Block SEAL_STONE;
		public Block SCARLET_CRYSTAL_ORE;
		public Block STAR_FLOWER;
		public Block CRUDE_QUARTZ;
		public Block ELEMENT_PLATFORM;
		public Block FLUORSPAR;
		public Block DECONSTRUCT_WINDMILL;
		public Block ELEMENT_TRANSLOCATOR;
		public Block GOAT_GOLD_BRICK;
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
		public Element STAR;
	}

	static public class Mantras {

		public Mantra LAUNCH_ECR;
		public Mantra LAUNCH_EDE;
		public Mantra LAUNCH_ECO;
		public Mantra LAUNCH_BRC;

		public Mantra ENDER_TELEPORT;
		public Mantra FLOAT;
		public Mantra SPRINT;
		public Mantra FIRE_BALL;
		public Mantra LUSH;
		public Mantra BLOCK_CRASH;
		public Mantra MINING_AREA;
		public Mantra LIGHTNING_AREA;
		public Mantra SUMMON;
		public Mantra SLOW_FALL;
		public Mantra FOOTBRIDGE;
		public Mantra FIRE_AREA;
		public Mantra MAGIC_STRAFE;
		public Mantra FLOAT_AREA;
		public Mantra FIRE_CHARGE;
		public Mantra ARROW;
		public Mantra POTENT;
		public Mantra FLUORSPAR;
		public Mantra TIME_HOURGLASS;
	}

	static public class Potions {
		public Potion TIME_SLOW;
		public Potion FIRE_WALKER;
		public Potion TIDE_WALKER;
		public Potion WIND_WALKER;
		public Potion FLUORESCE_WALKER;
		public Potion POUND_WALKER;
		public Potion VERDANT_WALKER;
		public Potion REBIRTH_FROM_FIRE;
		public Potion WATER_CALAMITY;
		public Potion ENDERIZATION;
		public Potion ENDERCORPS;
		public Potion WIND_SHIELD;
		public Potion GOLDEN_EYE;
		public Potion POWER_PITCHER;
		public Potion HEALTH_BALANCE;
		public Potion COMBAT_SKILL;
		public Potion DEFENSE_SKILL;
		public Potion STAR;
		public Potion CALAMITY;
		public Potion BLESSING;
	}

	static public class Village {
		public VillagerRegistry.VillagerProfession ES_VILLEGER;
	}
}
