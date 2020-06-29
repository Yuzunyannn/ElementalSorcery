package yuzunyannn.elementalsorcery.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.item.ItemSpellbook;
import yuzunyannn.elementalsorcery.render.model.ModelElf;

@SideOnly(Side.CLIENT)
public class RenderEntityElf extends RenderLiving<EntityElf> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf.png");

	public RenderEntityElf(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelElf(), 0.5f);
		this.addLayer(new LayerHeldItem(this));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityElf entity) {
		return TEXTURE;
	}

	@Override
	public void doRender(EntityElf entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		entity.getProfession().render(entity, x, y, z, entityYaw, partialTicks);
	}

}
