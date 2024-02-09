package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.util.client.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.computer.render.GItemFrame;
import yuzunyannn.elementalsorcery.container.gui.GuiComputerTutorialPad;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GNode;

public class TutorialCraftSP extends TutorialCraft {

	static public TutorialCraft tryCreate(ItemStack itemStack) {
		if (itemStack.getItem() == ESObjects.ITEMS.SPELLBOOK_ENCHANTMENT) return new TutorialCraftEnchantBook();
		return null;
	}

	static public class TutorialCraftEnchantBook extends TutorialCraft {

		public GNode createNodeContainer(TutorialCraftNodeParams params) {
			GShowCommon container = new GShow(params);
			container.setPosition(params.width / 2, params.height / 2, 0);
			container.updateCraft();
			return container;
		}

		protected class GShow extends GShowCommon {

			protected GItemFrame tool;
			protected GItemFrame input;
			protected GItemFrame output;

			public GShow(TutorialCraftNodeParams params) {
				super(params);
				double xOffset = -8;
				double yOffset = -16;

				input = addSlot(xOffset - 25, yOffset);
				output = addSlot(xOffset + 25, yOffset);
				tool = addSlot(xOffset - 25, 34 + yOffset);
				addArrow(xOffset, yOffset);

				input.setItemStack(new ItemStack(Blocks.ENCHANTING_TABLE));
				output.setItemStack(new ItemStack(ESObjects.ITEMS.SPELLBOOK_ENCHANTMENT));
				ItemStack pickaxe = new ItemStack(ESObjects.ITEMS.KYANITE_PICKAXE);
				NBTTagCompound nbt = pickaxe.getOrCreateSubCompound("eInv");
				nbt.setInteger("size", 1);
				ElementStack eStack = new ElementStack(ESObjects.ELEMENTS.ENDER, 10, 5);
				NBTTagList list = new NBTTagList();
				nbt.setTag("list", list);
				nbt = eStack.serializeNBT();
				nbt.setInteger("slot", 0);
				list.appendTag(nbt);
				tool.setItemStack(pickaxe);

				GImage tool = new GImage(GuiComputerTutorialPad.TEXTURE,
						new RenderTexutreFrame(22, 215, 12, 14, 256, 256));
				tool.setColorRef(params.color);
				tool.setAnchor(0.5, 0);
				tool.setPosition(xOffset - 25, 10 + yOffset);
				addChild(tool);
			}

			public void updateCraft() {

			}
		}
	}
}
