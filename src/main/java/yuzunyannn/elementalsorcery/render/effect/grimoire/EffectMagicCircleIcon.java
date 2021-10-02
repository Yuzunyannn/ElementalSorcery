package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EffectMagicCircleIcon extends EffectMagicCircle {

	public ResourceLocation icon;

	public EffectMagicCircleIcon(World world, Entity binder, ResourceLocation icon) {
		super(world, binder);
		this.icon = icon;
	}

	public EffectMagicCircleIcon(World world, BlockPos pos, ResourceLocation icon) {
		super(world, pos);
		this.icon = icon;
	}

	@Override
	protected void renderCenterIcon(float partialTicks, float alpha) {
		if (icon == null) return;
		mc.getTextureManager().bindTexture(icon);
		this.renderTexRectInCenter(0, 0, 12, 12, r, g, b, alpha);
	}
}
