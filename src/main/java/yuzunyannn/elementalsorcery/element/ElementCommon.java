package yuzunyannn.elementalsorcery.element;

import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;

//简单的icon返回的元素
public abstract class ElementCommon extends Element {

	ResourceLocation TEXTURE;

	public ElementCommon(int color, String resName) {
		super(color);
		TEXTURE = new ResourceLocation(ElementalSorcery.MODID, "textures/elements/" + resName + ".png");
		this.setUnlocalizedName(resName);
	}

	@Override
	public ResourceLocation getIconResourceLocation() {
		return TEXTURE;
	}

}
