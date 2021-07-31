package yuzunyannn.elementalsorcery.ts;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.ITickableTextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureManagerPacker extends TextureManager {

	final public TextureManager parent;

	public TextureManagerPacker(TextureManager other) {
		super(null);
		parent = other;
	}

	@Override
	public void bindTexture(ResourceLocation resource) {
		parent.bindTexture(resource);
	}

	@Override
	public boolean loadTickableTexture(ResourceLocation textureLocation, ITickableTextureObject textureObj) {
		return parent.loadTickableTexture(textureLocation, textureObj);
	}

	@Override
	public boolean loadTexture(ResourceLocation textureLocation, ITextureObject textureObj) {
		return parent.loadTexture(textureLocation, textureObj);
	}

	@Override
	public ITextureObject getTexture(ResourceLocation textureLocation) {
		return parent.getTexture(textureLocation);
	}

	@Override
	public ResourceLocation getDynamicTextureLocation(String name, DynamicTexture texture) {
		return parent.getDynamicTextureLocation(name, texture);
	}

	@Override
	public void tick() {

	}

	@Override
	public void deleteTexture(ResourceLocation textureLocation) {
		parent.deleteTexture(textureLocation);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		parent.onResourceManagerReload(resourceManager);
	}

}
