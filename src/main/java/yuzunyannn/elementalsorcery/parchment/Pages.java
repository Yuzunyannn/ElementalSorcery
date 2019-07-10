package yuzunyannn.elementalsorcery.parchment;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.parchment.Pages1.AboutEStone;
import yuzunyannn.elementalsorcery.parchment.Pages1.AboutEnchantingBook;
import yuzunyannn.elementalsorcery.parchment.Pages1.AboutHearth;
import yuzunyannn.elementalsorcery.parchment.Pages1.AboutKynatieTools;
import yuzunyannn.elementalsorcery.parchment.Pages1.AboutMagicDesk;
import yuzunyannn.elementalsorcery.parchment.Pages1.AboutSPElement;
import yuzunyannn.elementalsorcery.parchment.Pages1.AboutSPLaunch;
import yuzunyannn.elementalsorcery.parchment.Pages1.AboutSmeltBox;
import yuzunyannn.elementalsorcery.parchment.Pages1.AboutSpellbook;
import yuzunyannn.elementalsorcery.parchment.Pages1.AboutStela;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class Pages {
	static class PageSimpleInfo extends Page {

		private final String name;
		private final String info;

		public PageSimpleInfo(String name, String info) {
			this.name = name;
			this.info = info;
		}

		@Override
		public String getTitle() {
			return "page." + name;
		}

		@Override
		public String getContext() {
			return "page." + name + ".ct." + info;
		}

		@Override
		public void addContexts(List<String> contexts) {
			TextHelper.addInfoCheckLine(contexts, this.getContext());
		}
	}

	static public List<Entry<Item, Integer>> Item_Id = new ArrayList<Entry<Item, Integer>>();
	// 最大页数
	static public int REAL_PAGE_COUNT = 0;
	// 页面
	/** 错误页面，客户端和服务端注册 */
	static public int ERROR;
	/** 书页面 */
	static public int BOOK;
	static public int ABOUT_ELEMENT;
	static public int ABOUT_STELA;
	static public int ABOUT_HEARTH;
	static public int ABOUT_SMELT_BOX;
	static public int ABOUT_KYNAITE;
	static public int ABOUT_MAGICAL_PIECE;
	static public int ABOUT_INFUSION;
	static public int ABOUT_MAGICAL_ENDEREYE;
	static public int ABOUT_KYNATIE_TOOLS;
	static public int ABOUT_ABSORB_BOX;
	static public int ABOUT_MAGIC_PL;
	static public int ABOUT_MAGIC_CRY;
	static public int ABOUT_MAGIC_ESTONE;
	static public int ABOUT_ELEMENT_CRY;
	static public int ABOUT_ENCHANTINGBOOK;
	static public int ABOUT_SPELL_CRY;
	static public int ABOUT_EWORKBENCH;
	static public int ABOUT_MANUAL;
	static public int ABOUT_DEC_BOX;
	static public int ABOUT_MAGIC_PAPER;
	static public int ABOUT_SPELL_PAPER;
	static public int ABOUT_BOOKCOVER;
	static public int ABOUT_SPELLBOOK;
	static public int ABOUT_ELEMENT_CUBE;
	static public int ABOUT_MAGICDESK;
	static public int ABOUT_SPLAUNCH;
	static public int ABOUT_SPELEMENT;

	static public void initPre() {
	}

	static private Side side;

	static public void initError() {
		Page page = new PageError();
		pages.add(page);
		ERROR = page.id = 0;
		REAL_PAGE_COUNT++;

		page = new PageBook();
		pages.add(page);
		BOOK = page.id = 1;
		REAL_PAGE_COUNT++;
	}

	static public void init(Side side) {
		Pages.side = side;
		ABOUT_ELEMENT = addPage(new PageSimple("element", ESInitInstance.ITEMS.SPELLBOOK));
		ABOUT_STELA = addPage(new AboutStela());
		ABOUT_HEARTH = addPage(new AboutHearth());
		ABOUT_SMELT_BOX = addPage(new AboutSmeltBox());
		ABOUT_KYNAITE = addPage(new PageSmeltingSimple("kynaite", ESInitInstance.BLOCKS.KYNAITE_ORE));
		ABOUT_MAGICAL_PIECE = addPage(new PageSimple("mgical_piece", ESInitInstance.ITEMS.MAGICAL_PIECE));
		ABOUT_INFUSION = addPage(new PageCraftingSimple("infusion", ESInitInstance.BLOCKS.INFUSION_BOX));
		ABOUT_MAGICAL_ENDEREYE = addPage(
				new PageCraftingSimple("magical_endereye", ESInitInstance.ITEMS.MAGICAL_ENDER_EYE));
		ABOUT_KYNATIE_TOOLS = addPage(new AboutKynatieTools());
		ABOUT_ABSORB_BOX = addPage(new PageCraftingSimple("absorbBox", ESInitInstance.BLOCKS.ABSORB_BOX));
		ABOUT_MAGIC_PL = addPage(
				new PageCraftingSimple("magicPl", new ItemStack(ESInitInstance.BLOCKS.MAGIC_PLATFORM, 1, 0),
						new ItemStack(ESInitInstance.BLOCKS.MAGIC_PLATFORM, 1, 1)));
		ABOUT_MAGIC_CRY = addPage(new PageTransformSimple("magicCry", ESInitInstance.ITEMS.KYNAITE,
				ESInitInstance.ITEMS.MAGIC_CRYSTAL, PageTransform.INFUSION));
		ABOUT_MAGIC_ESTONE = addPage(new AboutEStone());
		ABOUT_ELEMENT_CRY = addPage(new PageTransformSimple("elementCry", ESInitInstance.ITEMS.MAGIC_CRYSTAL,
				ESInitInstance.ITEMS.ELEMENT_CRYSTAL, PageTransform.INFUSION));
		ABOUT_ENCHANTINGBOOK = addPage(new AboutEnchantingBook());
		ABOUT_SPELL_CRY = addPage(new PageTransformSimple("spellCry", ESInitInstance.ITEMS.MAGIC_CRYSTAL,
				ESInitInstance.ITEMS.SPELL_CRYSTAL, PageTransform.INFUSION));
		ABOUT_EWORKBENCH = addPage(new PageCraftingSimple("EWorkbench", ESInitInstance.BLOCKS.ELEMENT_WORKBENCH));
		ABOUT_MANUAL = addPage(new PageCraftingSimple("manual", ESInitInstance.ITEMS.MANUAL));
		ABOUT_DEC_BOX = addPage(new PageCraftingSimple("decBox", ESInitInstance.BLOCKS.DECONSTRUCT_BOX));
		ABOUT_MAGIC_PAPER = addPage(new PageCraftingSimple("magicPaper", ESInitInstance.ITEMS.MAGIC_PAPER));
		ABOUT_SPELL_PAPER = addPage(new PageCraftingSimple("spellPaper", ESInitInstance.ITEMS.SPELL_PAPER));
		ABOUT_BOOKCOVER = addPage(
				new PageCraftingSimple("bookCover", new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_COVER, 1, 0),
						new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_COVER, 1, 1)));
		ABOUT_SPELLBOOK = addPage(new AboutSpellbook());
		ABOUT_ELEMENT_CUBE = addPage(new PageCraftingSimple("elementCube", ESInitInstance.BLOCKS.ELEMENTAL_CUBE));
		ABOUT_MAGICDESK = addPage(new AboutMagicDesk());
		ABOUT_SPLAUNCH = addPage(new AboutSPLaunch());
		ABOUT_SPELEMENT = addPage(new AboutSPElement());
		// 初始化item到id图
		Pages.initItemToId();
	}

	static private void initItemToId() {
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

	static private void addItemId(Item item, int id) {
		Item_Id.add(new AbstractMap.SimpleEntry(item, id));
	}

	static private void addItemId(Block block, int id) {
		Item_Id.add(new AbstractMap.SimpleEntry(Item.getItemFromBlock(block), id));
	}

	/** 添加一个页面 */
	static public int addPage(Page page) {
		page.id = 0;
		if (side.isClient()) {
			pages.add(page);
			page.id = pages.size() - 1;
		} else
			page.id = REAL_PAGE_COUNT;
		REAL_PAGE_COUNT++;
		return page.id;
	}

	// 获取页数量，该函数客户端和服务端返回不一样
	static public int getCount() {
		return Pages.pages.size();
	}

	// 所有注册的页面
	static protected ArrayList<Page> pages = new ArrayList<Page>();

	// 获取真实最大页数
	static public int getMax() {
		return REAL_PAGE_COUNT;
	}

	// 获取页面，客户端使用，服务端永远是0页
	static public Page getPage(int index) {
		if (index < 0 || index >= pages.size()) {
			ElementalSorcery.logger.warn("getPage异常的页数进入:" + index);
			return Pages.getErrorPage();
		}
		return pages.get(index);
	}

	// 获取页面，客户端使用，服务端永远是0页
	static public Page getPage(ItemStack stack) {
		NBTTagCompound nbt = stack.getSubCompound("page");
		if (nbt == null)
			return Pages.getErrorPage();
		return getPage(nbt.getInteger("id"));
	}

	// 获取页面ID
	static public int getPageId(ItemStack stack) {
		NBTTagCompound nbt = stack.getSubCompound("page");
		if (nbt == null)
			return 0;
		return nbt.getInteger("id");
	}

	// 获取错误页面
	static public Page getErrorPage() {
		return pages.get(ERROR);
	}

	// 获取书页面
	static public PageBook getBookPage() {
		return (PageBook) pages.get(BOOK);
	}

	// 是有有效id
	static public boolean isVaild(int id) {
		return id >= 0 && id < getMax();
	}

	// 设置
	static public ItemStack setPageAt(ItemStack stack, int id) {
		NBTTagCompound nbt = stack.getOrCreateSubCompound("page");
		nbt.setInteger("id", id);
		return stack;
	}
}
