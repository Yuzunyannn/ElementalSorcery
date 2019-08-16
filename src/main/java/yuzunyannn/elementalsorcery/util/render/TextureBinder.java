package yuzunyannn.elementalsorcery.util.render;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;

public class TextureBinder {

	private int texId = -1;

	public TextureBinder(String path) {
		this(ElementalSorcery.MODID, path);
	}

	public TextureBinder(String domain, String path) {
		init(new ResourceLocation(domain, path));
	}

	public TextureBinder(ResourceLocation path) {
		init(path);
	}

	private void init(ResourceLocation path) {
		try {
			ITextureObject resource = new SimpleTexture(path);
			resource.loadTexture(Minecraft.getMinecraft().getResourceManager());
			texId = resource.getGlTextureId();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	public void bind() {
		if (texId == -1)
			return;
		GlStateManager.bindTexture(texId);
	}

}
