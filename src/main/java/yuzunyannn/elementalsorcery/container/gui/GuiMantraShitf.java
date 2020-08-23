package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.event.KeyBoard;

@SideOnly(Side.CLIENT)
public class GuiMantraShitf extends GuiScreen {

	public static final ResourceLocation RING = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/mantra_shitf/ring.png");
	public static final ResourceLocation SELECT = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/mantra_shitf/select.png");
	public static final ResourceLocation CIRCLE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/mantra_shitf/circle.png");
	public static final ResourceLocation FOG = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/mantra_shitf/fog.png");

	public GuiMantraShitf(EntityPlayer player) {
		// 测试
		addMantra();
		addMantra();
		addMantra();
		addMantra();
		addMantra();
		select(0);
	}

	/** 关闭gui同时将选择的新数据发送给服务器 */
	public void close() {
		this.mc.player.closeScreen();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void onResize(Minecraft mcIn, int w, int h) {
		super.onResize(mcIn, w, h);
	}

	public void setSize(float size) {
		this.size = size;
		int len = mantras.size();
		if (len > 8) mantraSize = (float) (size * Math.pow(0.925, len - 8));
	}

	public float size = 256;
	public float mantraSize = size * 0.25f;
	// 外环最开始的缩放
	public float ringScale = 2;
	public float prevRingScale = ringScale;
	// 外环的旋转
	public float ringRotate = 0;
	public float prevRingRotate = ringRotate;
	// 咒文图标开始半径
	public float radius = 0;
	public float prevRadius = radius;
	public float partialRadius;
	// 咒文图标开始旋转
	public float mantraRotate = -90;
	public float prevMantraRotate = mantraRotate;
	// 选择的位置
	public float selectRoate = 0;
	public float prevSelectRoate = selectRoate;

	/** 每一种咒文的绘图 */
	public class Mantra {

		public int n;
		public boolean isSelected;
		public float a = 1;
		public float prevA = a;
		public float fogScale = 1.7f;
		public float prevFogScale = fogScale;
		public float fogRoate = EventClient.rand.nextFloat() * 360;
		public float prevFogRoate = fogRoate;
		public float fogA = 0;
		public float prevFogA = fogA;

		public boolean scaleUp = true;

		public void update() {
			prevA = a;
			prevFogScale = fogScale;
			prevFogRoate = fogRoate;
			prevFogA = fogA;

			if (isSelected) {
				a = Math.min(1, a + 0.1f);
				fogA = Math.min(1, fogA + 0.1f);
			} else {
				a = Math.max(0.3f, a - 0.1f);
				fogA = Math.max(0.1f, fogA - 0.1f);
			}
			if (scaleUp) {
				fogScale += 0.001f;
				if (fogScale >= 1.85f) scaleUp = false;
			} else {
				fogScale -= 0.001f;
				if (fogScale <= 1.6f) scaleUp = true;
			}
			fogRoate += 0.2f;
		}

		public void draw(float partialTicks) {
			GlStateManager.pushMatrix();
			float roate = n / (float) mantras.size() * 360;
			float a = prevA + (this.a - prevA) * partialTicks;
			GlStateManager.color(202 / 255f, 197 / 255f, 224 / 255f, a);
			GlStateManager.rotate(roate, 0, 0, 1);
			GlStateManager.translate(0, -partialRadius, 0);

			GuiMantraShitf.this.setTexture(CIRCLE, 128, 128);
			GuiMantraShitf.this.draw(0, 0, mantraSize, mantraSize, 0, 0, 128, 128);

			roate = prevFogRoate + (fogRoate - prevFogRoate) * partialTicks;
			GlStateManager.rotate(roate, 0, 0, 1);
			float scale = prevFogScale + (fogScale - prevFogScale) * partialTicks;
			a = prevFogA + (fogA - prevFogA) * partialTicks;
			GlStateManager.color(202 / 255f, 197 / 255f, 224 / 255f, a);
			GuiMantraShitf.this.setTexture(FOG, 128, 128);
			GuiMantraShitf.this.draw(0, 0, mantraSize * scale, mantraSize * scale, 0, 0, 128, 128);

			GlStateManager.popMatrix();
		}
	}

	public ArrayList<Mantra> mantras = new ArrayList<>();

	public void addMantra() {
		Mantra m = new Mantra();
		m.n = mantras.size();
		mantras.add(m);
		this.setSize(size);
	}

	/** 更新绘制场景的数据 */
	@Override
	public void updateScreen() {
		prevRingScale = ringScale;
		prevRingRotate = ringRotate;
		prevRadius = radius;
		prevMantraRotate = mantraRotate;
		prevSelectRoate = selectRoate;

		ringRotate += 0.05f;

		final float rate = 0.35f;

		float dRingScale = 1 - ringScale;
		ringScale += dRingScale * rate;

		float dRadius = (size * 0.3f) - radius;
		radius += dRadius * rate;

		float dMantraRotate = -mantraRotate;
		mantraRotate += dMantraRotate * rate;

		for (Mantra mantra : mantras) mantra.update();

		if (selected >= 0) {
			float nd = 360f / mantras.size();
			float deta = nd * selected - 1;
			if (deta - selectRoate > 180) {
				if (selectRoate < 0) {
					selectRoate += 360;
					prevSelectRoate += 360;
				} else deta -= 360;
			} else if (selectRoate - deta > 180) {
				selectRoate -= 360;
				prevSelectRoate -= 360;
			}
			selectRoate += (deta - selectRoate) * rate;
		}
	}

	/** 直接处理鼠标消息，进行分析选择咒文 */
	@Override
	public void handleMouseInput() throws IOException {
		if (mantras.isEmpty()) return;
		int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		int cX = this.width / 2;
		int cY = this.height / 2;
		Vec3d tar = new Vec3d(mouseX - cX, mouseY - cY, 0);
		double cos = -tar.y / tar.lengthVector();
		double angle = Math.acos(cos) / Math.PI * 180;
		if (tar.x < 0) angle = 360 - angle;
		float deta = 360f / mantras.size();
		for (int i = 0; i < mantras.size(); i++) {
			float c = i * deta - deta / 2;
			boolean flag;
			if (c < 0) flag = (c + 360) <= angle || c + deta > angle;
			else flag = c <= angle && c + deta > angle;
			if (flag) {
				select(i);
				break;
			}
		}
	}

	/** 当前选择的内容 */
	private int selected = -1;

	public void select(int n) {
		if (selected >= 0) mantras.get(selected).isSelected = false;
		mantras.get(n).isSelected = true;
		selected = n;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		partialTicks = mc.getRenderPartialTicks();
		this.drawDefaultBackground();
		int cX = this.width / 2;
		int cY = this.height / 2;
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();

		GlStateManager.pushMatrix();
		float roate = prevRingRotate + (ringRotate - prevRingRotate) * partialTicks;
		float sacle = prevRingScale + (ringScale - prevRingScale) * partialTicks;
		float size = this.size * sacle;
		GlStateManager.translate(cX, cY, 0);
		GlStateManager.rotate(roate, 0, 0, 1);
		this.setTexture(RING, 256, 256);
		this.draw(0, 0, size, size, 0, 0, 256, 256);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(cX, cY, 0);
		roate = prevMantraRotate + (mantraRotate - prevMantraRotate) * partialTicks;
		partialRadius = prevRadius + (radius - prevRadius) * partialTicks;
		GlStateManager.rotate(roate, 0, 0, 1);
		// 画所有咒文图标
		for (Mantra mantra : mantras) mantra.draw(partialTicks);
		// 选择旋转
		GlStateManager.color(1, 1, 1);
		roate = prevSelectRoate + (selectRoate - prevSelectRoate) * partialTicks;
		GlStateManager.rotate(roate, 0, 0, 1);
		if (!mantras.isEmpty()) GlStateManager.translate(0, -partialRadius, 0);
		this.setTexture(SELECT, 256, 256);
		this.draw(0, 0, mantraSize * 1.1f, mantraSize * 1.1f, 0, 0, 256, 256);
		GlStateManager.popMatrix();

		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
	}

	/** 重写键盘处理，任何键放下都会结束这个 */
	@Override
	public void handleKeyboardInput() throws IOException {
		if (Keyboard.getEventKeyState() == false) {
			int key = Keyboard.getEventKey();
			if (key == KeyBoard.KEY_MANTRA_SHITF.getKeyCode() || key == Keyboard.KEY_ESCAPE) {
				this.close();
			}
		}
	}

	public float textureWidth = 256;
	public float textureHeight = 256;

	public void setTexture(ResourceLocation texture, float textureWidth, float textureHeight) {
		mc.getTextureManager().bindTexture(texture);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	public void draw(float x, float y, float width, float height, float u, float v, float texWidth, float texHeight) {
		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		float hw = width / 2;
		float hh = height / 2;
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x - hw, y + hh, 0.0D).tex((u * f), ((v + texHeight) * f1)).endVertex();
		bufferbuilder.pos(x + hw, y + hh, 0.0D).tex((u + texWidth) * f, (v + texHeight) * f1).endVertex();
		bufferbuilder.pos(x + hw, y - hh, 0.0D).tex((u + texWidth) * f, v * f1).endVertex();
		bufferbuilder.pos(x - hw, y - hh, 0.0D).tex(u * f, v * f1).endVertex();
		tessellator.draw();
	}

}
