package yuzunyannn.elementalsorcery.mods.jei;

import mezz.jei.api.gui.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.init.ESInit;

public class RiteIcon implements IDrawable {

	public ItemStack iconStack = ItemStack.EMPTY;
	public ItemStack defaultIconStack = new ItemStack(ESInit.BLOCKS.RITE_TABLE);

	@Override
	public int getWidth() {
		return 16;
	}

	@Override
	public int getHeight() {
		return 16;
	}

	public void setIconStack(ItemStack iconStack) {
		this.iconStack = iconStack;
	}

	@Override
	public void draw(Minecraft minecraft, int xOffset, int yOffset) {
		ItemStack drawStack = defaultIconStack;
		if (!iconStack.isEmpty()) drawStack = iconStack;

		GlStateManager.enableDepth();
		RenderHelper.enableGUIStandardItemLighting();
		FontRenderer font = minecraft.fontRenderer;
		minecraft.getRenderItem().renderItemAndEffectIntoGUI(null, drawStack, xOffset, yOffset);
		minecraft.getRenderItem().renderItemOverlayIntoGUI(font, drawStack, xOffset, yOffset, null);
		GlStateManager.disableBlend();
		RenderHelper.disableStandardItemLighting();
	}

}
