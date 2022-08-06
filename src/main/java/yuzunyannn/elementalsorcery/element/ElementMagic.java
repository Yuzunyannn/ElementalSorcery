package yuzunyannn.elementalsorcery.element;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.IElementExplosion;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.item.tool.ItemMagicBlastWand;

/** 特殊的元素，作为魔力使用 */
public class ElementMagic extends Element implements IElementExplosion {

	public ElementMagic() {
		super(0x4d2175);
	}

	/** 默认的资源 */
	public final static ResourceLocation MAGIC_RESOURCELOCATION = new ResourceLocation(ESAPI.MODID,
			"textures/elements/magic.png");

	@Override
	public ResourceLocation getIconResourceLocation() {
		return MAGIC_RESOURCELOCATION;
	}

	@Override
	public ElementExplosion newExplosion(World world, Vec3d pos, ElementStack eStack, EntityLivingBase attacker) {
		if (world.isRemote) return ElementExplosion.SELF_DEAL;
		ItemMagicBlastWand.blast(eStack, world, pos, attacker, attacker);
		return ElementExplosion.SELF_DEAL;
	}
}
