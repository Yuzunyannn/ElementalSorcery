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
			"textures/entity/elf/normal.png");
	public static final ResourceLocation TEXTURE_MASTER = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf/master.png");
	public static final ResourceLocation TEXTURE_SCHOLAR = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf/scholar.png");
	public static final ResourceLocation TEXTURE_BERSERKER = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf/berserker.png");
	public static final ResourceLocation TEXTURE_WARRIOR = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf/warrior.png");
	public static final ResourceLocation TEXTURE_CRAZY = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf/crazy.png");
	public static final ResourceLocation TEXTURE_MERCHANT = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf/merchant.png");
	public static final ResourceLocation TEXTURE_BUILDER = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf/builder.png");
	public static final ResourceLocation TEXTURE_RECEPTIONIST = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf/receptionist.png");
	public static final ResourceLocation TEXTURE_TEST = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/elf/what.png");

	public static final ModelElf MODEL = new ModelElf();

	public RenderEntityElf(RenderManager rendermanagerIn) {
		super(rendermanagerIn, MODEL, 0.5f);
		this.addLayer(new LayerHeldItem(this));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityElf entity) {
		return entity.getProfession().getTexture(entity);
	}

	@Override
	public void doRender(EntityElf entity, double x, double y, double z, float entityYaw, float partialTicks) {
		mainModel = entity.getProfession().getModel(entity);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		entity.getProfession().render(entity, x, y, z, entityYaw, partialTicks);
	}

}
