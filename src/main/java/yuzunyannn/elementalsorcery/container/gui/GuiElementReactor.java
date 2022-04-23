package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.container.ContainerElementReactor;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.element.ElementTransition;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectFragment;
import yuzunyannn.elementalsorcery.render.effect.gui.GUIEffectBatch;
import yuzunyannn.elementalsorcery.render.effect.gui.GUIEffectBatchList;
import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor;
import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor.ReactorStatus;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.element.ElementTransitionReactor;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class GuiElementReactor extends GuiScreen {

	static public Element[] involvedElementForDraw;
	static public float maxLevel;

	static public Element[] getInvolvedElements() {
		if (involvedElementForDraw != null) return involvedElementForDraw;
		Element[] elements = ElementTransitionReactor.getInvolvedElements();
		involvedElementForDraw = new Element[elements.length];
		for (int i = 0; i < elements.length; i++) {
			involvedElementForDraw[i] = elements[i];
			float level = elements[i].getTransition().getLevel();
			if (level > maxLevel) maxLevel = level;
		}
		Arrays.sort(involvedElementForDraw, (a, b) -> {
			float lA = a.getTransition().getLevel();
			float lB = b.getTransition().getLevel();
			return lA > lB ? -1 : (lA == lB ? 0 : 1);
		});
		return involvedElementForDraw;
	}

	public static final TextureBinder RING = new TextureBinder("textures/gui/reactor/ring.png");
	public static final TextureBinder HALO = new TextureBinder("textures/gui/reactor/halo.png");
	public static final TextureBinder COMS = new TextureBinder("textures/gui/reactor/coms.png");
	public static final TextureBinder ARC = new TextureBinder("textures/gui/reactor/arc_line.png");

	public static final Color[] fcolors = new Color[] { new Color(0xc2518b), new Color(0x5e4daa), new Color(0xf47dba),
			new Color(0x964ec2) };

	public final ContainerElementReactor container;
	public final TileElementReactor reactor;
	// 反应堆的状态
	public TileElementReactor.ReactorStatus reactorStatus;

	// 当前状态机
	protected ActionState currState;
	protected final List<ActionState> overGroups = new LinkedList<>();

	public abstract class ActionState {
		public abstract void update();

		public abstract ActionState getNextState();

		public boolean updateOver() {
			return false;
		}

		public void render(float partialTicks) {
		}

		public void mouseMove(int mouseX, int mouseY) {
		}

		public void mouseClick(int mouseX, int mouseY) {
		}

		public void onStatusChange() {
		}
	}

	public GuiElementReactor(ContainerElementReactor container) {
		this.container = container;
		this.reactor = container.tileEntity;
		this.container.guiObject = this;
		this.reactorStatus = this.reactor.getStatus();
		this.currState = this.reactorStatus.isRunning ? new RunningState() : new WaitingStartState();
	}

	@Override
	public void initGui() {
		super.initGui();
		this.mc.player.openContainer = this.container;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		currState.update();
		ActionState nextState = this.currState.getNextState();
		if (nextState != null && currState != nextState) {
			overGroups.add(currState);
			currState = nextState;
		}

		Iterator<ActionState> iter = overGroups.iterator();
		if (iter.hasNext()) {
			ActionState group = iter.next();
			if (!group.updateOver()) iter.remove();
		}

		guiEffectFrontList.update();
		guiEffectBackList.update();

		if (this.reactorStatus != reactor.getStatus()) {
			this.reactorStatus = reactor.getStatus();
			currState.onStatusChange();
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		partialTicks = mc.getRenderPartialTicks();
		this.drawDefaultBackground();

		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();

		GlStateManager.translate(0, 0, 0.1f);
		guiEffectBackList.render(partialTicks);
		currState.render(partialTicks);
		for (ActionState group : overGroups) group.render(partialTicks);
		guiEffectFrontList.render(partialTicks);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		currState.mouseMove(i, j);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		currState.mouseClick(mouseX, mouseY);
	}

	// 画一个弧形
	static public void renderArc(float kernel, float region, float r, Color color) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
		float dn = Math.max(16, region / 10) * 1.5f;
		region = region / 180 * 3.1415926f;
		float angle = (180 - kernel) / 180 * 3.1415926f - region / 2;
		float da = region / dn;
		bufferbuilder.pos(0, 0, 0).tex(0.5, 1).endVertex();
		float a = angle;
		float eAngle = angle + region;
		while (true) {
			if (a > eAngle) a = eAngle;
			float xoff = MathHelper.sin(a) * r;
			float yoff = MathHelper.cos(a) * r;
			bufferbuilder.pos(xoff, yoff, 0).tex((a - angle) / region, 0).endVertex();
			if (a >= eAngle) break;
			a += da;
		}
		tessellator.draw();
	}

	protected class RunningState extends ActionState {

		public float startRatio, prevStartRatio;
		public float rotation, prevRotation;
		public float transitionRation, prevTransitionRation;

		public Color color;
		public Color colorLight;

		Color targetColor;
		float colorChangeRatio;

		int stageIndex = 0;

		public RunningState() {
			color = reactor.getRenderColor();
			colorLight = new Color(color).weight(new Color(0xffffff), 0.9f);
		}

		@Override
		public void update() {
			prevStartRatio = startRatio;
			if (startRatio < 1) startRatio = Math.min(1, startRatio + 0.05f);

			prevRotation = rotation;
			rotation += 0.25f;

			prevTransitionRation = transitionRation;
			if (stageIndex == 1) {
				transitionRation = Math.min(1, transitionRation + 0.1f);
				if (transitionRation == 1) {
					Element myElement = reactor.getReactorCore().getElement();
					Element[] elements = getInvolvedElements();
					Element element = elements[Effect.rand.nextInt(elements.length)];
					for (Element e : elements) if (e == myElement) {
						element = e;
						break;
					}
					spawnEffectOfTransition(element);
				}

			} else if (transitionRation > 0) transitionRation = Math.max(0, transitionRation - 0.1f);

			if (targetColor != null) {
				colorChangeRatio = Math.min(1, colorChangeRatio + 0.025f);
				color.weight(targetColor, colorChangeRatio);
				colorLight = new Color(color).weight(new Color(0xffffff), 0.9f);
				if (colorChangeRatio == 1) targetColor = null;
			} else {
				Color color = reactor.getRenderColor();
				if (!color.equals(this.color)) setTargetColor(color);
			}

			if (startRatio >= 1) {
				ElementTransitionReactor core = reactor.getReactorCore();
				Element element = core.getElement();
				if (element != ElementStack.EMPTY.getElement()) updateCircleEffect();
			}
		}

		public void updateCircleEffect() {
			Random rand = Effect.rand;
			int cX = width / 2, cY = height / 2;
			for (int i = 0; i < 4; i++) {
				Part p = new Part() {
					@Override
					public void update() {
						super.update();
						this.alpha = this.alpha * (1 - transitionRation * 0.85f);
					}
				};
				p.startLifeTime = p.lifeTime = 20 * 5;
				Color color = reactor.getRenderColor();
				p.setColor(color.r, color.g, color.b);
				float roation = -this.rotation + i * 90 + rand.nextFloat() * 30 - 15;
				float x = MathHelper.sin(roation / 180 * 3.14f) * 90;
				float y = MathHelper.cos(roation / 180 * 3.14f) * 90;
				p.setPosition(cX + x, cY + y);
				p.vx = (float) -x * 0.01f;
				p.vy = (float) -y * 0.01f;
				guiEffectBackList.add(p);
			}
		}

		public void setTargetColor(Color color) {
			this.targetColor = color;
			this.colorChangeRatio = 0;
		}

		@Override
		public ActionState getNextState() {
			return null;
		}

		@Override
		public void mouseMove(int mouseX, int mouseY) {
			if (startRatio < 1) return;
			int cX = width / 2, cY = height / 2;
			float centerSize = 24;
			stageIndex = 0;
			if (GuiNormal.isMouseIn(mouseX, mouseY, cX - centerSize / 2, cY - centerSize / 2, centerSize, centerSize)) {
				stageIndex = 1;
				return;
			}
		}

		@Override
		public void render(float partialTicks) {
			GlStateManager.pushMatrix();
			int cX = width / 2, cY = height / 2;
			GlStateManager.translate(cX, cY, 0);

			float s = RenderHelper.getPartialTicks(startRatio, prevStartRatio, partialTicks);
			float r = RenderHelper.getPartialTicks(rotation, prevRotation, partialTicks);
			float t = RenderHelper.getPartialTicks(transitionRation, prevTransitionRation, partialTicks);

			float rScale = 1;
			float aH = Math.min(MathHelper.sin(r / 180 * 60) * 0.25f + 1, 1), aR = 1;
			if (s < 1) {
				s = s < 0.5f ? 4 * s * s * s : 1 - (float) Math.pow(-2 * s + 2, 3) / 2;
				if (s < 0.5) {
					aH = 0;
					aR = s * 2;
					float scale = 1 - s * 2;
					rScale = 1 / (1 + scale * 2);
					GlStateManager.scale(1 + scale * 2, 1 + scale * 2, 1 + scale * 2);
				} else {
					aH = (s - 0.5f) * 2;
				}
			}
			if (t > 0) {
				aH = aH * (1 - t * 0.85f);
				aR = aR * (1 - t * 0.85f);
			}

			GlStateManager.rotate(r, 0, 0, 1);

			GlStateManager.color(colorLight.r, colorLight.g, colorLight.b, aH);
			HALO.bind();
			RenderHelper.drawTexturedRectInCenter(0, 0, 256, 256, 0, 0, 256, 256, 256, 256);

			GlStateManager.color(color.r, color.g, color.b, aR);
			RING.bind();
			RenderHelper.drawTexturedRectInCenter(0, 0, 256, 256, 0, 0, 256, 256, 256, 256);

			if (rScale != 1) GlStateManager.scale(rScale, rScale, rScale);
			if (t > 0) renderTransition(t);

			GlStateManager.rotate(-r * 2, 0, 0, 1);

			GlStateManager.color(color.r, color.g, color.b, s);
			mc.getTextureManager().bindTexture(GuiMantraShitf.FOG);
			RenderHelper.drawTexturedRectInCenter(0, 0, 80, 80);

			GlStateManager.rotate(r, 0, 0, 1);

			if (s < 1) {
				if (s < 0.75) GlStateManager.scale(0, 0, 0);
				else {
					float scale = (s - 0.75f) / 0.25f;
					GlStateManager.scale(scale, scale, scale);
				}
			}
			// 图标
			GlStateManager.scale(1.4, 1.4, 1.4);
			ElementTransitionReactor core = reactor.getReactorCore();
			Element element = core.getElement();
			element.drawElemntIconInGUI(new ElementStack(element), 0, 0,
					Element.DRAW_GUI_FLAG_CENTER | Element.DRAW_GUI_FLAG_NO_INFO);
			GlStateManager.scale(1 / 1.4, 1 / 1.4, 1 / 1.4);

			// 数字颜色
			int iColor = color.toInt() | (int) (aR * 255) << 24;
			// 片元个数
			{
				String fragment = TextHelper.toAbbreviatedNumber(core.getFragment());
				int w = fontRenderer.getStringWidth(fragment);
				fontRenderer.drawString(fragment, -w / 2, 24, iColor);
			}
			// 一条线
			int lenWidth = 42;
			GlStateManager.disableTexture2D();
			GL11.glLineWidth(2);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(-lenWidth, 32, 0).color(color.r, color.g, color.b, aR).endVertex();
			bufferbuilder.pos(lenWidth, 32, 0).color(color.r, color.g, color.b, aR).endVertex();
			tessellator.draw();
			GlStateManager.enableTexture2D();
			// 率
			{
				int offset = -9;
				float ir = (float) (reactor.getInstableRatio() * 100);
				String text = ir > 0.1f ? String.format("|| %.1f%% ||", ir) : "|| <0.1% ||";
				int w1 = fontRenderer.getStringWidth(text);
				fontRenderer.drawString(text, -w1 + offset, 31, iColor);
				float ifr = (float) (reactor.getInstableFragment() / reactor.getInstableFragmentCapacity() * 100);
				text = String.format(" %s(%.1f%%)", TextHelper.toAbbreviatedNumber(reactor.getInstableFragment(), 1),
						ifr);
				fontRenderer.drawString(text, offset, 31, iColor);
			}
			// 能量线
			{
				String text = String.format("| %d |", reactor.getPowerLine());
				int w = fontRenderer.getStringWidth(text);
				fontRenderer.drawString(text, -w / 2, -40, iColor);
			}

			GlStateManager.popMatrix();
		}

		// 展示一些跃迁界面的粒子
		public void spawnEffectOfTransition(Element element) {
			Random rand = Effect.rand;
			float maxRange = Math.min(height, width) / 2;
			float levelToRange = maxRange / maxLevel;
			ElementTransition et = element.getTransition();
			float len = et.getLevel() * levelToRange;
			int cX = width / 2, cY = height / 2;
			float ka = et.getKernelAngle() - et.getRegionAngle() / 2 + rand.nextFloat() * et.getRegionAngle();
			float angle = rotation / 180 * 3.14f + (ka - 90) / 180 * 3.14f;

			Part p = new Part();
			p.drawSize = 1 + rand.nextFloat() * 2;
			Color color = new Color(element.getColor(new ElementStack(element)));
			p.setColor(color.r, color.g, color.b);
			Vec3d at = new Vec3d(MathHelper.cos(angle) * len, MathHelper.sin(angle) * len, 0);
			p.setPosition(cX + (float) at.x, cY + (float) at.y);
			Vec3d speed = at.normalize().scale(0.5);
			p.vx = (float) -speed.x;
			p.vy = (float) -speed.y;
			guiEffectFrontList.add(p);
		}

		// 画转化图
		public void renderTransition(float t) {
			ElementTransitionReactor met = reactor.getReactorCore();

			float maxRange = Math.min(height, width) / 2;
			float levelToRange = maxRange / maxLevel;
			ARC.bind();
			Element[] elements = getInvolvedElements();
			for (Element element : elements) {
				ElementTransition et = element.getTransition();
				float ct = (float) Math.pow(t, et.getLevel());
				Color color = new Color(element.getColor(new ElementStack(element)));
				GlStateManager.color(color.r, color.g, color.b, ct);
				float scale = t < 1 ? (1 - ct) * 2 + 1 : 1;
				float rScale = 1 / scale;
				GlStateManager.scale(scale, scale, scale);
				renderArc(et.getKernelAngle(), et.getRegionAngle(), et.getLevel() * levelToRange, color);
				GlStateManager.scale(rScale, rScale, rScale);
			}
			GlStateManager.color(color.r, color.g, color.b, t);
			renderArc(0, 360, levelToRange, color);

			COMS.bind();
			GlStateManager.color(Math.min(1, color.r + 0.2f), Math.min(1, color.g + 0.2f), Math.min(1, color.b + 0.2f),
					t);
			float angle = (met.getAngle() - 90) / 180 * 3.1415926f;
			float step = met.getStep() - 0.05f;
			float xoff = MathHelper.cos(angle) * step * levelToRange;
			float yoff = MathHelper.sin(angle) * step * levelToRange;
			RenderHelper.drawTexturedRectInCenter(xoff, yoff, 6, 6, 38, 0, 6, 6, 256, 256);
		}

	}

	protected class WaitingStartState extends ActionState {

		float startRatio, prevStartRatio;
		float rotation, prevRotation;
		float hoverRatio, prevHoverRatio;
		float endRation, prevEndRation;
		boolean mouseHover;
		boolean canNextState;
		int launchCD;

		@Override
		public void mouseMove(int mouseX, int mouseY) {
			mouseHover = isMouseInCenter(mouseX, mouseY);
		}

		@Override
		public void mouseClick(int mouseX, int mouseY) {
			if (!mouseHover) return;
			if (startRatio < 1) return;
			if (launchCD > 0) return;
			// 发送数据，告知服务端要移动了
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("L", true);
			container.sendToServer(nbt);
		}

		public boolean isMouseInCenter(int mouseX, int mouseY) {
			int cX = width / 2, cY = height / 2;
			return mouseX > cX - 65 && mouseX < cX + 65 && mouseY > cY - 65 && mouseY < cY + 65;
		}

		@Override
		public void update() {
			prevStartRatio = startRatio;
			if (startRatio < 1) startRatio = Math.min(1, startRatio + 0.075f);

			if (startRatio >= 1) {
				prevHoverRatio = hoverRatio;
				if (mouseHover) hoverRatio = Math.min(1, hoverRatio + 0.05f);
				else hoverRatio = Math.max(0, hoverRatio - 0.05f);
			}

			prevRotation = rotation;
			rotation += 0.5f + 8f * hoverRatio;

			if (Effect.rand.nextFloat() < hoverRatio + 0.1f) spawnEffect();

			if (launchCD > 0) launchCD--;
		}

		@Override
		public boolean updateOver() {
			prevRotation = rotation;
			prevEndRation = endRation;
			if (endRation < 1) endRation = Math.min(1, endRation + 0.1f);
			else return false;
			spawnEffect();
			return true;
		}

		public void spawnEffect() {
			Random rand = Effect.rand;
			int len = Math.max(width, height);
			int cX = width / 2, cY = height / 2;
			for (int i = 0; i < fcolors.length; i++) {
				float r = rotation / 180 * 3.14f + i * 6.28f / fcolors.length;
				Part p = new Part();
				Color color = fcolors[i];
				p.setColor(color.r, color.g, color.b);
				p.vx = MathHelper.cos(r) * 1.5f * hoverRatio + (1 - hoverRatio) * (float) rand.nextGaussian();
				p.vy = MathHelper.sin(r) * 1.5f * hoverRatio + (1 - hoverRatio) * (float) rand.nextGaussian();
				float dx = MathHelper.cos(r) * (40 + endRation * len) + (float) rand.nextGaussian() * 4;
				float dy = MathHelper.sin(r) * (40 + endRation * len) + (float) rand.nextGaussian() * 4;
				p.setPosition(cX + dx, cY + dy);
				guiEffectFrontList.add(p);
			}
		}

		@Override
		public ActionState getNextState() {
			return canNextState ? new RunningState() : null;
		}

		@Override
		public void render(float partialTicks) {
			COMS.bind();
			int cX = width / 2, cY = height / 2;
			GlStateManager.pushMatrix();
			GlStateManager.translate(cX, cY, 0);

			float r = RenderHelper.getPartialTicks(rotation, prevRotation, partialTicks);
			float s = RenderHelper.getPartialTicks(startRatio, prevStartRatio, partialTicks);
			float h = RenderHelper.getPartialTicks(hoverRatio, prevHoverRatio, partialTicks);
			float e = RenderHelper.getPartialTicks(endRation, prevEndRation, partialTicks);
			GlStateManager.rotate(r, 0, 0, 1);
			float a = 1;

			if (h > 0 && h < 1) h = h < 0.5f ? 4 * h * h * h : 1 - (float) Math.pow(-2 * h + 2, 3) / 2;
			if (s < 1) {
				s = 1 - (float) Math.pow(1 - s, 3);
				a = MathHelper.sqrt(s);
			}
			for (int i = 0; i < 4; i++) {
				Color color = fcolors[i];
				GlStateManager.color(color.r, color.g, color.b, a);
				GlStateManager.rotate(90 * s, 0, 0, 1);
				float offset = MathHelper.sin(r / 180 * 5) * 2 + e * Math.max(width, height);
				GlStateManager.translate(-19, -54 - offset, 0);
				RenderHelper.drawTexturedModalRect(0, 0, 0, 0, 38, 54, 256, 256);
				GlStateManager.translate(19, 54 + offset, 0);
			}

			GlStateManager.popMatrix();
		}

		@Override
		public void onStatusChange() {
			if (reactorStatus == ReactorStatus.RUNNING) canNextState = true;
		}
	}

	protected GUIEffectBatchList<Part> guiEffectFrontList = new GUIEffectBatchList<>();
	protected GUIEffectBatchList<Part> guiEffectBackList = new GUIEffectBatchList<>();

	public static class Part extends GUIEffectBatch {
		public float vx, vy;
		public int startLifeTime = 40;

		public Part() {
			this.prevAlpha = this.alpha = 0;
			drawSize = 3f;
			this.lifeTime = this.startLifeTime = 20 + Effect.rand.nextInt(10);
		}

		public void setColor(float r, float g, float b) {
			this.color.setColor(r, g, b).add(Effect.rand.nextFloat() * 0.25f);
		}

		public void update() {
			super.update();
			this.lifeTime--;
			x += vx;
			y += vy;
			alpha = scale = this.lifeTime / (float) this.startLifeTime;
		}

		@Override
		public void bindTexture() {
			EffectFragment.BATCH_TYPE.bind();
		}
	}
}
