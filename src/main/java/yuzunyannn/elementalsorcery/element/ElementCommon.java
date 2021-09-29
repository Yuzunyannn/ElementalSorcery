package yuzunyannn.elementalsorcery.element;

import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.element.IElementExplosion;
import yuzunyannn.elementalsorcery.api.element.IStarFlowerCast;

//简单的icon返回的元素
public abstract class ElementCommon extends Element implements IStarFlowerCast, IElementExplosion {

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

	protected int getStarFlowerRange(ElementStack estack) {
		return (int) Math.min(Math.ceil(16 * estack.getPower() / 1000f) + 4, 24);
	}

}
