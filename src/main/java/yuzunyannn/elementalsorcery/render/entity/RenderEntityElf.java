package yuzunyannn.elementalsorcery.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.render.model.ModelElf;

@SideOnly(Side.CLIENT)
public class RenderEntityElf extends RenderLiving<EntityElf> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf.png");
	public static final ResourceLocation TEXTURE_MASTER = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf_master.png");
	public static final ResourceLocation TEXTURE_SCHOLAR = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf_scholar.png");
	public static final ResourceLocation TEXTURE_BERSERKER = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf_berserker.png");
	public static final ResourceLocation TEXTURE_WARRIOR = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf_warrior.png");
	public static final ResourceLocation TEXTURE_CRAZY = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf_crazy.png");

	public RenderEntityElf(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelElf(), 0.5f);
		this.addLayer(new LayerHeldItem(this));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityElf entity) {
		return entity.getProfession().getTexture(entity);
	}

	@Override
	public void doRender(EntityElf entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		entity.getProfession().render(entity, x, y, z, entityYaw, partialTicks);
	}

}
