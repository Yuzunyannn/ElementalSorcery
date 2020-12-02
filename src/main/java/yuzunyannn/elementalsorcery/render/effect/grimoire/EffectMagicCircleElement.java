package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;

@SideOnly(Side.CLIENT)
public class EffectMagicCircleElement extends EffectMagicCircle {

	public ElementStack element;

	public EffectMagicCircleElement(World world, Entity binder, Element element) {
		super(world, binder);
		this.element = new ElementStack(element);
		this.setColor(this.element.getElement().getColor(this.element));
	}

	@Override
	protected void renderCenterIcon(float partialTicks) {
		GlStateManager.color(1, 1, 1, alpha);
		element.getElement().drawElemntIcon(this.element, alpha);
	}
}
