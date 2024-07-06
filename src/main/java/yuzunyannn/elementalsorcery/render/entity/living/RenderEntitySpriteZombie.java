package yuzunyannn.elementalsorcery.render.entity.living;

import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.entity.mob.EntitySpriteZombie;

@SideOnly(Side.CLIENT)
public class RenderEntitySpriteZombie extends RenderBiped<EntitySpriteZombie> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/entity/spirit_zombie.png");

	public RenderEntitySpriteZombie(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelZombie(), 0.5F);
		LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
			protected void initArmor() {
				this.modelLeggings = new ModelZombie(0.5F, true);
				this.modelArmor = new ModelZombie(1.0F, true);
			}
		};
		this.addLayer(layerbipedarmor);
	}

	@Override
	public void doRender(EntitySpriteZombie entity, double x, double y, double z, float entityYaw, float partialTicks) {
		RenderFriend.disableLightmap(true);
		GlStateManager.colorMask(false, true, true, true);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		GlStateManager.colorMask(true, true, true, true);
		RenderFriend.disableLightmap(false);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpriteZombie entity) {
		return TEXTURE;
	}
}