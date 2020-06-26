package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PageSimple extends PageEasy {
	protected final String title;
	protected final String value;
	protected final ItemStack icon;
	protected final ItemStack blockground;

	public PageSimple(String name) {
		this.title = "page." + name;
		this.value = title + ".ct";
		this.icon = ItemStack.EMPTY;
		this.blockground = ItemStack.EMPTY;
	}

	public PageSimple(String name, ItemStack icon) {
		this.title = "page." + name;
		this.value = title + ".ct";
		this.icon = icon;
		this.blockground = ItemStack.EMPTY;
	}

	public PageSimple(String name, Item icon) {
		this(name, new ItemStack(icon));
	}

	public PageSimple(String name, Block icon) {
		this(name, new ItemStack(icon));
	}

	public PageSimple(String name, ItemStack icon, ItemStack background) {
		this.title = "page." + name;
		this.value = title + ".ct";
		this.icon = icon;
		this.blockground = background;
	}

	public PageSimple(String title, String value, ItemStack icon, ItemStack background) {
		this.title = title;
		this.value = value;
		this.icon = icon;
		this.blockground = background;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getContext() {
		return value;
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
		GlStateManager.translate(xoff + 175, yoff + 110, 100.0F);
		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.0F, -1.0F, 1.0F);
		GlStateManager.scale(12.0F, 12.0F, 12.0F);
		GlStateManager.scale(5f, 5f, 5f);
		bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel,
				ItemCameraTransforms.TransformType.GUI, false);
		itemRender.renderItem(this.blockground, bakedmodel);
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		GlStateManager.popMatrix();
	}
}
