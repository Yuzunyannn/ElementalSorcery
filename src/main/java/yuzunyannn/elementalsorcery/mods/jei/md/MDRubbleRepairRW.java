package yuzunyannn.elementalsorcery.mods.jei.md;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.container.gui.GuiMDBase;
import yuzunyannn.elementalsorcery.container.gui.GuiMDRubbleRepair;
import yuzunyannn.elementalsorcery.container.gui.GuiNormal;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

public class MDRubbleRepairRW implements MDRecipeWrapper {
	final TileMDRubbleRepair.Recipe recipe;

	public MDRubbleRepairRW(TileMDRubbleRepair.Recipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(ItemStack.class, this.recipe.getInput());
		ingredients.setOutput(ItemStack.class, this.recipe.getOutput());
	}

	@Override
	public void layout(IRecipeLayout layout) {
		IGuiItemStackGroup group = layout.getItemStacks();
		group.init(0, true, 87 - 18 - 12, 21);
		group.init(1, false, 87 + 12, 21);
		group.set(0, recipe.getInput());
		group.set(1, recipe.getOutput());
	}

	@Override
	public void drawBackground(Minecraft mc, MDDraw draw, int offsetX, int offsetY) {
		float rate = recipe.getCost() / 500.0f;
		GuiMDBase.drawDefault(mc, rate, draw.getWidth(), draw.getHeight(), offsetX, offsetY, 59, 10,
				mc.getRenderPartialTicks(), MDDraw.TEXTURE1, MDDraw.TEXTURE2);
		GuiMDRubbleRepair.drawMagicSign(offsetX + 6, offsetY + 30, 25, 15);
		GuiMDRubbleRepair.drawMagicSign(offsetX + 6, offsetY + 15, 25, 15);
		int x = draw.getWidth() / 2;
		offsetY = 21;
		draw.drawSolt(offsetX + x - 18 - 12, offsetY);
		draw.drawSolt(offsetX + x + 12, offsetY);
		RenderHelper.drawTexturedModalRect(offsetX + x - 11, offsetY + 2, 25, 83, 22, 15, 256, 256);
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		if (GuiNormal.isMouseIn(mouseX, mouseY, 15, 59, 144, 10)) {
			List<String> list = new LinkedList<String>();
			int cost = recipe.getCost();
			list.add(I18n.format("info.arcCrystal.count", I18n.format("element.magic.name"), cost));
			return list;
		}
		return Collections.emptyList();
	}

}
