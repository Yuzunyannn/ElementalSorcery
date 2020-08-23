package yuzunyannn.elementalsorcery.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;

@SideOnly(Side.CLIENT)
public class RenderEntityGrimoire extends Render<EntityGrimoire> {

	public RenderEntityGrimoire(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGrimoire entity) {
		return null;
	}

	@Override
	public void doRender(EntityGrimoire entity, double x, double y, double z, float entityYaw, float partialTicks) {

	}
}
