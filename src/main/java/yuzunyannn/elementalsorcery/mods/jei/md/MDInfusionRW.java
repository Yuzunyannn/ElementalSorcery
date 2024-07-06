package yuzunyannn.elementalsorcery.mods.jei.md;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.container.gui.GuiMDBase;
import yuzunyannn.elementalsorcery.container.gui.GuiNormal;
import yuzunyannn.elementalsorcery.tile.md.TileMDInfusion;

public class MDInfusionRW implements MDRecipeWrapper {
	final TileMDInfusion.Recipe recipe;
	final int cost;

	public MDInfusionRW(TileMDInfusion.Recipe recipe) {
		this.recipe = recipe;
		int cost = Integer.MAX_VALUE;
		for (int i = 0; i < 5; i++) {
			ElementStack maxMagic = TileMDInfusion.getMaxOfferMagic(i);
			if (maxMagic.getCount() >= recipe.getCost().getCount()
					&& maxMagic.getPower() >= recipe.getCost().getPower()) {
				if (cost > maxMagic.getCount()) cost = maxMagic.getCount();
			}
		}
		this.cost = cost;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(ItemStack.class, this.recipe.getInput());
		ingredients.setOutput(ItemStack.class, this.recipe.getOutput());
	}

	@Override
	public void layout(IRecipeLayout layout) {
		IGuiItemStackGroup group = layout.getItemStacks();
		int offsetY = -10;
		int offsetX = 0;
		group.init(0, true, offsetX + 35, offsetY + 41);
		group.init(1, true, offsetX + 57, offsetY + 49);
		group.init(2, true, offsetX + 79, offsetY + 57);
		group.init(3, true, offsetX + 101, offsetY + 49);
		group.init(4, true, offsetX + 123, offsetY + 41);
		for (int i = 0; i < 5; i++) {
			ElementStack maxMagic = TileMDInfusion.getMaxOfferMagic(i);
			if (maxMagic.getCount() >= recipe.getCost().getCount()
					&& maxMagic.getPower() >= recipe.getCost().getPower()) {
				List<ItemStack> stacks = new ArrayList<ItemStack>(2);
				stacks.add(recipe.getInput());
				stacks.add(recipe.getOutput());
				group.set(i, stacks);
			}
		}
	}

	@Override
	public void drawBackground(Minecraft mc, MDDraw draw, int offsetX, int offsetY) {
		mc.getTextureManager().bindTexture(MDDraw.TEXTURE1);
		offsetY -= 10;
		RenderFriend.drawTextureModalRect(offsetX + 15, offsetY + 19, 0, 166, 144, 10, 256, 256);
		float rate = this.cost / 500.0f;
		GuiMDBase.drawMagicVolume(offsetX + 15, offsetY + 19, 144, 10, rate, mc.getRenderPartialTicks());
		mc.getTextureManager().bindTexture(MDDraw.TEXTURE1);
		RenderFriend.drawTextureModalRect(offsetX, offsetY, 0, 0, draw.getWidth(), draw.getHeight(), 256, 256);
		mc.getTextureManager().bindTexture(MDDraw.TEXTURE2);
		RenderFriend.drawTextureModalRect(offsetX + 14, offsetY + 30, 14, 30, 146, 40, 256, 256);
		mc.getTextureManager().bindTexture(MDDraw.TEXTURE1);
		RenderFriend.drawTextureModalRect(offsetX + 14, offsetY + 18 + 11, 14, 18 + 51, 146, 1, 256, 256);
		// 所有物品栏
		RenderFriend.drawTextureModalRect(offsetX + 35, offsetY + 41, 7, 83, 18, 18, 256, 256);
		RenderFriend.drawTextureModalRect(offsetX + 57, offsetY + 49, 7, 83, 18, 18, 256, 256);
		RenderFriend.drawTextureModalRect(offsetX + 79, offsetY + 57, 7, 83, 18, 18, 256, 256);
		RenderFriend.drawTextureModalRect(offsetX + 101, offsetY + 49, 7, 83, 18, 18, 256, 256);
		RenderFriend.drawTextureModalRect(offsetX + 123, offsetY + 41, 7, 83, 18, 18, 256, 256);
		// 进度背景
		RenderFriend.drawTextureModalRect(offsetX + 41, offsetY + 30, 176, 55, 5, 11, 256, 256);
		RenderFriend.drawTextureModalRect(offsetX + 63, offsetY + 30, 176, 55, 5, 19, 256, 256);
		RenderFriend.drawTextureModalRect(offsetX + 85, offsetY + 30, 176, 55, 5, 27, 256, 256);
		RenderFriend.drawTextureModalRect(offsetX + 107, offsetY + 30, 176, 55, 5, 19, 256, 256);
		RenderFriend.drawTextureModalRect(offsetX + 129, offsetY + 30, 176, 55, 5, 11, 256, 256);
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		if (GuiNormal.isMouseIn(mouseX, mouseY, 15, 19 - 10, 144, 10)) {
			List<String> list = new LinkedList<String>();
			int power = recipe.getCost().getPower();
			list.add(I18n.format("es.pageui.crafting.show", I18n.format("element.magic.name"), cost, power));
			return list;
		}
		return Collections.emptyList();
	}

}
