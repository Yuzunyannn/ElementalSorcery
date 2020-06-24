package yuzunyannn.elementalsorcery.mods.jei.md;

import java.util.Collections;
import java.util.List;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;

public interface MDRecipeWrapper extends IRecipeWrapper {

	abstract void layout(IRecipeLayout layout);

	abstract void drawBackground(Minecraft mc, MDDraw draw, int offsetX, int offsetY);

	default List<String> getTooltipStrings(int mouseX, int mouseY) {
		return Collections.emptyList();
	}

}
