package yuzunyan.elementalsorcery.parchment;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import yuzunyan.elementalsorcery.init.ESInitInstance;
import yuzunyan.elementalsorcery.parchment.Pages1.AboutEStone;
import yuzunyan.elementalsorcery.parchment.Pages1.AboutEnchantingBook;
import yuzunyan.elementalsorcery.parchment.Pages1.AboutHearth;
import yuzunyan.elementalsorcery.parchment.Pages1.AboutKynatieTools;
import yuzunyan.elementalsorcery.parchment.Pages1.AboutMagicDesk;
import yuzunyan.elementalsorcery.parchment.Pages1.AboutSPElement;
import yuzunyan.elementalsorcery.parchment.Pages1.AboutSPLaunch;
import yuzunyan.elementalsorcery.parchment.Pages1.AboutSmeltBox;
import yuzunyan.elementalsorcery.parchment.Pages1.AboutSpellbook;

public class Pages {
	static public List<Entry<Item, Integer>> Item_Id = new ArrayList<Entry<Item, Integer>>();
	static public int ERROR;
	static public int ABOUT_ELEMENT;
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
	static public int ABOUT_DEC_BOX;
	static public int ABOUT_MAGIC_PAPER;
	static public int ABOUT_SPELL_PAPER;
	static public int ABOUT_BOOKCOVER;
	static public int ABOUT_SPELLBOOK;
	static public int ABOUT_MAGICDESK;
	static public int ABOUT_SPLAUNCH;
	static public int ABOUT_SPELEMENT;

	static public void initPre() {
		ERROR = Page.addPage(new PageError());
	}

	static public void init() {
		// 1
		ABOUT_ELEMENT = Page.addPage(new PageSimple("element"));
		ABOUT_HEARTH = Page.addPage(new AboutHearth());
		ABOUT_SMELT_BOX = Page.addPage(new AboutSmeltBox());
		ABOUT_KYNAITE = Page.addPage(new PageSmeltingSimple("kynaite", ESInitInstance.BLOCKS.KYNAITE_ORE));
		ABOUT_MAGICAL_PIECE = Page.addPage(new PageSimple("mgical_piece"));
		ABOUT_INFUSION = Page.addPage(new PageCraftingSimple("infusion", ESInitInstance.BLOCKS.INFUSION_BOX));
		ABOUT_MAGICAL_ENDEREYE = Page
				.addPage(new PageCraftingSimple("magical_endereye", ESInitInstance.ITEMS.MAGICAL_ENDER_EYE));
		ABOUT_KYNATIE_TOOLS = Page.addPage(new AboutKynatieTools());
		ABOUT_ABSORB_BOX = Page.addPage(new PageCraftingSimple("absorbBox", ESInitInstance.BLOCKS.ABSORB_BOX));
		ABOUT_MAGIC_PL = Page.addPage(new PageCraftingSimple("magicPl", ESInitInstance.BLOCKS.MAGIC_PLATFORM));
		// 11
		ABOUT_MAGIC_CRY = Page.addPage(new PageTransformSimple("magicCry", ESInitInstance.ITEMS.KYNAITE,
				ESInitInstance.ITEMS.MAGIC_CRYSTAL, PageTransform.INFUSION));
		ABOUT_MAGIC_ESTONE= Page.addPage(new AboutEStone());
		ABOUT_ELEMENT_CRY = Page.addPage(new PageTransformSimple("elementCry", ESInitInstance.ITEMS.MAGIC_CRYSTAL,
				ESInitInstance.ITEMS.ELEMENT_CRYSTAL, PageTransform.INFUSION));
		ABOUT_ENCHANTINGBOOK = Page.addPage(new AboutEnchantingBook());
		ABOUT_SPELL_CRY = Page.addPage(new PageTransformSimple("spellCry", ESInitInstance.ITEMS.MAGIC_CRYSTAL,
				ESInitInstance.ITEMS.SPELL_CRYSTAL, PageTransform.INFUSION));
		ABOUT_EWORKBENCH = Page.addPage(new PageCraftingSimple("EWorkbench", ESInitInstance.BLOCKS.ELEMENT_WORKBENCH));
		ABOUT_DEC_BOX = Page.addPage(new PageCraftingSimple("decBox", ESInitInstance.BLOCKS.DECONSTRUCT_BOX));
		ABOUT_MAGIC_PAPER = Page.addPage(new PageCraftingSimple("magicPaper", ESInitInstance.ITEMS.MAGIC_PAPER));
		ABOUT_SPELL_PAPER = Page.addPage(new PageCraftingSimple("spellPaper", ESInitInstance.ITEMS.SPELL_PAPER));
		ABOUT_BOOKCOVER = Page
				.addPage(new PageCraftingSimple("bookCover", new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_COVER, 1, 0),
						new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_COVER, 1, 1)));
		// 21
		ABOUT_SPELLBOOK = Page.addPage(new AboutSpellbook());
		ABOUT_MAGICDESK = Page.addPage(new AboutMagicDesk());
		ABOUT_SPLAUNCH = Page.addPage(new AboutSPLaunch());
		ABOUT_SPELEMENT = Page.addPage(new AboutSPElement());
		// 初始化item到id图
		Pages.initItemToId();
	}

	// 最大页数，服务端判断使用！！！！
	static public final int REAL_PAGE_COUNT = 1 + 23;

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
}
