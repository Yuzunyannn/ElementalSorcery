package yuzunyannn.elementalsorcery.api.element;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;

/** 特殊的元素，作为魔力使用 */
public class ElementMagic extends Element {

	public ElementMagic() {
		super(0x4d2175);
	}

	@Override
	public ElementStack changetoMagic(World world, ElementStack estack) {
		return estack;
	}

	/** 默认的资源 */
	public final static ResourceLocation MAGIC_RESOURCELOCATION = new ResourceLocation(ElementalSorcery.MODID,
			"textures/elements/magic.png");

	@Override
	public ResourceLocation getIconResourceLocation() {
		return MAGIC_RESOURCELOCATION;
	}
}
