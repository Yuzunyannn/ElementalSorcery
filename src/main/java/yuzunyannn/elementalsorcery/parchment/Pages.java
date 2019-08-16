package yuzunyannn.elementalsorcery.parchment;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.api.ability.IElementInventory;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemKynaiteTools;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;

public class Pages {

	/** 记录所有的page */
	static final Map<String, Page> pages = new HashMap<>();
	/** 数组形式记录所有page */
	static final List<Page> pageList = new ArrayList<>();

	/** 获取page个数 */
	public static int getCount() {
		return pages.size();
	}

	/** 是否有效 */
	public static boolean isVaild(String id) {
		return pages.containsKey(id);
	}

	/** 添加页面 */
	public static void addPage(String id, Page page) {
		page.id = id;
		pages.put(id, page);
		pageList.add(page);
	}

	/** 获取错误页面 */
	public static Page getErrorPage() {
		return pages.get("error");
	}

	/** 获取page */
	public static Page getPage(String id) {
		if (pages.containsKey(id))
			return pages.get(id);
		return getErrorPage();
	}

	/** 通过index获取page */
	public static Page getPage(int index) {
		if (index < 0 || index >= pageList.size())
			return getErrorPage();
		return pageList.get(index);
	}

	/** 是否有效 */
	public static boolean isVaild(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null)
			return false;
		return isVaild(nbt.getString("pId"));
	}

	/** 获取page */
	public static Page getPage(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null)
			return getErrorPage();
		return getPage(nbt.getString("pId"));
	}

	/** 设置page */
	public static ItemStack setPage(String id, ItemStack stack) {
		if (id == null)
			id = "error";
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		nbt.setString("pId", id);
		return stack;
	}

	/** 检测item需转跳的对应的page */
	static public Page itemToPage(Item item) {
		if (item instanceof ItemKynaiteTools.toolsCapability)
			return getPage(Pages.ABOUT_KYNATIE_TOOLS);
		else {
			for (Entry<Item, String> entry : Pages.itemToId) {
				if (entry.getKey() == item)
					return getPage(entry.getValue());
			}
		}
		return null;
	}

	/** 获取书的页面 */
	static public PageBook getBookPage() {
		return (PageBook) getPage(BOOK);
	}

	/** item转跳查询表 */
	static public List<Entry<Item, String>> itemToId = new ArrayList<Entry<Item, String>>();
	/** 端 */
	static private Side side;
	/** 基本页面 */
	static public final String ERROR = "error";
	static public final String BOOK = "book";
	/** 其他页面 */
	static public final String ABOUT_ELEMENT = "element";
	static public final String ABOUT_STELA = "stela";
	static public final String ABOUT_HEARTH = "hearth";
	static public final String ABOUT_SMELT_BOX = "smeltBox";
	static public final String ABOUT_KYNAITE = "kynaite";
	static public final String ABOUT_MAGICAL_PIECE = "mgPiece";
	static public final String ABOUT_STAR_SAND = "starStone";
	static public final String ABOUT_STONE_MILL = "sMill";
	static public final String ABOUT_MAGIC_STONE = "mgStone";
	static public final String ABOUT_MELT_CAULDRON = "meltCal";
	static public final String ABOUT_ASTONE = "atone";

	static public final String ABOUT_INFUSION = "infusion";
	static public final String ABOUT_MAGICAL_ENDEREYE = "mgEeyes";
	static public final String ABOUT_KYNATIE_TOOLS = "kyTools";
	static public final String ABOUT_ABSORB_BOX = "absorbBox";
	static public final String ABOUT_MAGIC_PLATFORM = "mgPl";
	static public final String ABOUT_MAGIC_CRY = "mgCry";
	static public final String ABOUT_MAGIC_ESTONE = "estone";
	static public final String ABOUT_ELEMENT_CRY = "eleCry";
	static public final String ABOUT_ENCHANTINGBOOK = "encBook";
	static public final String ABOUT_SPELL_CRY = "spCry";
	static public final String ABOUT_EWORKBENCH = "eleWB";
	static public final String ABOUT_MANUAL = "manual";
	static public final String ABOUT_DEC_BOX = "decBox";
	static public final String ABOUT_MAGIC_PAPER = "mgPaper";
	static public final String ABOUT_SPELL_PAPER = "spPaper";
	static public final String ABOUT_BOOKCOVER = "bookCover";
	static public final String ABOUT_SPELLBOOK = "spBook";
	static public final String ABOUT_ELEMENT_CUBE = "eleCube";
	static public final String ABOUT_MAGICDESK = "mgDesk";
	static public final String ABOUT_SPLAUNCH = "spbLaunch";
	static public final String ABOUT_SPELEMENT = "spbEle";

	// 注册
	// enc=enchanting,cry=crystal,mg=magic,ele=element,sp=spell,spb=spellbook
	static public void init(Side side) {
		Pages.side = side;
		regPage(ERROR, new PageError());
		regPage(BOOK, new PageBook());
		regPage(ABOUT_ELEMENT, new PageSimple("element", ESInitInstance.ITEMS.SPELLBOOK));
		regPage(ABOUT_STELA, aboutStela());
		regPage(ABOUT_HEARTH, aboutHearth());
		regPage(ABOUT_SMELT_BOX, aboutSmeltBox());
		regPage(ABOUT_KYNAITE, new PageSmeltingSimple("kynaite", ESInitInstance.BLOCKS.KYNAITE_ORE));
		regPage(ABOUT_MAGICAL_PIECE, new PageSimple("mgicalPiece", ESInitInstance.ITEMS.MAGICAL_PIECE));
		regPage(ABOUT_STAR_SAND, new PageSimple("starSand", new ItemStack(ESInitInstance.BLOCKS.STAR_SAND),
				new ItemStack(ESInitInstance.BLOCKS.STAR_SAND)));
		regPage(ABOUT_STONE_MILL, new PageCraftingSimple("stoneMill", ESInitInstance.BLOCKS.STONE_MILL));
		regPage(ABOUT_MAGIC_STONE, new PageCraftingSimple("magicStone", ESInitInstance.ITEMS.MAGIC_STONE));
		regPage(ABOUT_MELT_CAULDRON, new PageCraftingSimple("meltCauldron", ESInitInstance.BLOCKS.MELT_CAULDRON));
		regPage(ABOUT_ASTONE, aboutAStone());

		regPage(ABOUT_INFUSION, new PageCraftingSimple("infusion", ESInitInstance.BLOCKS.INFUSION_BOX));
		regPage(ABOUT_MAGICAL_ENDEREYE,
				new PageCraftingSimple("magical_endereye", ESInitInstance.ITEMS.MAGICAL_ENDER_EYE));
		regPage(ABOUT_KYNATIE_TOOLS, aboutKynatieTools());
		regPage(ABOUT_ABSORB_BOX, new PageCraftingSimple("absorbBox", ESInitInstance.BLOCKS.ABSORB_BOX));
		regPage(ABOUT_MAGIC_PLATFORM,
				new PageCraftingSimple("magicPl", new ItemStack(ESInitInstance.BLOCKS.MAGIC_PLATFORM, 1, 0),
						new ItemStack(ESInitInstance.BLOCKS.MAGIC_PLATFORM, 1, 1)));
		regPage(ABOUT_MAGIC_CRY, new PageTransformSimple("magicCry", ESInitInstance.ITEMS.KYNAITE,
				ESInitInstance.ITEMS.MAGIC_CRYSTAL, PageTransform.INFUSION));
		regPage(ABOUT_MAGIC_ESTONE, aboutEStone());
		regPage(ABOUT_ELEMENT_CRY, new PageTransformSimple("elementCry", ESInitInstance.ITEMS.MAGIC_CRYSTAL,
				ESInitInstance.ITEMS.ELEMENT_CRYSTAL, PageTransform.INFUSION));
		regPage(ABOUT_ENCHANTINGBOOK, aboutEnchantingBook());
		regPage(ABOUT_SPELL_CRY, new PageTransformSimple("spellCry", ESInitInstance.ITEMS.MAGIC_CRYSTAL,
				ESInitInstance.ITEMS.SPELL_CRYSTAL, PageTransform.INFUSION));
		regPage(ABOUT_EWORKBENCH, new PageCraftingSimple("EWorkbench", ESInitInstance.BLOCKS.ELEMENT_WORKBENCH));
		regPage(ABOUT_MANUAL, new PageCraftingSimple("manual", ESInitInstance.ITEMS.MANUAL));
		regPage(ABOUT_DEC_BOX, new PageCraftingSimple("decBox", ESInitInstance.BLOCKS.DECONSTRUCT_BOX));
		regPage(ABOUT_MAGIC_PAPER, new PageCraftingSimple("magicPaper", ESInitInstance.ITEMS.MAGIC_PAPER));
		regPage(ABOUT_SPELL_PAPER, new PageCraftingSimple("spellPaper", ESInitInstance.ITEMS.SPELL_PAPER));
		regPage(ABOUT_BOOKCOVER,
				new PageCraftingSimple("bookCover", new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_COVER, 1, 0),
						new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_COVER, 1, 1)));
		regPage(ABOUT_SPELLBOOK, aboutSpellbook());
		regPage(ABOUT_ELEMENT_CUBE, new PageCraftingSimple("elementCube", ESInitInstance.BLOCKS.ELEMENTAL_CUBE));
		regPage(ABOUT_MAGICDESK, aboutMagicDesk());
		regPage(ABOUT_SPLAUNCH, aboutSP("spLaunch", ESInitInstance.ITEMS.SPELLBOOK,
				ESInitInstance.ITEMS.SPELLBOOK_LAUNCH, TileMagicDesk.AUTO_LAUNCH_BOOK));
		regPage(ABOUT_SPELEMENT, aboutSP("spElement", ESInitInstance.ITEMS.SPELLBOOK,
				ESInitInstance.ITEMS.SPELLBOOK_ELEMENT, TileMagicDesk.AUTO_ELEMENT_BOOK));
		initItemToId();
	}

	static private void initItemToId() {
		addItemId(ESInitInstance.BLOCKS.MELT_CAULDRON, ABOUT_MELT_CAULDRON);
		addItemId(ESInitInstance.ITEMS.MAGIC_STONE, ABOUT_MAGIC_STONE);
		addItemId(ESInitInstance.BLOCKS.STONE_MILL, ABOUT_STONE_MILL);
		addItemId(ESInitInstance.BLOCKS.KYNAITE_ORE, ABOUT_KYNAITE);
		addItemId(ESInitInstance.ITEMS.KYNAITE, ABOUT_KYNAITE);
		addItemId(ESInitInstance.ITEMS.MAGICAL_PIECE, ABOUT_MAGICAL_PIECE);
		addItemId(ESInitInstance.ITEMS.MAGICAL_ENDER_EYE, ABOUT_MAGICAL_ENDEREYE);
		addItemId(ESInitInstance.ITEMS.MAGIC_CRYSTAL, ABOUT_MAGIC_CRY);
		addItemId(ESInitInstance.ITEMS.SPELL_CRYSTAL, ABOUT_SPELL_CRY);
		addItemId(ESInitInstance.ITEMS.ELEMENT_CRYSTAL, ABOUT_ELEMENT_CRY);
		addItemId(ESInitInstance.ITEMS.SPELLBOOK_COVER, ABOUT_BOOKCOVER);
		addItemId(ESInitInstance.ITEMS.SPELLBOOK, ABOUT_SPELLBOOK);
		addItemId(ESInitInstance.ITEMS.SPELLBOOK_ENCHANTMENT, ABOUT_ENCHANTINGBOOK);
		addItemId(ESInitInstance.ITEMS.SPELL_PAPER, ABOUT_SPELL_PAPER);
	}

	static private void addItemId(Block block, String id) {
		itemToId.add(new AbstractMap.SimpleEntry(Item.getItemFromBlock(block), id));
	}

	static private void addItemId(Item item, String id) {
		itemToId.add(new AbstractMap.SimpleEntry(item, id));
	}

	static private String regPage(String id, Page page) {
		if (side.isClient())
			addPage(id, page);
		else
			addPage(id, new Page());
		return id;
	}

	// 特殊性质的多页
	static private class PageMultS extends PageMult {
		final int showAt;

		public PageMultS(int showAt, Page... pages) {
			super(pages);
			this.showAt = showAt;
		}

		public void addItemInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
			pages[showAt].addItemInformation(stack, worldIn, tooltip, flagIn);
		}

		@Override
		public void drawIcon(int xoff, int yoff, IPageManager pageManager) {
			pages[showAt].drawIcon(xoff, yoff, pageManager);
		}

		@Override
		public void drawString(int xoff, int yoff, IPageManager pageManager) {
			pages[showAt].drawString(xoff, yoff, pageManager);
		}
	}

	static private Page aboutStela() {
		return new PageMultS(2, new PageSimple("how2ply"),
				new PageCraftingSimple("parchment", ESInitInstance.ITEMS.PARCHMENT), new PageSimple("stela",
						new ItemStack(ESInitInstance.BLOCKS.STELA), new ItemStack(ESInitInstance.BLOCKS.STELA)));
	}

	static private Page aboutHearth() {
		return new PageCraftingSimple("hearth", new ItemStack(ESInitInstance.BLOCKS.HEARTH, 1, 0),
				new ItemStack(ESInitInstance.BLOCKS.HEARTH, 1, 1), new ItemStack(ESInitInstance.BLOCKS.HEARTH, 1, 2));
	}

	static private Page aboutSmeltBox() {
		return new PageCraftingSimple("smeltbox", new ItemStack(ESInitInstance.BLOCKS.SMELT_BOX),
				new ItemStack(ESInitInstance.BLOCKS.SMELT_BOX_IRON),
				new ItemStack(ESInitInstance.BLOCKS.SMELT_BOX_KYNAITE));
	}

	static private Page aboutAStone() {
		return new PageMultS(0,
				new PageSimpleInfo("astone", "fir", new ItemStack(ESInitInstance.BLOCKS.ASTONE),
						new ItemStack(ESInitInstance.BLOCKS.ASTONE)),
				new PageSimpleInfo("astone", "sec", new ItemStack(ESInitInstance.BLOCKS.ASTONE),
						new ItemStack(ESInitInstance.BLOCKS.MELT_CAULDRON)),
				new PageSimpleInfo("astone", "thi"));
	}

	static private Page aboutKynatieTools() {
		return new PageCraftingSimple("kynatieTools", new ItemStack(ESInitInstance.ITEMS.KYNAITE_AXE),
				new ItemStack(ESInitInstance.ITEMS.KYNAITE_HOE), new ItemStack(ESInitInstance.ITEMS.KYNAITE_PICKAXE),
				new ItemStack(ESInitInstance.ITEMS.KYNAITE_SPADE), new ItemStack(ESInitInstance.ITEMS.KYNAITE_SWORD));
	}

	static private Page aboutEStone() {
		return new PageCraftingSimple("estone", new ItemStack(ESInitInstance.BLOCKS.ESTONE, 1, 0),
				new ItemStack(ESInitInstance.BLOCKS.ESTONE, 1, 1), new ItemStack(ESInitInstance.BLOCKS.ESTONE, 1, 2),
				new ItemStack(ESInitInstance.BLOCKS.ESTONE_SLAB), new ItemStack(ESInitInstance.BLOCKS.ESTONE_STAIRS));
	}

	static private Page aboutEnchantingBook() {
		((ItemKynaiteTools.toolsCapability) ESInitInstance.ITEMS.KYNAITE_HOE).provideOnce();
		ItemStack extra = new ItemStack(ESInitInstance.ITEMS.KYNAITE_HOE);
		IElementInventory inv = extra.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		inv.insertElement(new ElementStack(ESInitInstance.ELEMENTS.ENDER, 10, 5), false);
		return new PageTransformSimple("enchantingBook", new ItemStack(Blocks.ENCHANTING_TABLE),
				new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_ENCHANTMENT), extra, null, PageTransform.SEPARATE);
	}

	static private Page aboutSpellbook() {
		return new PageMultS(1, new PageSimpleInfo("spellbook", "first"),
				new PageCraftingSimple("spellbook", ESInitInstance.ITEMS.SPELLBOOK),
				new PageSimpleInfo("spellbook", "more"));
	}

	static private Page aboutMagicDesk() {
		return new PageMultS(0, new PageCraftingSimple("magicDesk", ESInitInstance.BLOCKS.MAGIC_DESK),
				new PageBuildingSimple("magicDesk", Buildings.SPELLBOOK_ALTAR),
				new PageSimpleInfo("magicDesk", "crafting"), new PageSimpleInfo("magicDesk", "charge"));
	}

	static private Page aboutSP(String name, Item book, Item newBook, List<ItemStack> list) {
		return new PageMultS(0, new PageTransformSimple(name, book, newBook, list, PageTransform.SPELLALTAR),
				new PageSimpleInfo(name, "info"));
	}

}
