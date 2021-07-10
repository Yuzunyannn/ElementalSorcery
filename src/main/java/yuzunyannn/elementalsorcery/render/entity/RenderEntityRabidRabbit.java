package yuzunyannn.elementalsorcery.render.entity;

import net.minecraft.client.model.ModelRabbit;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;

@SideOnly(Side.CLIENT)
public class RenderEntityRabidRabbit extends RenderLiving<EntityLiving> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/rabid_rabbit.png");

	public static final ModelRabbit MODEL = new ModelRabbit();

	public RenderEntityRabidRabbit(RenderManager rendermanagerIn) {
		super(rendermanagerIn, MODEL, 0.5f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLiving entity) {
		return TEXTURE;
	}

}
