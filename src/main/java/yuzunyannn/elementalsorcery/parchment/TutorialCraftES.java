package yuzunyannn.elementalsorcery.parchment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.crafting.IElementRecipe;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.computer.render.GItemFrame;
import yuzunyannn.elementalsorcery.container.ContainerSupremeTable;
import yuzunyannn.elementalsorcery.container.gui.GuiSupremeTable;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.nodegui.GNode;

public class TutorialCraftES extends TutorialCraft {

	static public TutorialCraftES tryCreate(ItemStack itemStack) {
		List<IElementRecipe> list = new ArrayList<>();
		for (IElementRecipe ire : ESAPI.recipeMgr.getValues()) {
			if (isItemStackThinkSame(itemStack, ire.getRecipeOutput())) list.add(ire);
		}
		if (list.isEmpty()) return null;
		return new TutorialCraftES(list);
	}

	protected List<Entry<List<Ingredient>, ItemStack>> list = new ArrayList<>();
	protected List<List<ElementStack>> eList = new ArrayList<>();

	public TutorialCraftES(List<IElementRecipe> irecipes) {
		for (IElementRecipe irecipe : irecipes) {
			NonNullList<Ingredient> ingLIst = irecipe.getIngredients();
			list.add(entryOf(ingLIst, irecipe.getRecipeOutput()));
			eList.add(irecipe.getNeedElements());
		}
	}

	@Override
	public GNode createNodeContainer(TutorialCraftNodeParams params) {
		GShowCommon container = new GShow(params);
		container.setPosition(params.width / 2 - 1, params.height / 2, 0);
		container.updateCraft();
		return container;
	}

	protected class GShow extends GShowCommon {

		protected List<ElementStack> eStacks;
		protected List<GItemFrame> inputs = new ArrayList<>();
		protected GItemFrame output;

		@Override
		protected Collection<?> getElements() {
			return list;
		}

		public GShow(TutorialCraftNodeParams params) {
			super(params);
			double xoffset = 0;
			double padOffsetX = -18 + xoffset;
			double padOffsetY = -18;
			for (int i = 0; i < 25; i++) {
				int x = ContainerSupremeTable.craftingRelative[i * 2];
				int y = ContainerSupremeTable.craftingRelative[i * 2 + 1];
				inputs.add(addSlot(x + padOffsetX, y + padOffsetY));
			}
			output = addSlot(xoffset, 36 + 9);
		}

		@Override
		public void updateCraft() {
			if (list.isEmpty()) return;
			Entry<List<Ingredient>, ItemStack> recipe = list.get(showIndex);
			output.setItemStack(recipe.getValue());
			List<Ingredient> list = recipe.getKey();
			int listSize = list.size();
			for (int i = 0; i < inputs.size(); i++) {
				if (i < list.size()) {
					Ingredient ingredient = list.get(i);
					ItemStack[] stacks = ingredient.getMatchingStacks();
					if (stacks.length == 0) inputs.get(i).setItemStack(ItemStack.EMPTY);
					else inputs.get(i).setItemStack(stacks[0]);
				} else inputs.get(i).setItemStack(ItemStack.EMPTY);
				if (i >= 9) inputs.get(i).setVisible(listSize > 9);
			}
			eStacks = eList.get(showIndex);
//			eStacks = new ArrayList<>();
//			eStacks.add(new ElementStack(ESObjects.ELEMENTS.EARTH, 10));
//			eStacks.add(new ElementStack(ESObjects.ELEMENTS.WATER, 10));
//			eStacks.add(new ElementStack(ESObjects.ELEMENTS.FIRE, 10));
//			eStacks.add(new ElementStack(ESObjects.ELEMENTS.WOOD, 10));
//			eStacks.add(new ElementStack(ESObjects.ELEMENTS.ENDER, 10));
//			eStacks.add(new ElementStack(ESObjects.ELEMENTS.AIR, 10));
//			eStacks.add(new ElementStack(ESObjects.ELEMENTS.STAR, 10));
//			eStacks.add(new ElementStack(ESObjects.ELEMENTS.KNOWLEDGE, 10));
//			eStacks.add(new ElementStack(ESObjects.ELEMENTS.METAL, 10));
		}

		@Override
		protected void render(float partialTicks) {
			super.render(partialTicks);
			if (eStacks != null) {
				GlStateManager.translate(0, 0.5, 100);
				GuiSupremeTable.drawElements(mc, -70 - 45, -75 - 9, eStacks, 8, EventClient.tick / 40);
				GlStateManager.translate(0, -0.5, -100);
			}
		}

	}

}
