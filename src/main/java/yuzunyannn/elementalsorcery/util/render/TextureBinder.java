package yuzunyannn.elementalsorcery.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;

public class TextureBinder {

	static public final Minecraft mc = Minecraft.getMinecraft();

	public TextureBinder(String path) {
		this(ElementalSorcery.MODID, path);
	}

	public TextureBinder(String domain, String path) {
		init(new ResourceLocation(domain, path));
	}

	public TextureBinder(ResourceLocation path) {
		init(path);
	}

	ResourceLocation resource;

	private void init(ResourceLocation path) {
		this.resource = path;
	}

	public void bind() {
		mc.getTextureManager().bindTexture(resource);
	}

//	private int texId = -1;
//
//	private void init(ResourceLocation path) {
//		try {
//			ITextureObject resource = new SimpleTexture(path);
//			resource.loadTexture(Minecraft.getMinecraft().getResourceManager());
//			texId = resource.getGlTextureId();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return;
//		}
//	}
//
//	public void bind() {
//		if (texId == -1) return;
//		GlStateManager.bindTexture(texId);
//	}

}
