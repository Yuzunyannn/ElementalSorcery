package yuzunyannn.elementalsorcery.parchment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.render.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.computer.render.GItemFrame;
import yuzunyannn.elementalsorcery.container.gui.GuiComputerTutorialPad;
import yuzunyannn.elementalsorcery.crafting.ISmashRecipe;
import yuzunyannn.elementalsorcery.nodegui.GImage;

public class TutorialCraftSmashHammer extends TutorialCraft {

	static public TutorialCraftSmashHammer tryCreate(ItemStack itemStack) {
		List<ISmashRecipe> recipes = ISmashRecipe.recipes;
		List<ISmashRecipe> useRecipes = new ArrayList<>();
		for (ISmashRecipe recipe : recipes) {
			for (ItemStack result : recipe.getOutputs()) {
				if (isItemStackThinkSame(itemStack, result)) {
					useRecipes.add(recipe);
					break;
				}
			}
		}
		if (useRecipes.isEmpty()) return null;
		return new TutorialCraftSmashHammer(useRecipes);
	}

	protected List<ISmashRecipe> list;

	protected TutorialCraftSmashHammer(List<ISmashRecipe> recipes) {
		this.list = recipes;
	}

	@Override
	public GShowCommon createMyContainer(TutorialCraftNodeParams params) {
		return new GShow(params);
	}

	protected class GShow extends GShowCommon {

		protected GItemFrame input;
		protected List<GItemFrame> outputs = new ArrayList<>();

		public GShow(TutorialCraftNodeParams params) {
			super(params);
			initCraft(params, ESObjects.ITEMS.MILL_HAMMER);

			double xOffset = -8 - 25;
			input = addSlot(xOffset - 25, 0);
			addArrow(xOffset, 0);
			for (int i = 0; i < 16; i++) {
				int x = (i % 4) * 18;
				int y = (i / 4) * 18;
				outputs.add(addSlot(xOffset + x + 25, y - 18 - 9));
			}

			GImage hammer = new GImage(GuiComputerTutorialPad.TEXTURE,
					new RenderTexutreFrame(23, 173, 20, 20, 256, 256));
			hammer.setColorRef(params.color);
			hammer.setAnchor(0.5, 1);
			hammer.setPosition(xOffset - 25, -10);
			addChild(hammer);
		}

		@Override
		protected Collection<?> getElements() {
			return list;
		}

		public void updateCraft() {
			if (list.isEmpty()) return;
			ISmashRecipe recipe = list.get(showIndex);
			input.setItemStack(getItemStack(recipe.getIngredient()));
			List<ItemStack> list = recipe.getOutputs();
			for (int i = 0; i < outputs.size(); i++) {
				if (i < list.size()) outputs.get(i).setItemStack(list.get(i));
				else outputs.get(i).setItemStack(ItemStack.EMPTY);
			}
		}

	}

}
