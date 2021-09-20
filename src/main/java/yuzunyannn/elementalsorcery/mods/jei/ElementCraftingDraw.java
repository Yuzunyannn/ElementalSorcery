package yuzunyannn.elementalsorcery.mods.jei;

import java.util.List;

import mezz.jei.api.gui.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.gui.GuiSupremeTable;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.event.EventClient;

public class ElementCraftingDraw implements IDrawable {

	static final public ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/jei/element_crafting_table.png");

	@Override
	public int getWidth() {
		return 178;
	}

	@Override
	public int getHeight() {
		return 126;
	}

	@Override
	public void draw(Minecraft minecraft, int xOffset, int yOffset) {
		minecraft.getTextureManager().bindTexture(TEXTURE);
		Gui.drawModalRectWithCustomSizedTexture(xOffset + 9, yOffset - 1, 0, 0, 160, 126, 256, 256);
		if (list == null) return;
		int xoff = 9 - 35;
		int yoff = -1 - 21;
		GuiSupremeTable.drawElements(minecraft, xoff, yoff, list, 6, EventClient.tick / 40);
	}

	private List<ElementStack> list;

	public void setElementList(List<ElementStack> list) {
		this.list = list;
	}

}
