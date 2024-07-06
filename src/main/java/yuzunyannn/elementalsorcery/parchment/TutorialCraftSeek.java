package yuzunyannn.elementalsorcery.parchment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.render.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.computer.render.GItemFrame;
import yuzunyannn.elementalsorcery.container.gui.GuiComputerTutorialPad;
import yuzunyannn.elementalsorcery.crafting.mc.RecipeRiteWrite;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class TutorialCraftSeek extends TutorialCraft {

	static public TutorialCraftSeek tryCreate(ItemStack itemStack) {
		TileRiteTable.Recipe recipe = null;
		for (TileRiteTable.Recipe r : TileRiteTable.getRecipes()) {
			if (isItemStackThinkSame(itemStack, r.getOutput())) {
				recipe = r;
				break;
			}
		}
		if (recipe == null) return null;
		return new TutorialCraftSeek(recipe);
	}

	protected List<Ingredient> list;
	protected ItemStack input;
	protected ItemStack output;

	protected TutorialCraftSeek(TileRiteTable.Recipe recipes) {
		input = recipes.parchmentInput();
		output = recipes.getOutput();
	}

	@Override
	public GShowCommon createMyContainer(TutorialCraftNodeParams params) {
		return new GShow(params);
	}

	protected class GShow extends GShowCommon {

		protected GItemFrame input;
		protected GItemFrame inputPage;
		protected GItemFrame output;

		public GShow(TutorialCraftNodeParams params) {
			super(params);
			ItemStack stack = new ItemStack(ESObjects.BLOCKS.RITE_TABLE);
			ItemHelper.getOrCreateTagCompound(stack).setInteger("level", params.tutorial.getLevel());
			initCraft(params, stack);
			
			double xOffset = 12;
			double padOffsetY = 9;

			List<GItemFrame> list = new ArrayList<>(9);
			for (int r = 0; r < 3; r++) {
				for (int c = 0; c < 3; c++) {
					list.add(addSlot(c * 18 - 18 * 3 / 2 + xOffset - 18 * 3, r * 18 - 18 * 3 / 2 + padOffsetY));
				}
			}

			addArrow(xOffset - 23, 0);

			list.get(0).setItemStack(new ItemStack(Items.FEATHER));
			list.get(1).setItemStack(new ItemStack(Items.DYE));
			list.get(3).setItemStack(new ItemStack(ESObjects.ITEMS.PARCHMENT));
			input = list.get(4);
			inputPage = addSlot(xOffset, 0);
			output = addSlot(xOffset + 50, 0);

			GImage fire = new GImage(GuiComputerTutorialPad.TEXTURE, new RenderTexutreFrame(45, 178, 23, 16, 256, 256));
			fire.setColorRef(params.color);
			fire.setAnchor(0.5, 0.5);
			fire.setPosition(xOffset + 25, 0);
			addChild(fire);
		}

		@Override
		protected Collection<?> getElements() {
			return list;
		}

		public void updateCraft() {
			input.setItemStack(TutorialCraftSeek.this.input);
			output.setItemStack(TutorialCraftSeek.this.output);
			ItemStack parchment = ItemParchment.getParchment(null);
			RecipeRiteWrite.setInnerStack(parchment, TutorialCraftSeek.this.input);
			inputPage.setItemStack(parchment);
		}

	}

}
