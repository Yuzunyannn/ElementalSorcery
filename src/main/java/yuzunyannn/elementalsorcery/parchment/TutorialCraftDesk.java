package yuzunyannn.elementalsorcery.parchment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.client.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.computer.render.GItemFrame;
import yuzunyannn.elementalsorcery.computer.render.SoftGuiCommon;
import yuzunyannn.elementalsorcery.container.gui.GuiComputerTutorialPad;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk.Recipe;

public class TutorialCraftDesk extends TutorialCraft {

	static public TutorialCraftDesk tryCreate(ItemStack itemStack) {
		List<Recipe> list = new ArrayList<>();
		for (Recipe ire : TileMagicDesk.getRecipes()) {
			if (isItemStackThinkSame(itemStack, ire.getRecipeOutput())) list.add(ire);
		}
		if (list.isEmpty()) return null;
		return new TutorialCraftDesk(list);
	}

	protected List<Recipe> list;

	public TutorialCraftDesk(List<Recipe> irecipes) {
		list = irecipes;
	}

	@Override
	public GShowCommon createMyContainer(TutorialCraftNodeParams params) {
		return new GShow(params);
	}

	protected class GShow extends GShowCommon {

		protected GItemFrame input;
		protected GItemFrame output;
		protected List<GItemFrame> order = new ArrayList<>();

		@Override
		protected Collection<?> getElements() {
			return list;
		}

		public GShow(TutorialCraftNodeParams params) {
			super(params);
			initCraft(params, ESObjects.BLOCKS.MAGIC_DESK);
			
			double xOffset = 0;
			double putOffsetY = -50;
			input = addSlot(xOffset - 24, putOffsetY);
			output = addSlot(xOffset + 24, putOffsetY);
			addArrow(xOffset, putOffsetY);

			GImage icon = new GImage(GuiComputerTutorialPad.TEXTURE, new RenderTexutreFrame(21, 231, 14, 17, 256, 256));
			icon.setColorRef(params.color);
			icon.setAnchor(0.5, 0);
			icon.setPosition(xOffset, putOffsetY + 10);
			icon.setSize(14 * 1.2, 17 * 1.2);
			addChild(icon);

			int length = 6;
			double sOffsetX = xOffset - ((length - 1) / 2.0 * 27);
			double sOffsetY = -8;

			for (int i = 0; i < length; i++) {
				order.add(addSlot(sOffsetX + i * 27, sOffsetY));
				if (i == length - 1) {
					icon = new GImage(SoftGuiCommon.TEXTURE_1, SoftGuiCommon.FRAME_ARROW_1_DOWN);
					icon.setAnchor(0.5, 0);
					icon.setColorRef(params.color);
					icon.setPosition(sOffsetX + i * 27, sOffsetY + 9);
					addChild(icon);
				} else {
					icon = new GImage(SoftGuiCommon.TEXTURE_1, SoftGuiCommon.FRAME_ARROW_1_RIGHT);
					icon.setColorRef(params.color);
					icon.setPosition(sOffsetX + i * 27 + 9, sOffsetY - 9);
					addChild(icon);
				}
			}

			sOffsetY = sOffsetY + 27;

			for (int i = length - 1; i >= 0; i--) {
				order.add(addSlot(sOffsetX + i * 27, sOffsetY));
				if (i == 0) {
					icon = new GImage(SoftGuiCommon.TEXTURE_1, SoftGuiCommon.FRAME_ARROW_1_DOWN);
					icon.setAnchor(0.5, 0);
					icon.setColorRef(params.color);
					icon.setPosition(sOffsetX + i * 27, sOffsetY + 9);
					addChild(icon);
				} else {
					icon = new GImage(SoftGuiCommon.TEXTURE_1, SoftGuiCommon.FRAME_ARROW_1_LEFT);
					icon.setColorRef(params.color);
					icon.setPosition(sOffsetX + i * 27 - 18, sOffsetY - 9);
					addChild(icon);
				}
			}

			sOffsetY = sOffsetY + 27;

			for (int i = 0; i < length; i++) {
				order.add(addSlot(sOffsetX + i * 27, sOffsetY));
				if (i == length - 1);
				else {
					icon = new GImage(SoftGuiCommon.TEXTURE_1, SoftGuiCommon.FRAME_ARROW_1_RIGHT);
					icon.setColorRef(params.color);
					icon.setPosition(sOffsetX + i * 27 + 9, sOffsetY - 9);
					addChild(icon);
				}
			}
		}

		@Override
		public void updateCraft() {
			if (list.isEmpty()) return;
			Recipe recipe = list.get(showIndex);
			output.setItemStack(recipe.getRecipeOutput());
			input.setItemStack(recipe.getRecipeInput());
			List<ItemStack> sequence = recipe.getSequence();
			for (int i = 0; i < order.size(); i++) {
				GItemFrame frame = order.get(i);
				if (i < sequence.size()) {
					ItemStack ingredient = sequence.get(i);
					frame.setItemStack(ingredient);
					frame.setHasHoverEffect(true);
				} else frame.setItemStack(ItemStack.EMPTY);
				frame.setHasHoverEffect(!frame.getItemStack().isEmpty());
			}
		}

	}

}
