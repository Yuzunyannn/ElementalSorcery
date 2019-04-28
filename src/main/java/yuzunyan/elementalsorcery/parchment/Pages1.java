package yuzunyan.elementalsorcery.parchment;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import yuzunyan.elementalsorcery.api.ability.IElementInventory;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.building.Building;
import yuzunyan.elementalsorcery.building.Buildings;
import yuzunyan.elementalsorcery.capability.ElementInventory;
import yuzunyan.elementalsorcery.init.ESInitInstance;
import yuzunyan.elementalsorcery.item.ItemKynaiteTools;
import yuzunyan.elementalsorcery.tile.TileMagicDesk;
import yuzunyan.elementalsorcery.util.TextHelper;

public class Pages1 {

	static class WithPages extends Page {
		protected int page = 0;
		protected int max_page = 0;

		@Override
		public void open() {
			page = 0;
		}

		@Override
		public int prePage() {
			if (page <= 0)
				return -1;
			else
				return this.getId();
		}

		@Override
		public int nextPage() {
			if (page >= this.max_page)
				return -1;
			else
				return this.getId();
		}

		@Override
		public void nextPageUpdate() {
			page++;
		}

		@Override
		public void prePageUpdate() {
			page--;
		}
	}

	static class CraftingWithPages extends PageCrafting {
		protected int page = 0;
		protected int max_page = 0;

		public CraftingWithPages(ItemStack... stacks) {
			super(stacks);
		}

		public CraftingWithPages(Block block) {
			super(block);
		}

		public CraftingWithPages(Item item) {
			super(item);
		}

		@Override
		public void open() {
			page = 0;
		}

		@Override
		public int prePage() {
			if (page <= 0)
				return -1;
			else
				return this.getId();
		}

		@Override
		public int nextPage() {
			if (page >= this.max_page)
				return -1;
			else
				return this.getId();
		}

		@Override
		public void nextPageUpdate() {
			page++;
		}

		@Override
		public void prePageUpdate() {
			page--;
		}
	}

	// 关于灶台的合成
	static class AboutHearth extends PageCrafting {
		public AboutHearth() {
			super(new ItemStack(ESInitInstance.BLOCKS.HEARTH, 1, 0), new ItemStack(ESInitInstance.BLOCKS.HEARTH, 1, 1),
					new ItemStack(ESInitInstance.BLOCKS.HEARTH, 1, 2));
		}

		@Override
		public String getTitle() {
			return "page.hearth";
		}

		@Override
		public String getContext() {
			return "page.hearth.ct";
		}
	}

	// 关于加热箱
	static class AboutSmeltBox extends PageCrafting {
		public AboutSmeltBox() {
			super(new ItemStack(ESInitInstance.BLOCKS.SMELT_BOX), new ItemStack(ESInitInstance.BLOCKS.SMELT_BOX_IRON),
					new ItemStack(ESInitInstance.BLOCKS.SMELT_BOX_KYNAITE));
		}

		@Override
		public String getTitle() {
			return "page.smeltbox";
		}

		@Override
		public String getContext() {
			return "page.smeltbox.ct";
		}
	}

	// 关于加热箱
	static class AboutKynatieTools extends PageCraftingSimple {
		public AboutKynatieTools() {
			super("kynatieTools", new ItemStack(ESInitInstance.ITEMS.KYNAITE_AXE),
					new ItemStack(ESInitInstance.ITEMS.KYNAITE_HOE),
					new ItemStack(ESInitInstance.ITEMS.KYNAITE_PICKAXE),
					new ItemStack(ESInitInstance.ITEMS.KYNAITE_SPADE),
					new ItemStack(ESInitInstance.ITEMS.KYNAITE_SWORD));
		}
	}

	// 关于魔力石英
	static class AboutEStone extends PageCraftingSimple {

		public AboutEStone() {
			super("estone", new ItemStack(ESInitInstance.BLOCKS.ESTONE, 1, 0),
					new ItemStack(ESInitInstance.BLOCKS.ESTONE, 1, 1),
					new ItemStack(ESInitInstance.BLOCKS.ESTONE, 1, 2), new ItemStack(ESInitInstance.BLOCKS.ESTONE_SLAB),
					new ItemStack(ESInitInstance.BLOCKS.ESTONE_STAIRS));
		}

	}

	// 关于附魔书
	static class AboutEnchantingBook extends PageTransformSimple {
		public AboutEnchantingBook() {
			super("enchantingBook", Blocks.ENCHANTING_TABLE, ESInitInstance.ITEMS.SPELLBOOK_ENCHANTMENT,
					ESInitInstance.ITEMS.KYNAITE_HOE, PageTransform.SEPARATE);
			((ItemKynaiteTools.toolsCapability) ESInitInstance.ITEMS.KYNAITE_HOE).provideOnce();
			this.extra = new ItemStack(ESInitInstance.ITEMS.KYNAITE_HOE);
			IElementInventory inv = this.extra.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
			inv.insertElement(new ElementStack(ESInitInstance.ELEMENTS.ENDER, 10, 5), false);
		}
	}

	// 关于魔导书
	static class AboutSpellbook extends CraftingWithPages {

		public AboutSpellbook() {
			super(ESInitInstance.ITEMS.SPELLBOOK);
			this.max_page = 2;
		}

		@Override
		public NonNullList<Ingredient> getCrafting() {
			if (page == 1)
				return super.getCrafting();
			else
				return null;
		}

		@Override
		public String getTitle() {
			return "page.spellbook";
		}

		@Override
		public void addContexts(List<String> contexts) {
			if (page == 1)
				super.addContexts(contexts);
			else if (page == 0) {
				TextHelper.addInfoCheckLine(contexts, "page.spellbook.ct.first");
			} else {
				TextHelper.addInfoCheckLine(contexts, "page.spellbook.ct.more");
			}
		}

		@Override
		public String getContext() {
			return "page.spellbook.ct";
		}
	}

	// 关于魔法书桌
	static class AboutMagicDesk extends CraftingWithPages {

		public AboutMagicDesk() {
			super(ESInitInstance.BLOCKS.MAGIC_DESK);
			this.max_page = 3;
		}

		@Override
		public NonNullList<Ingredient> getCrafting() {
			if (page == 0)
				return super.getCrafting();
			else
				return null;
		}

		@Override
		public void addContexts(List<String> contexts) {
			if (page == 0)
				super.addContexts(contexts);
			else if (page == 2) {
				TextHelper.addInfoCheckLine(contexts, "page.magicDesk.ct.crafting");
			} else {
				TextHelper.addInfoCheckLine(contexts, "page.magicDesk.ct.charge");
			}
		}

		@Override
		public String getContext() {
			return "page.magicDesk.ct";
		}

		@Override
		public String getTitle() {
			return "page.magicDesk";
		}

		public Building getBuilding() {
			if (page == 1)
				return Buildings.SPELLBOOK_ALTAR;
			return null;
		}
	}

	static class AboutSP extends WithPages {

		protected ItemStack book;
		protected ItemStack newBook;
		protected List<ItemStack> list;
		protected String name;

		public AboutSP(String name, Item book, Item newBook, List<ItemStack> list) {
			this.book = new ItemStack(book);
			this.newBook = new ItemStack(newBook);
			this.list = list;
			this.max_page = 1;
			this.name = name;
		}

		@Override
		public void addContexts(List<String> contexts) {
			if (page == 0)
				super.addContexts(contexts);
			else if (page == 1) {
				TextHelper.addInfoCheckLine(contexts, "page." + name + ".ct.info");
			}
		}

		@Override
		public String getContext() {
			return "page." + name + ".ct";
		}

		@Override
		public String getTitle() {
			return "page." + name;
		}

		public int getTransformGui() {
			return PageTransform.SPELLALTAR;
		}

		@Override
		public ItemStack getOrigin() {
			if (this.page == 0)
				return book;
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack getOutput() {
			return newBook;
		}

		@Override
		public List<ItemStack> getItemList() {
			return list;
		}

	}

	// 关于起始之魔导书
	static class AboutSPLaunch extends AboutSP {
		public AboutSPLaunch() {
			super("spLaunch", ESInitInstance.ITEMS.SPELLBOOK, ESInitInstance.ITEMS.SPELLBOOK_LAUNCH,
					TileMagicDesk.AUTO_LAUNCH_BOOK);
		}
	}

	// 关于元素之魔导书
	static class AboutSPElement extends AboutSP {
		public AboutSPElement() {
			super("spElement", ESInitInstance.ITEMS.SPELLBOOK, ESInitInstance.ITEMS.SPELLBOOK_ELEMENT,
					TileMagicDesk.AUTO_ELEMENT_BOOK);
		}
	}
}
