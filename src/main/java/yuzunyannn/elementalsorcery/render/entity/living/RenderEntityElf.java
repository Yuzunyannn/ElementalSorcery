package yuzunyannn.elementalsorcery.render.entity.living;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.render.model.living.ModelElf;

@SideOnly(Side.CLIENT)
public class RenderEntityElf extends RenderLiving<EntityElf> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/entity/elf/normal.png");
	public static final ResourceLocation TEXTURE_MASTER = new ResourceLocation(ESAPI.MODID,
			"textures/entity/elf/master.png");
	public static final ResourceLocation TEXTURE_SCHOLAR = new ResourceLocation(ESAPI.MODID,
			"textures/entity/elf/scholar.png");
	public static final ResourceLocation TEXTURE_BERSERKER = new ResourceLocation(ESAPI.MODID,
			"textures/entity/elf/berserker.png");
	public static final ResourceLocation TEXTURE_WARRIOR = new ResourceLocation(ESAPI.MODID,
			"textures/entity/elf/warrior.png");
	public static final ResourceLocation TEXTURE_CRAZY = new ResourceLocation(ESAPI.MODID,
			"textures/entity/elf/crazy.png");
	public static final ResourceLocation TEXTURE_MERCHANT = new ResourceLocation(ESAPI.MODID,
			"textures/entity/elf/merchant.png");
	public static final ResourceLocation TEXTURE_BUILDER = new ResourceLocation(ESAPI.MODID,
			"textures/entity/elf/builder.png");
	public static final ResourceLocation TEXTURE_RECEPTIONIST = new ResourceLocation(ESAPI.MODID,
			"textures/entity/elf/receptionist.png");
	public static final ResourceLocation TEXTURE_IRONSMITH = new ResourceLocation(ESAPI.MODID,
			"textures/entity/elf/ironsmith.png");
	public static final ResourceLocation TEXTURE_POSTMAN = new ResourceLocation(ESAPI.MODID,
			"textures/entity/elf/postman.png"); 
	public static final ResourceLocation TEXTURE_RESEARCHER = new ResourceLocation(ESAPI.MODID,
			"textures/entity/elf/researcher.png");
	public static final ResourceLocation TEXTURE_DEBT_COLLECTOR = new ResourceLocation(ESAPI.MODID,
			"textures/entity/elf/debt_collector.png");
	public static final ResourceLocation TEXTURE_TEST = new ResourceLocation(ESAPI.MODID,
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
		super.doRender(entity, x, y - 0.08, z, entityYaw, partialTicks);
		entity.getProfession().render(entity, x, y, z, entityYaw, partialTicks);
	}

}
