package yuzunyannn.elementalsorcery.render.entity;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.gui.GuiQuest;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.entity.EntityBulletin;
import yuzunyannn.elementalsorcery.entity.EntityBulletin.QuestInfo;
import yuzunyannn.elementalsorcery.render.model.ModelBulletin;

@SideOnly(Side.CLIENT)
public class RenderEntitiyBulletin extends Render<EntityBulletin> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/bulletin.png");

	public static final ModelBulletin MODEL = new ModelBulletin();

	public RenderEntitiyBulletin(RenderManager renderManager) {
		super(renderManager);
	}

	public void doRender(EntityBulletin entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.enableRescaleNormal();
		this.bindEntityTexture(entity);
		GlStateManager.scale(0.0625F, 0.0625F, 0.0625F);
		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}
		this.renderBulletin(entity);
		this.renderQuests(entity);
		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	private void renderBulletin(EntityBulletin entity) {
		int width = entity.getWidthPixels();
		int height = entity.getHeightPixels();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -height + 8, -1.1);
		GlStateManager.scale(width / 30.0, height / 18.0, 1);
		MODEL.render(null, 0, 0, 0, 0, 0, 1);
		GlStateManager.popMatrix();
	}

	protected void renderQuests(EntityBulletin entity) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		GlStateManager.enableLighting();
		GlStateManager.translate(0, 0, -1.11);
		GlStateManager.scale(0.0625F, 0.0625F, 0.0625F);
		GlStateManager.rotate(180, 0, 0, 0);
		List<QuestInfo> quests = entity.getQuests();
		float width = entity.getWidthPixels() * 16 * 0.5f;
		float height = entity.getHeightPixels() * 16 * 0.5f;
		float around = EntityBulletin.MODEL_FRAME_SIZE * entity.getWidthPixels() / EntityBulletin.MODEL_WIDTH * 16;
		float rw = entity.getValueWidth();
		float rh = entity.getValueHeight();
		double zoff = 0.05 * quests.size();
		for (QuestInfo info : quests) {
			GlStateManager.pushMatrix();
			float x = info.getX() / rw * (width * 2 - around * 2);
			float y = info.getY() / rh * (height * 2 - around * 2);
			GlStateManager.translate(x + around - width, y - height + around, zoff);
			float s = EntityBulletin.MODEL_QUEST_SIZE / 207f * 16;
			GlStateManager.color(0.8f, 0.8f, 0.8f);
			GlStateManager.scale(s, s, s);
			Quest quest = info.getQuest();
			GuiQuest.drawQuest(quest, null, false);
			GlStateManager.popMatrix();
			zoff -= 0.05;
		}
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBulletin entity) {
		return TEXTURE;
	}

}
