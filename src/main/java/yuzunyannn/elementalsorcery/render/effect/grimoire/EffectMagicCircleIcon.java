package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.IEffectBinder;

@SideOnly(Side.CLIENT)
public class EffectMagicCircleIcon extends EffectMagicCircle {

	public ResourceLocation icon;

	public EffectMagicCircleIcon(World world, IEffectBinder binder, ResourceLocation icon) {
		super(world, binder);
		this.icon = icon;
	}

	@Override
	protected void renderCenterIcon(float partialTicks, float alpha) {
		if (icon == null) return;
		mc.getTextureManager().bindTexture(icon);
		this.renderTexRectInCenter(0, 0, 12, 12, r, g, b, alpha);
	}
}
