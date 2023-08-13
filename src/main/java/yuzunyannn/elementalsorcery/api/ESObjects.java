package yuzunyannn.elementalsorcery.api;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;

public class ESObjects {

	public static final Items ITEMS = new Items();
	public static final Blocks BLOCKS = new Blocks();
	public static final Elements ELEMENTS = new Elements();
	public static final Mantras MANTRAS = new Mantras();
	public static final Potions POTIONS = new Potions();
	public static final PotionTypes POTION_TYPES = new PotionTypes();
	public static final Village VILLAGE = new Village();

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
		public Item ELEMENT_CRACK;
		public Item ENTANGLE_NODE;
		public Item DRAGON_BREATH_PICKAXE;
		public Item INVERT_GEM;
		public Item ICE_ROCK_CHIP;
		public Item ICE_ROCK_SPAR;
		public Item FAIRY_CORE;
		public Item MATERIAL_DEBRIS;
		public Item MANTRA_GEM;
		public Item CONTROLLER;
		public Item MAGIC_TERMINAL;
		public Item VOID_FRAGMENT;
		public Item VOID_CONTAINER;
		public Item VOID_CONTAINER_ELEMENT;
		public Item ELF_DIAMOND;
		public Item COLLAPSE;
		public Item COLLAPSE_WAND;
		public Item RELIC_GUARD_CORE;
		public Item SHOCK_WAND;
		public Item RELIC_DISC;
		public Item JUICE_CONCENTRATE;
		public Item DUNGEON_KEY;
		public Item MEMORY_FRAGMENT;
		public Item MEMORY_FEATHER;
		public Item DUNGEON_SEED;
		public Item DUNGEON_STONE;
		public Item SIMPLE_MATERIAL_CONTAINER;
		public Item STRENGTHEN_AGENT;
		public Item FLOAT_CARPET;
		public Item METEORITE_INGOT;
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
		public Block DEVOLVE_CUBE;
		public Block DISINTEGRATE_STELA;
		public Block ICE_ROCK_STAND;
		public Block ICE_ROCK_CRYSTAL_BLOCK;
		public Block ICE_ROCK_NODE;
		public Block ELEMENT_REACTOR;
		public Block ESTONE_MATRIX;
		public Block INSTANT_CONSTITUTE;
		public Block ESTONE_CROCK;
		public Block IS_CRAFT_CC;
		public Block DUNGEON_DOOR;
		public Block DUNGEON_DOOR_EXPAND;
		public Block DUNGEON_BRICK;
		public Block DUNGEON_STAIRS;
		public Block DUNGEON_FUNCTION;
		public Block DUNGEON_HAYSTACK;
		public Block DUNGEON_BARRIER;
		public Block DUNGEON_ROTTEN_LIFELOG;
		public Block DUNGEON_ACTINIC_GLASS;
		public Block DUNGEON_LIGHT;
		public Block DUNGEON_MAGIC_CIRCLE_A;
		public Block DUNGEON_CHECKPOINT;
		public Block STRANGE_EGG;
		public Block METEORITE;
		public Block METEORITE_DRUSE;
	}

	static public class Elements {
		public Element VOID;
		public Element MAGIC;

		public Element WATER;
		public Element FIRE;
		public Element EARTH;
		public Element AIR;
		public Element WOOD;
		public Element METAL;
		public Element ENDER;
		public Element KNOWLEDGE;
		public Element STAR;
	}

	static public class Mantras {

		public Mantra LAUNCH_ECR;
		public Mantra LAUNCH_EDE;
		public Mantra LAUNCH_ECO;
		public Mantra LAUNCH_BRC;

		public Mantra ECRACK_OPEN;

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
		public Mantra ELEMENT_WHIRL;
		public Mantra LASER;
		public Mantra STURDY_AREA;
		public Mantra FROZEN;
		public Mantra ICE_CRYSTAL_BOMB;
		public Mantra NATURAL_MEDAL;
		public Mantra PUPPET_AREA;
		public Mantra GOLD_SHIELD;
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
		public Potion ELEMENT_CRACK_ATTACK;
		public Potion ENTHUSIASTIC_STUDY;
		public Potion SILENT;
		public Potion FROZEN;
		public Potion NATURAL_MEDAL;
		public Potion GOLD_SHIELD;
		public Potion DEATH_WATCH;
		public Potion METEORITE_DISEASE;
	}

	static public class PotionTypes {
		public PotionType SILENT;
	}

	static public class Village {
		public VillagerRegistry.VillagerProfession ES_VILLEGER;
	}

}
