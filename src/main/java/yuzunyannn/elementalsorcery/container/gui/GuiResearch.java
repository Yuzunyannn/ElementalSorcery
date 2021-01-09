package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ContainerResearch;
import yuzunyannn.elementalsorcery.elf.research.Topic;
import yuzunyannn.elementalsorcery.elf.research.Topics;
import yuzunyannn.elementalsorcery.util.ColorHelper;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

@SideOnly(Side.CLIENT)
public class GuiResearch extends GuiContainer {

	public static final ResourceLocation BUTTON = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/researcher/button.png");
	public static final ResourceLocation TEXTURE_01 = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/researcher/researcher_01.png");

	public final ContainerResearch container;

	public GuiResearch(EntityPlayer player, BlockPos pos) {
		super(new ContainerResearch(player, pos));
		this.container = (ContainerResearch) inventorySlots;
		ySize = xSize = 240;
		Set<String> all = Topics.getDefaultTopics();
		for (String key : container.reasearher.keySet()) {
			this.addTopics(key);
			all.remove(key);
		}
		// 必须有5个，玩家携带不足的话，强行补
		for (String key : all) {
			if (topics.size() >= 5) break;
			this.addTopics(key);
		}
	}

	public float topicSize = ySize * 0.3f;
	public float partialRadius = ySize * 0.6f;
	public float mapRadius = partialRadius - topicSize / 2 - 10;
	public float mapAlpha = 0;
	public float mapPrevAlpha = 0;
	private ArrayList<Float> mapVertex = new ArrayList<>(); // 中间星图的每个顶点的动画数据
	private ArrayList<Float> mapPrevVertex = new ArrayList<>();
	public float fogAlpha = 0;
	public float fogPrevAlpha = 0;
	public float fogRotate = 0;
	public float fogPrevRotate = 0;
	public boolean isSelectedCenter = false;
	public float mapR = 1, mapG = 1, mapB = 1;
	public int mapStrColor = 0xffffff;

	public float allPower = 0;

	// 每一种类型的信息
	public class TopicInfo {

		public Topic topic;
		public int power = 0;

		public boolean isSelected;
		public int startTime = 0;
		public float a = 0;
		public float prevA = a;
		public float scale = 0;
		public float prevScale = 0;

		public int maxPower = 0;

		public int getMaxPower() {
			if (container.fromSrverUpdateFlag) maxPower = container.reasearher.get(topic.getTypeName());
			return maxPower;
		}

		public void update() {
			if (startTime > 0) {
				startTime--;
				return;
			}
			prevA = a;
			prevScale = scale;
			if (isSelected) a = Math.min(a + 0.1f, 1f);
			else a += (0.3 - a) * 0.3f;
			scale += (1 - scale) * 0.3f;
			topic.update(isSelected);
		}

		public void draw(float partialTicks) {
			float scale = RenderHelper.getPartialTicks(this.scale, this.prevScale, partialTicks);
			float a = RenderHelper.getPartialTicks(this.a, this.prevA, partialTicks);
			float size = topicSize * scale;
			topic.render(mc, size, a, partialTicks);
		}
	}

	public int sendCD = 0;

	public void conductResearch() {
		if (sendCD > 0) return;
		NBTTagCompound nbt = new NBTTagCompound();
		for (TopicInfo info : topics) {
			if (info.power > 0) nbt.setInteger(info.topic.getTypeName(), info.power);
		}
		sendCD = 20;
		container.sendToServer(nbt);
	}

	private List<TopicInfo> topics = new ArrayList<>();

	public void addTopics(String type) {
		TopicInfo info = new TopicInfo();
		info.startTime = 5 + RandomHelper.rand.nextInt(10);
		info.topic = Topic.create(type);
		topics.add(info);
		mapVertex.add(0f);
		mapPrevVertex.add(0f);

		int len = topics.size();
		if (len > 8) topicSize = (float) (ySize * Math.pow(0.925, len - 8)) * 0.25f;
	}

	public List<TopicInfo> getTopics() {
		return topics;
	}

	private int selected = -1;

	public void select(int n) {
		if (selected >= 0) topics.get(selected).isSelected = false;
		if (n >= 0 && n < topics.size()) {
			topics.get(n).isSelected = true;
			selected = n;
		} else selected = -1;
	}

	public void addPower(int n, int power) {
		if (n < 0 || n >= topics.size()) return;
		TopicInfo info = topics.get(n);
		int size = topics.size();
		int lastPower = info.power;
		info.power += power;
		if (container.player.isCreative()) info.power = Math.max(0, info.power);
		else info.power = MathHelper.clamp(info.power, 0, info.getMaxPower());
		allPower += (info.power - lastPower) / (float) size;
		mapAlpha = 1;
		// 重新计算颜色
		if (allPower <= 0.1f) {
			mapR = mapG = mapB = 1;
			return;
		}
		mapR = mapG = mapB = 0;
		for (int i = 0; i < topics.size(); i++) {
			info = topics.get(i);
			Vec3d color = ColorHelper.color(info.topic.getColor());
			float rate = info.power / (allPower * size);
			mapR += color.x * rate;
			mapG += color.y * rate;
			mapB += color.z * rate;
		}
		mapR = Math.min(mapR, 1);
		mapG = Math.min(mapG, 1);
		mapB = Math.min(mapB, 1);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.width / 2.0f, height / 2, 0);
		this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		GlStateManager.enableAlpha();
		GlStateManager.popMatrix();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// 中心按钮
		float fogRotate = RenderHelper.getPartialTicks(this.fogRotate, this.fogPrevRotate, partialTicks);
		float fogAlpha = RenderHelper.getPartialTicks(this.fogAlpha, this.fogPrevAlpha, partialTicks);
		GlStateManager.color(mapR, mapG, mapB, fogAlpha);
		mc.getTextureManager().bindTexture(GuiMantraShitf.FOG);
		GlStateManager.pushMatrix();
		GlStateManager.rotate(fogRotate, 0, 0, 1);
		RenderHelper.drawTexturedRectInCenter(0, 2, 60, 60);
		GlStateManager.popMatrix();
		mc.getTextureManager().bindTexture(BUTTON);
		RenderHelper.drawTexturedRectInCenter(0, 0, 40, 40);

		final float mapRadius = this.mapRadius - 40;
		int size = topics.size();
		float dR = 360f / size;
		for (int n = 0; n < size; n++) {
			GlStateManager.pushMatrix();
			float roate = n * dR;
			GlStateManager.rotate(roate, 0, 0, 1);
			TopicInfo topic = topics.get(n);
			float r2 = RenderHelper.getPartialTicks(mapVertex.get(n % size), mapPrevVertex.get(n % size), partialTicks);
			float l2 = r2 * mapRadius + 40;
			String numStr = Integer.toString(topic.power) + "/" + topic.getMaxPower();
			this.fontRenderer.drawString(numStr, -fontRenderer.getStringWidth(numStr) / 2, -(int) l2 - 10, mapStrColor);
			GlStateManager.translate(0, -partialRadius, 0);
			topic.draw(partialTicks);
			GlStateManager.popMatrix();
		}
		// 画线
		GlStateManager.color(1, 1, 1, 1);
		float mapA = RenderHelper.getPartialTicks(mapAlpha, mapPrevAlpha, partialTicks);
		dR = dR / 180.0f * 3.1415926f;
		GlStateManager.disableTexture2D();
		for (int n = 1; n <= size; n++) {
			float roate = n * dR;
			float r1 = RenderHelper.getPartialTicks(mapVertex.get(n - 1), mapPrevVertex.get(n - 1), partialTicks);
			float l1 = r1 * mapRadius + 40;
			float x1 = MathHelper.sin(roate - dR) * l1;
			float y1 = -MathHelper.cos(roate - dR) * l1;
			float r2 = RenderHelper.getPartialTicks(mapVertex.get(n % size), mapPrevVertex.get(n % size), partialTicks);
			float l2 = r2 * mapRadius + 40;
			float x2 = MathHelper.sin(roate) * l2;
			float y2 = -MathHelper.cos(roate) * l2;
			drawLine(x1, y1, x2, y2, mapR, mapG, mapB, mapA);
		}
		GlStateManager.enableTexture2D();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		sendCD = Math.max(sendCD - 1, 0);
		float allPower = Math.max(this.allPower, 1);
		int size = topics.size();
		for (int i = 0; i < size; i++) {
			TopicInfo info = topics.get(i);
			info.update();
			if (container.fromSrverUpdateFlag) info.getMaxPower();
			float at = mapVertex.get(i);
			mapPrevVertex.set(i, at);
			float target = Math.min(1, info.power / allPower);
			mapVertex.set(i, at + (target - at) * 0.1f);
		}
		mapPrevAlpha = mapAlpha;
		fogPrevAlpha = fogAlpha;
		fogPrevRotate = fogRotate;
		mapAlpha += (0.3f - mapAlpha) * 0.1f;

		if (isSelectedCenter) fogAlpha += (1f - fogAlpha) * 0.15f;
		else fogAlpha += (0.4f - fogAlpha) * 0.15f;
		fogRotate += 0.1f;

		container.fromSrverUpdateFlag = false;
	}

	public int getTopicsWithPostion(int mouseX, int mouseY) {
		if (topics.isEmpty()) return -1;
		int cX = this.width / 2;
		int cY = this.height / 2;
		Vec3d tar = new Vec3d(mouseX - cX, mouseY - cY, 0);
		double length = tar.lengthVector();
		if (length < partialRadius - topicSize / 2) return -1;
		if (length > partialRadius + topicSize / 2) return -1;
		double cos = -tar.y / length;
		double angle = Math.acos(cos) / Math.PI * 180;
		if (tar.x < 0) angle = 360 - angle;
		float deta = 360f / topics.size();
		for (int i = 0; i < topics.size(); i++) {
			float c = i * deta - deta / 2;
			boolean flag;
			if (c < 0) flag = (c + 360) <= angle || c + deta > angle;
			else flag = c <= angle && c + deta > angle;
			if (flag) return i;
		}
		return -1;
	}

	public boolean isInCenterButton(int mouseX, int mouseY) {
		int cX = this.width / 2;
		int cY = this.height / 2;
		Vec3d tar = new Vec3d(mouseX - cX, mouseY - cY, 0);
		return tar.lengthSquared() <= 15 * 15;
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		this.isSelectedCenter = this.isInCenterButton(x, y);
		int i = getTopicsWithPostion(x, y);
		select(i);
		if (i >= 0) {
			int dw = Mouse.getDWheel();
			if (dw == 0) return;
			boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
			addPower(i, dw / 120 * (shift ? 10 : 1));
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		if (isInCenterButton(mouseX, mouseY)) {
			if (mouseButton == 0) this.conductResearch();
			return;
		}
		int i = getTopicsWithPostion(mouseX, mouseY);
		if (i < 0) return;
		if (mouseButton == 1) {
			if (shift) addPower(i, -100);
			else addPower(i, -1);
		} else {
			if (shift) addPower(i, 100);
			else addPower(i, 1);
		}

	}

	protected void drawLine(float x1, float y1, float x2, float y2, float r, float g, float b, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		for (int i = 9; i > 0; i -= 2) {
			float a = alpha * (1 - (i - 1) / 9.0f);
			GlStateManager.glLineWidth(i);
			bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(x1, y1, 0).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x2, y2, 0).color(r, g, b, a).endVertex();
			tessellator.draw();
		}
	}

}
