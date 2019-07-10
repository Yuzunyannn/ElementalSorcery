package yuzunyannn.elementalsorcery.parchment;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.ability.IElementInventory;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemKynaiteTools;
import yuzunyannn.elementalsorcery.parchment.Pages.PageSimpleInfo;
import yuzunyannn.elementalsorcery.tile.TileMagicDesk;

public class Pages1 {

	// 关于灶台的合成
	static class AboutHearth extends PageCraftingSimple {
		public AboutHearth() {
			super("hearth", new ItemStack(ESInitInstance.BLOCKS.HEARTH, 1, 0),
					new ItemStack(ESInitInstance.BLOCKS.HEARTH, 1, 1),
					new ItemStack(ESInitInstance.BLOCKS.HEARTH, 1, 2));
		}
	}

	// 关于加热箱
	static class AboutSmeltBox extends PageCraftingSimple {
		public AboutSmeltBox() {
			super("smeltbox", new ItemStack(ESInitInstance.BLOCKS.SMELT_BOX),
					new ItemStack(ESInitInstance.BLOCKS.SMELT_BOX_IRON),
					new ItemStack(ESInitInstance.BLOCKS.SMELT_BOX_KYNAITE));
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
	static class AboutSpellbook extends PageMul {

		public AboutSpellbook() {
			super(new PageSimpleInfo("spellbook", "first"),
					new PageCraftingSimple("spellbook", ESInitInstance.ITEMS.SPELLBOOK),
					new PageSimpleInfo("spellbook", "more"));
		}

		@Override
		public ItemStack getIcon() {
			return new ItemStack(ESInitInstance.ITEMS.SPELLBOOK);
		}
	}

	// 关于魔法书桌
	static class AboutMagicDesk extends PageMul {

		public AboutMagicDesk() {
			super(new PageCraftingSimple("magicDesk", ESInitInstance.BLOCKS.MAGIC_DESK),
					new PageBuilding("magicDesk", Buildings.SPELLBOOK_ALTAR),
					new PageSimpleInfo("magicDesk", "crafting"), new PageSimpleInfo("magicDesk", "charge"));
		}
	}

	// 关于知识碑
	static class AboutStela extends PageMul {
		public AboutStela() {
			super(new PageSimple("how2ply"), new PageCraftingSimple("parchment", ESInitInstance.ITEMS.PARCHMENT),
					new PageSimple("stela", new ItemStack(ESInitInstance.BLOCKS.STELA),
							new ItemStack(ESInitInstance.BLOCKS.STELA)));
		}

		@Override
		public String getItemInfo() {
			return "page.stela";
		}

		@Override
		public ItemStack getIcon() {
			return pages[2].getIcon();
		}
	}

	static class AboutSP extends PageMul {

		public AboutSP(String name, Item book, Item newBook, List<ItemStack> list) {
			super(new PageTransformSimple(name, book, newBook, list, PageTransform.SPELLALTAR),
					new PageSimpleInfo(name, "info"));
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
