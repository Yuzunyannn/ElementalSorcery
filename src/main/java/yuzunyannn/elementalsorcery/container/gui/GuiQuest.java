package yuzunyannn.elementalsorcery.container.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.container.ContainerQuest;
import yuzunyannn.elementalsorcery.elf.ElfTime;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.QuestDescribe;
import yuzunyannn.elementalsorcery.elf.quest.QuestStatus;
import yuzunyannn.elementalsorcery.elf.quest.QuestType;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestCondition;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestReward;

@SideOnly(Side.CLIENT)
public class GuiQuest extends GuiContainer {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID, "textures/gui/elf/quest.png");

	protected final ContainerQuest container;
	protected boolean dynamic;

	public GuiQuest(EntityPlayer player) {
		super(new ContainerQuest(player));
		container = (ContainerQuest) inventorySlots;
		this.xSize = 207;
		this.ySize = 219;
		Quest quest = container.getQuest();
		if (quest == null) return;
		dynamic = quest.getStatus() == QuestStatus.UNDERWAY;
		dynamic = dynamic && quest.isAdventurer(container.player);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		Quest quest = container.getQuest();
		if (quest == null) return;
		GlStateManager.pushMatrix();
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		GlStateManager.translate(offsetX, offsetY, 0);
		drawQuest(quest, container.player, dynamic);
		GlStateManager.popMatrix();
	}

	public static void drawQuest(Quest quest, @Nullable EntityLivingBase player, boolean dynamic) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.getTextureManager().bindTexture(TEXTURE);
		FontRenderer fontRenderer = mc.fontRenderer;
		int color = 0x4b2811;
		final int width = 207;
		final int height = 219;
		RenderFriend.drawTextureModalRect(0, 0, 0, 0, width, height, 256, 256);
		QuestType type = quest.getType();
		QuestDescribe describe = type.getDescribe();
		ArrayList<QuestCondition> preConditions = type.getPreconditions();
		QuestStatus status = quest.getStatus();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 0.001);
		String title = I18n.format(describe.getTitle());
		fontRenderer.drawString(TextFormatting.BOLD + title, width / 2 - fontRenderer.getStringWidth(title) / 2, 6,
				color);
		boolean overdue = false;
		if (player != null) overdue = quest.isOverdue(player.world.getWorldTime());
		// 具体描述
		String des = describe.getMainDescribe(quest, player, dynamic && !overdue);
		int yoff = 17;
		for (String s : fontRenderer.listFormattedStringToWidth(des, width - 20)) {
			fontRenderer.drawString(s, 10, yoff, color);
			yoff += fontRenderer.FONT_HEIGHT;
		}
		// 接受条件
		fontRenderer.drawString(TextFormatting.BOLD + I18n.format("quest.pre.condition"), 5, 90, color);
		yoff = 101;
		if (quest.getEndTime() > 0) {
			String endTime = new ElfTime(quest.getEndTime()).getDate();
			if (overdue && status != QuestStatus.FINISH)
				fontRenderer.drawString(TextFormatting.DARK_RED + I18n.format("quest.end", endTime), 10, yoff, color);
			else fontRenderer.drawString(I18n.format("quest.end", endTime), 10, yoff, color);
			yoff += fontRenderer.FONT_HEIGHT;
		}
		for (QuestCondition con : preConditions) {
			String value = con.getDescribe(quest, player, player != null && status == QuestStatus.NONE);
			for (String s : fontRenderer.listFormattedStringToWidth(value, width - 20)) {
				fontRenderer.drawString(s, 10, yoff, color);
				yoff += fontRenderer.FONT_HEIGHT;
			}
		}
		if (yoff == 101) fontRenderer.drawString(I18n.format("info.none"), 10, yoff, color);
		// 奖励
		String reward = I18n.format("quest.reward");
		fontRenderer.drawString(TextFormatting.BOLD + reward, 5, 146, color);
		yoff = 157;
		List<QuestReward> rewards = type.getRewards();
		for (QuestReward rew : rewards) {
			String str = rew.getDescribe(quest, player);
			if (str.isEmpty()) continue;
			fontRenderer.drawString(str, 10, yoff, color);
			yoff += fontRenderer.FONT_HEIGHT;
		}
		// 请求的人
		String name = quest.getAdventurerName();
		if (name != null) {
			int w = fontRenderer.getStringWidth(name);
			fontRenderer.drawString(name, 200 - w, 202, color);
			fontRenderer.drawString("✏", 200 - w - 10, 203, color);
		}

		GlStateManager.popMatrix();
		// 完成标记
		if (status == QuestStatus.FINISH) {
			mc.getTextureManager().bindTexture(TEXTURE);
			GlStateManager.color(1, 1, 1);
			GlStateManager.pushMatrix();
			float x = width / 2 - 55;
			float y = height / 2 - 50;
			GlStateManager.translate(x, y, 10);
			GlStateManager.rotate(20, 0, 0, 1);
			GlStateManager.scale(3, 3, 3);
			RenderFriend.drawTextureModalRect(0, 0, 208, 0, 48, 20, 256, 256);
			GlStateManager.popMatrix();
		}
	}

}
