package yuzunyannn.elementalsorcery.api.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;

@SideOnly(Side.CLIENT)
public class TextureBinder {

	static public final Minecraft mc = Minecraft.getMinecraft();

	public static void bindTexture(ResourceLocation texture) {
		mc.getTextureManager().bindTexture(texture);
	}

	protected final ResourceLocation resource;

	public TextureBinder(String path) {
		this(ESAPI.MODID, path);
	}

	public TextureBinder(String domain, String path) {
		this.resource = new ResourceLocation(domain, path);
	}

	public TextureBinder(ResourceLocation path) {
		this.resource = path;
	}

	public void bind() {
		mc.getTextureManager().bindTexture(resource);
	}

	public ResourceLocation getResource() {
		return resource;
	}

	public void bindAtive(int active) {
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + active);
		TextureBinder.mc.getTextureManager().bindTexture(resource);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public void unbindAtive(int active) {
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + active);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

}
