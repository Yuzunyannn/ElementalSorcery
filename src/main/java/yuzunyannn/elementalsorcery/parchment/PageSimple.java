package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.container.gui.GuiParchment;

public class PageSimple extends PageEasy {
	protected final String title;
	protected final String value;
	protected final ItemStack icon;
	protected final ItemStack blockground;

	@Deprecated
	public PageSimple(String name) {
		this.title = "page." + name;
		this.value = title + ".ct";
		this.icon = ItemStack.EMPTY;
		this.blockground = ItemStack.EMPTY;
	}

	public PageSimple(String title, String value, ItemStack icon, ItemStack background) {
		this.title = title;
		this.value = value;
		this.icon = icon;
		this.blockground = background;
	}

	@Override
	public String getTitle() {
		return "es.page." + title + ".title";
	}

	@Override
	public String getContext() {
		return "es.page." + value + ".describe";
	}

	@Override
	public ItemStack getIcon() {
		return icon;
	}

	@Override
	public void drawBackground(int xoff, int yoff, IPageManager pageManager) {
		if (this.blockground.isEmpty()) return;
		RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
		IBakedModel bakedmodel = itemRender.getItemModelWithOverrides(blockground, (World) null,
				(EntityLivingBase) null);
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		GlStateManager.pushMatrix();
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		GlStateManager.translate(xoff + 175, yoff + 110, -20F);
		GlStateManager.translate(8.0F, 8.0F, 20);
		GlStateManager.scale(1.0F, -1.0F, 1.0F);
		GlStateManager.scale(12.0F, 12.0F, 12.0F);
		GlStateManager.scale(5f, 5f, 5f);
		bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel,
				ItemCameraTransforms.TransformType.GUI, false);
		itemRender.renderItem(this.blockground, bakedmodel);
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		GlStateManager.popMatrix();

		TextureBinder.bindTexture(GuiParchment.TEXTURE);
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 5);
		GlStateManager.color(1, 1, 1, 0.5f);
		RenderFriend.drawTextureModalRect(xoff + 145, yoff + 81, 145, 81, 75, 75, 256, 256);
		GlStateManager.popMatrix();

	}
}
