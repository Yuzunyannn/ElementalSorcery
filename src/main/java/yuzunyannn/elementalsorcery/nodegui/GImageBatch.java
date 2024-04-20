package yuzunyannn.elementalsorcery.nodegui;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

@SideOnly(Side.CLIENT)
public class GImageBatch extends GNode {

	protected ResourceLocation textureResource = RenderObjects.ASTONE;

	public GImageBatch() {
	}

	public GImageBatch(ResourceLocation textureResource) {
		this.setTextureResource(textureResource);
	}

	public GImageBatch(TextureBinder textureResource) {
		this.setTextureResource(textureResource);
	}

	public void setTextureResource(ResourceLocation textureResource) {
		this.textureResource = textureResource == null ? this.textureResource : textureResource;
	}

	public void setTextureResource(TextureBinder textureBinder) {
		setTextureResource(textureBinder.getResource());
	}

	public void bindTextre() {
		mc.getTextureManager().bindTexture(textureResource);
	}

	@Override
	public void draw(float partialTicks) {
		if (!visible) return;
		updateRenderProps(partialTicks);
		this.bindTextre();
		GlStateManager.color(color.r, color.g, color.b, rAlpha);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		for (GNode node : this.children) node.draw(bufferbuilder, partialTicks);
		tessellator.draw();
	}

}
