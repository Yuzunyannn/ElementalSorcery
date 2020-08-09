package yuzunyannn.elementalsorcery.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.EntityVest;

@SideOnly(Side.CLIENT)
public class RenderEntityVest extends Render<EntityVest> {

	public RenderEntityVest(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityVest entity) {
		return null;
	}

	@Override
	public void doRender(EntityVest entity, double x, double y, double z, float entityYaw, float partialTicks) {
	}
}
