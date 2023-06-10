package yuzunyannn.elementalsorcery.render.entity.living;

import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.entity.mob.EntitySubjectZombie;
import yuzunyannn.elementalsorcery.render.model.living.ModelSubjectZombie;

@SideOnly(Side.CLIENT)
public class RenderEntitySubjectZombie extends RenderBiped<EntitySubjectZombie> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/entity/subject_zombie.png");

	public RenderEntitySubjectZombie(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelSubjectZombie(), 0.5F);
		LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
			protected void initArmor() {
				this.modelLeggings = new ModelZombie(0.5F, true);
				this.modelArmor = new ModelZombie(1.0F, true);
			}
		};
		this.addLayer(layerbipedarmor);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySubjectZombie entity) {
		return TEXTURE;
	}
}