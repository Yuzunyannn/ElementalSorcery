package yuzunyannn.elementalsorcery.mods.jei.md;

import java.util.Collections;
import java.util.List;

import mezz.jei.api.gui.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;

public class MDDraw implements IDrawable {

	public static final ResourceLocation TEXTURE1 = new ResourceLocation(ESAPI.MODID,
			"textures/gui/container/md_jei1.png");
	public static final ResourceLocation TEXTURE2 = new ResourceLocation(ESAPI.MODID,
			"textures/gui/container/md_jei2.png");

	public MDDraw() {
	}

	@Override
	public int getWidth() {
		return 175;
	}

	@Override
	public int getHeight() {
		return 99;
	}

	@Override
	public void draw(Minecraft minecraft, int xOffset, int yOffset) {
		if (mdRW == null) return;
		mdRW.drawBackground(minecraft, this, xOffset, yOffset);
		minecraft.getTextureManager().bindTexture(TEXTURE2);
		RenderFriend.drawTextureModalRect(xOffset, yOffset + 70, 0, 70, 175, 29, 256, 256);
	}

	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return Collections.emptyList();
	}

	private MDRecipeWrapper mdRW;

	public void setMDCategory(MDRecipeWrapper mdRW) {
		this.mdRW = mdRW;
	}

	public void drawSolt(int x, int y) {
		RenderFriend.drawTextureModalRect(x, y, 7, 83, 18, 18, 256, 256);
	}

}
