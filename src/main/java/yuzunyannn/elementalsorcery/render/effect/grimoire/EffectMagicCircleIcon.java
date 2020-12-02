package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

@SideOnly(Side.CLIENT)
public class EffectMagicCircleIcon extends EffectMagicCircle {

	public ResourceLocation icon;

	public EffectMagicCircleIcon(World world, Entity binder, ResourceLocation icon) {
		super(world, binder);
		this.icon = icon;
	}

	@Override
	protected void renderCenterIcon(float partialTicks) {
		if (icon == null) return;
		mc.getTextureManager().bindTexture(icon);
		RenderHelper.drawTexturedRectInCenter(0, 0, 12, 12);
	}
}
