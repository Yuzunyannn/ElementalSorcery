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
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.container.gui.GuiMDBase;
import yuzunyannn.elementalsorcery.container.gui.GuiNormal;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicSolidify;

public class MDMagicSolidifyRW implements MDRecipeWrapper {

	public static enum FakeRecipe {
		MAGIC_STONE, MAGIC_PIECE
	}

	final FakeRecipe recipe;

	public MDMagicSolidifyRW(FakeRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		if (this.recipe == FakeRecipe.MAGIC_STONE)
			ingredients.setOutput(ItemStack.class, TileMDMagicSolidify.MAGIC_STONE);
		else if (this.recipe == FakeRecipe.MAGIC_PIECE)
			ingredients.setOutput(ItemStack.class, TileMDMagicSolidify.MAGIC_PIECE);
	}

	@Override
	public void layout(IRecipeLayout layout) {
		IGuiItemStackGroup group = layout.getItemStacks();
		group.init(0, false, 87 - 9, 31);
		if (this.recipe == FakeRecipe.MAGIC_STONE) group.set(0, TileMDMagicSolidify.MAGIC_STONE);
		else group.set(0, TileMDMagicSolidify.MAGIC_PIECE);
	}

	@Override
	public void drawBackground(Minecraft mc, MDDraw draw, int offsetX, int offsetY) {
		float rate = 20 / 500.0f;
		if (recipe == FakeRecipe.MAGIC_STONE) rate = 100 / 500.0f;
		GuiMDBase.drawDefault(mc, rate, draw.getWidth(), draw.getHeight(), offsetX, offsetY, 59, 10,
				mc.getRenderPartialTicks(), MDDraw.TEXTURE1, MDDraw.TEXTURE2);
		int x = draw.getWidth() / 2;
		offsetY = 31;
		RenderFriend.drawTextureModalRect(offsetX + x - 9 - 11, offsetY - 7, 0, 216, 39, 34, 256, 256);
		draw.drawSolt(offsetX + x - 9, offsetY);
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		if (GuiNormal.isMouseIn(mouseX, mouseY, 15, 59, 144, 10)) {
			List<String> list = new LinkedList<String>();
			String name = I18n.format("element.magic.name");
			if (this.recipe == FakeRecipe.MAGIC_STONE) list.add(I18n.format("es.pageui.crafting.show", name, 100, 25));
			else list.add(I18n.format("info.arcCrystal.count", name, 20));
			return list;
		}
		return Collections.emptyList();
	}

}
