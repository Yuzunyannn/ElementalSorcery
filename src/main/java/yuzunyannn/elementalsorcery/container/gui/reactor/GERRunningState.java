package yuzunyannn.elementalsorcery.container.gui.reactor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.ElementTransition;
import yuzunyannn.elementalsorcery.api.element.ElementTransitionReactor;
import yuzunyannn.elementalsorcery.api.mantra.IFragmentMantraLauncher;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.api.util.target.WorldLocation;
import yuzunyannn.elementalsorcery.container.gui.GuiMantraShitf;
import yuzunyannn.elementalsorcery.container.gui.GuiNormal;
import yuzunyannn.elementalsorcery.container.gui.reactor.GuiElementReactor.Part;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor.ReactorStatus;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class GERRunningState extends GERActionState {

	public static final int STAGE_NONE = 0;
	public static final int STAGE_ER = 1;
	public static final int STAGE_TOOL = 2;
	public static final int STAGE_MANTRA_SUB = 3;

	public int tick;
	public int mouseX, mouseY;
	public float startRatio, prevStartRatio;
	public float rotation, prevRotation;
	public float transitionRation, prevTransitionRation;
	public boolean canNextState;
	public int lastSendTick;

	public float prevToolRatio = 0, toolRatio = 0;
	public float stopRatio = 0;

	public float prevToReadyRatio, toReadyRatio = 0;

	public Color color;
	public Color colorLight;

	Color targetColor;
	float colorChangeRatio;

	protected int selectMantraIndex = -1;
	public float mantraSelectRatio, prevMantraSelectRatio;

	int stageIndex = STAGE_NONE;

	List<GuiLocationBar> bars = new ArrayList<>();
	List<Mantra> mantras;

	@Override
	public void init(GuiElementReactor guiReactor) {
		super.init(guiReactor);
		color = reactor.getRenderColor();
		colorLight = new Color(color).weight(new Color(0xffffff), 0.9f);

		List<WorldLocation> localList = gui.ncList;
		int i = MathHelper.floor(localList.size() / 2.0f);
		addLocationBar(localList, 0, i, true);
		addLocationBar(localList, i, localList.size(), false);

		if (mantras == null) mantras = reactor.checkAndGetMantras();
	}

	protected void addLocationBar(List<WorldLocation> list, int i, int j, boolean isLeft) {
		int size = j - i;
		final float dTheta = 50 / 180f * 3.1415926f;
		float theta = Math.min(dTheta * 2 / size, dTheta * 2 / 9);
		float offMove = size % 2 == 0 ? theta / 2 : 0;
		float offTheta = 3.1415926f / 2;
		if (isLeft) offTheta = -offTheta;
		for (int index = 0; index < size; index++) {
			int n = index % 2 == 0 ? -index / 2 : (index / 2 + 1);
			GuiLocationBar bar = new GuiLocationBar(gui);
			bar.fontRenderer = gui.getFontRender();
			bar.x = MathHelper.sin(theta * n + offTheta - offMove) * 150;
			bar.y = MathHelper.cos(theta * n + offTheta - offMove) * 150;
			bar.setLoaction(list.get(index + i));
			bars.add(bar);
		}
	}

	public List<Mantra> getMantraList() {
		return mantras;
	}

	public void trySendStop() {
		if (gui.reactorStatus != ReactorStatus.RUNNING) return;
		// 发送数据，告知服务端要停止了
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("S", true);
		gui.container.sendToServer(nbt);
	}

	@Override
	public void update() {
		tick++;
		if (lastSendTick > 0) lastSendTick--;

		prevStartRatio = startRatio;
		if (startRatio < 1) startRatio = Math.min(1, startRatio + 0.05f);

		prevRotation = rotation;
		rotation += 0.25f - 0.5f * stopRatio;
		if (reactor.getStatus() == ReactorStatus.RUNAWAY) rotation += RandomHelper.rand.nextGaussian() * 10;

		if (gui.reactorStatus != ReactorStatus.RUNNING) stageIndex = STAGE_NONE;
		if (gui.reactorStatus == ReactorStatus.CLOSING) stopRatio = Math.min(1, stopRatio + 0.1f);
		else if (isRunningMantra()) {
			stageIndex = STAGE_NONE;
			for (GuiLocationBar bar : bars) bar.update(mouseX, mouseY);
		}

		prevToolRatio = toolRatio;
		if (stageIndex == STAGE_TOOL) {
			toolRatio = Math.min(1, toolRatio + 0.1f);
			if (mantraSelectRatio == 0) selectMantraIndex = checkSelectMantra(mouseX, mouseY);
		} else toolRatio = Math.max(0, toolRatio - 0.1f);

		prevMantraSelectRatio = mantraSelectRatio;
		if (stageIndex == STAGE_MANTRA_SUB) mantraSelectRatio = Math.min(1, mantraSelectRatio + 0.1f);
		else {
			if (stageIndex == STAGE_TOOL) mantraSelectRatio = Math.max(0, mantraSelectRatio - 0.1f);
			else prevMantraSelectRatio = mantraSelectRatio = 0;
		}

		prevTransitionRation = transitionRation;
		if (stageIndex == STAGE_ER) {
			transitionRation = Math.min(1, transitionRation + 0.1f);
			if (transitionRation == 1) {
				Element myElement = reactor.getReactorCore().getElement();
				Element[] elements = GuiElementReactor.getInvolvedElements();
				Element element = elements[Effect.rand.nextInt(elements.length)];
				for (Element e : elements) if (e == myElement) {
					element = e;
					break;
				}
				spawnEffectOfTransition(element);
			}

		} else if (transitionRation > 0) transitionRation = Math.max(0, transitionRation - 0.1f);

		prevToReadyRatio = toReadyRatio;
		if (isMantraReady()) toReadyRatio = Math.min(1, toReadyRatio + 0.1f);
		else toReadyRatio = Math.max(0, toReadyRatio - 0.1f);

		if (targetColor != null) {
			colorChangeRatio = Math.min(1, colorChangeRatio + 0.025f);
			color.weight(targetColor, colorChangeRatio);
			colorLight = new Color(color).weight(new Color(0xffffff), 0.9f);
			if (colorChangeRatio == 1) targetColor = null;
		} else {
			if (isRunningMantra()) {
				Color color = new Color(reactor.getRunningMantraPair().mantra.getColor(null));
				if (!color.equals(this.color)) setTargetColor(color);
				else {
					color = reactor.getRenderColor();
					if (!color.equals(this.color)) setTargetColor(color);
				}
			} else {
				Color color = reactor.getRenderColor();
				if (!color.equals(this.color)) setTargetColor(color);
			}
		}

		if (startRatio >= 1) {
			ElementTransitionReactor core = reactor.getReactorCore();
			Element element = core.getElement();
			if (element != ElementStack.EMPTY.getElement()) updateCircleEffect();
		}

	}

	@Override
	public boolean updateOver() {
		prevStartRatio = startRatio;
		if (startRatio > 0) startRatio = Math.max(0, startRatio - 0.05f);
		else return false;
		return true;
	}

	public void updateCircleEffect() {
		if (isMantraReady()) return;
		Random rand = Effect.rand;
		if (rand.nextFloat() < stopRatio) return;
		int cX = gui.width / 2, cY = gui.height / 2;
		for (int i = 0; i < 4; i++) {
			Part p = new Part() {
				@Override
				public void update() {
					super.update();
					this.alpha = this.alpha
							* (1 - Math.min(toolRatio + transitionRation + mantraSelectRatio, 1) * 0.85f);
				}
			};
			p.startLifeTime = p.lifeTime = 20 * 5;
			Color color = this.color.copy();
			p.setColor(color.r, color.g, color.b);
			float roation = -this.rotation + i * 90 + rand.nextFloat() * 30 - 15;
			float x = MathHelper.sin(roation / 180 * 3.14f) * 90;
			float y = MathHelper.cos(roation / 180 * 3.14f) * 90;
			p.setPosition(cX + x, cY + y);
			p.vx = (float) -x * 0.01f;
			p.vy = (float) -y * 0.01f;
			gui.guiEffectBackList.add(p);
		}
	}

	public void setTargetColor(Color color) {
		this.targetColor = color;
		this.colorChangeRatio = 0;
	}

	@Override
	public void onStatusChange() {
		if (gui.reactorStatus == ReactorStatus.STANDBY) canNextState = true;
	}

	@Override
	public void mouseMove(int mouseX, int mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		if (startRatio < 1) return;
		if (stageIndex != STAGE_NONE && stageIndex != STAGE_ER) return;
		stageIndex = STAGE_NONE;
		if (gui.reactorStatus != ReactorStatus.RUNNING) return;
		int cX = gui.width / 2, cY = gui.height / 2;
		float centerSize = 24;
		if (GuiNormal.isMouseIn(mouseX, mouseY, cX - centerSize / 2, cY - centerSize / 2, centerSize, centerSize)) {
			stageIndex = 1;
			return;
		}
	}

	protected int checkSlectList(int mouseX, int mouseY, int size, float range, float extra) {
		if (size > 0) {
			int degree = size > 4 ? (360 / size) : 90;
			for (int i = 0; i < size; i++) {
				float theta = degree * i + this.rotation - 90;
				float x = MathHelper.cos(theta / 180 * 3.1415926f) * range + gui.width / 2;
				float y = MathHelper.sin(theta / 180 * 3.1415926f) * range + gui.height / 2;
				float dx = mouseX - x;
				float dy = mouseY - y;
				float len = MathHelper.sqrt(dx * dx + dy * dy);
				if (len < extra) return i;
			}
		}
		return -1;
	}

	private int checkSelectMantra(int mouseX, int mouseY) {
		List<Mantra> mantras = getMantraList();
		return checkSlectList(mouseX, mouseY, mantras.size(), 100, 14);
	}

	private int checkSelectMantraSub(int mouseX, int mouseY) {
		Mantra mantra = getMantraList().get(selectMantraIndex);
		return checkSlectList(mouseX, mouseY, mantra.getFragmentMantraLaunchers().size(), 30, 13);
	}

	private void sendSelectLauncer(Mantra mantra, IFragmentMantraLauncher launcher) {
		if (lastSendTick > 0) return;
		if (!launcher.canUse(reactor.getReactorCore())) return;
		lastSendTick = 10;
		String id = IFragmentMantraLauncher.toId(mantra, launcher);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("M", id);
		gui.container.sendToServer(nbt);
	}

	private void sendShiftMapLocation(WorldLocation location) {
		if (lastSendTick > 0) return;
		if (location == null) return;
		lastSendTick = 5;
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("SML", true);
		location.writeToNBT(nbt);
		gui.container.sendToServer(nbt);
	}

	private void sendSelectTargetLocation(BlockPos offset) {
		if (lastSendTick > 0) return;
		if (reactor.waitSpellTick > 0) return;
		lastSendTick = 10;
		NBTTagCompound nbt = new NBTTagCompound();
		NBTHelper.setBlockPos(nbt, "ML!", offset);
		NBTHelper.setBlockPos(nbt, "Base", reactor.getWorldMap().getPos());
		gui.container.sendToServer(nbt);
	}

	private void sendForceChargeInfo(boolean finCharge) {
		if (lastSendTick > 0) return;
		lastSendTick = 10;
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("FC~", finCharge);
		gui.container.sendToServer(nbt);
	}

	@Override
	public void mousePress(int mouseX, int mouseY) {
		if (startRatio < 1) return;

		int cX = gui.width / 2, cY = gui.height / 2;
		float centerSize = 24;

		if (isRunningMantra()) {

			if (isMantraReady()) {
				for (GuiLocationBar bar : bars) {
					if (bar.isSelect(mouseX, mouseY)) {
						sendShiftMapLocation(bar.getLocaltion());
						break;
					}
				}
				float x = (mouseX - gui.width / 2) / (GuiElementReactor.MAP_DRAW_SIZE / 2.0f);
				float y = (mouseY - gui.height / 2) / (GuiElementReactor.MAP_DRAW_SIZE / 2.0f);
				float len = MathHelper.sqrt(x * x + y * y);
				if (len <= 1) {
					float range = reactor.getWorldMap().getRange();
					int xoff = (int) (x * range);
					int zoff = (int) (y * range);
					sendSelectTargetLocation(new BlockPos(xoff, 0, zoff));
				} else sendForceChargeInfo(false);
			} else sendForceChargeInfo(true);

			return;
		}

		if (stageIndex == STAGE_MANTRA_SUB) {
			int i = checkSelectMantraSub(mouseX, mouseY);
			if (i != -1) {
				Mantra mantra = getMantraList().get(selectMantraIndex);
				IFragmentMantraLauncher launcher = mantra.getFragmentMantraLaunchers().get(i);
				sendSelectLauncer(mantra, launcher);
			} else stageIndex = STAGE_TOOL;
			return;
		}

		if (stageIndex == STAGE_TOOL) {
			selectMantraIndex = -1;
			if (GuiNormal.isMouseIn(mouseX, mouseY, cX - centerSize / 2, cY - centerSize / 2, centerSize, centerSize)) {
				trySendStop();
				stageIndex = STAGE_NONE;
				return;
			}
			selectMantraIndex = checkSelectMantra(mouseX, mouseY);
			if (selectMantraIndex != -1) {
				Mantra mantra = getMantraList().get(selectMantraIndex);
				List<IFragmentMantraLauncher> list = mantra.getFragmentMantraLaunchers();
				if (list == null || list.isEmpty()) stageIndex = STAGE_NONE;
				else stageIndex = STAGE_MANTRA_SUB;
			} else stageIndex = STAGE_NONE;
			return;
		}

		if (GuiNormal.isMouseIn(mouseX, mouseY, cX - centerSize / 2, cY - centerSize / 2, centerSize, centerSize)) {
			stageIndex = STAGE_TOOL;
			mantras = reactor.checkAndGetMantras();
			return;
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {

	}

	// 展示一些跃迁界面的粒子
	public void spawnEffectOfTransition(Element element) {
		if (isMantraReady()) return;
		Random rand = Effect.rand;
		float maxRange = Math.min(gui.height, gui.width) / 2;
		float levelToRange = maxRange / GuiElementReactor.maxLevel;
		ElementTransition et = element.getTransition();
		float len = et.getLevel() * levelToRange;
		int cX = gui.width / 2, cY = gui.height / 2;
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
		gui.guiEffectFrontList.add(p);
	}

	public boolean isRunningMantra() {
		return reactor.isRunningMantra();
	}

	public boolean isMantraReady() {
		return reactor.isRunningMantraReady();
	}

	@Override
	public void render(float partialTicks) {
		GlStateManager.pushMatrix();
		int cX = gui.width / 2, cY = gui.height / 2;
		GlStateManager.translate(cX, cY, 0);
		// 计算
		float startRatio = RenderFriend.getPartialTicks(this.startRatio, this.prevStartRatio, partialTicks);
		float rotation = RenderFriend.getPartialTicks(this.rotation, this.prevRotation, partialTicks);
		float transRatio = RenderFriend.getPartialTicks(this.transitionRation, this.prevTransitionRation, partialTicks);
		float toolRatio = RenderFriend.getPartialTicks(this.toolRatio, this.prevToolRatio, partialTicks);
		float mantraRatio = RenderFriend.getPartialTicks(this.mantraSelectRatio, this.prevMantraSelectRatio,
				partialTicks);
		float toReadyRatio = RenderFriend.getPartialTicks(this.toReadyRatio, this.prevToReadyRatio, partialTicks);
		float rScale = 1;
		float aH = Math.min(MathHelper.sin(rotation / 180 * 60) * 0.25f + 1, 1), aR = 1;
		if (startRatio < 1) {
			startRatio = startRatio < 0.5f ? 4 * startRatio * startRatio * startRatio
					: 1 - (float) Math.pow(-2 * startRatio + 2, 3) / 2;
			if (startRatio < 0.5) {
				aH = 0;
				aR = startRatio * 2;
				float scale = 1 - startRatio * 2;
				rScale = (1 + scale * 2);
			} else {
				aH = (startRatio - 0.5f) * 2;
			}
		}
		if (transRatio > 0 || toolRatio > 0 || mantraRatio > 0) {
			float ut = Math.min(toolRatio + transRatio + mantraRatio, 1);
			aH = aH * (1 - ut * 0.85f);
			aR = aR * (1 - ut * 0.85f);
		}
		if (rScale != 1) GlStateManager.scale(rScale, rScale, rScale);
		// 画地图
		if (toReadyRatio > 0) {
			aR = aR * (1 - toReadyRatio) + toReadyRatio * 1;
			aH = aH * (1 - toReadyRatio) + toReadyRatio * 1;
			gui.renderMap(toReadyRatio);
			if (reactor.waitSpellTick > 0) {
				// 正在释放
				GuiElementReactor.COMS.bind();
				GlStateManager.color(color.r, color.g, color.b, 1);
				final int totalLen = 168;
				final int useLen = totalLen / 2;
				float tRate = (tick + partialTicks) / 100;
				GlStateManager.scale(2.5, 2.5, 1);
				RenderFriend.drawTextureModalRect(-useLen / 2, -2.5f, (useLen - (useLen * tRate) % useLen), 54, useLen,
						5, 256, 256);
				GlStateManager.scale(1 / 2.5, 1 / 2.5, 1);
			} else if (toReadyRatio >= 1) {
				// 鼠标
				GlStateManager.color(color.r, color.g, color.b, 1);
				float x = mouseX - gui.width / 2;
				float y = mouseY - gui.height / 2;
				float len = MathHelper.sqrt(x * x + y * y);
				if (len < GuiElementReactor.MAP_DRAW_SIZE / 2 - 10) {
					GlStateManager.translate(x, y, 0);
					renderCenterIcon(0.25f, partialTicks);
					GlStateManager.translate(-x, -y, 0);
				}
			}
		}
		GlStateManager.rotate(rotation, 0, 0, 1);
		// 背景圈环
		renderBackGround(aH, aR);
		if (rScale != 1) {
			rScale = 1 / rScale;
			GlStateManager.scale(rScale, rScale, rScale);
		}
		// 跃迁图
		if (transRatio > 0) renderTransition(transRatio);
		if (toReadyRatio > 0) {
			GlStateManager.rotate(-rotation, 0, 0, 1);
			float alpha = toReadyRatio * startRatio;
			for (GuiLocationBar bar : bars) bar.render(mouseX, mouseY, color, alpha, partialTicks);

			if (toReadyRatio >= 1) {
				GlStateManager.popMatrix();
				return;
			}
			aR = aR * (1 - toReadyRatio);
			aH = aH * (1 - toReadyRatio);
			GlStateManager.rotate(rotation, 0, 0, 1);
			GlStateManager.disableAlpha();
		}
		// 旋转归位
		GlStateManager.rotate(-rotation, 0, 0, 1);
		GlStateManager.rotate(-tick / 2, 0, 0, 1);
		// 中心fog
		Color fogColor = color;
		if (mantraRatio > 0) {
			Mantra mantra = getMantraList().get(selectMantraIndex);
			fogColor = fogColor.copy().weight(new Color(mantra.getColor(null)), mantraRatio);
		}
		GlStateManager.color(fogColor.r, fogColor.g, fogColor.b, startRatio);
		TextureBinder.bindTexture(GuiMantraShitf.FOG);
		RenderFriend.drawTextureRectInCenter(0, 0, 80, 80);
		// 切换状态
		GlStateManager.rotate(tick / 2, 0, 0, 1);
		if (startRatio < 1) {
			if (startRatio < 0.75) GlStateManager.scale(0, 0, 0);
			else {
				float scale = (startRatio - 0.75f) / 0.25f;
				GlStateManager.scale(scale, scale, scale);
			}
		}
		// 核心图标
		float iconRatio = Math.min(1, toolRatio + mantraRatio + toReadyRatio);
		float iconScale = 1.4f * (1 - iconRatio) + 0.01f;
		renderCenterIcon(iconScale, partialTicks);
		// 关机图标
		if (toolRatio > 0) {
			iconScale = 1.4f * toolRatio + 0.01f;
			GlStateManager.scale(iconScale, iconScale, iconScale);
			GuiElementReactor.COMS.bind();
			GlStateManager.color(color.r, color.g, color.b, 1);
			RenderFriend.drawTextureRectInCenter(0, 0, 16, 16, 44, 0, 20, 20, 256, 256);
			iconScale = 1 / iconScale;
			GlStateManager.scale(iconScale, iconScale, iconScale);
		}
		// progress
		if (isRunningMantra()) {
			GuiElementReactor.COMS.bind();
			GlStateManager.color(color.r, color.g, color.b, 1 - iconRatio);
			final int totalLen = 168;
			final int useLen = totalLen / 2;
			float progress = reactor.mantraChargeProgress;
			float tRate = (tick + partialTicks) / 100;
			RenderFriend.drawTextureModalRect(-useLen / 2, -37, (useLen - (useLen * tRate) % useLen), 54,
					useLen * progress, 5, 256, 256);
		}
		// mantra sub
		if (mantraRatio > 0) renderMantraSubSelect(mantraRatio, rotation, partialTicks);
		// mantra
		if (toolRatio > 0) renderMantraSelect(rotation, partialTicks);
		// 文字渲染
		renderGUIText(aR);

		GlStateManager.popMatrix();
	}

	private void renderGUIText(float alpha) {
		ElementTransitionReactor core = reactor.getReactorCore();
		FontRenderer fontRenderer = gui.getFontRender();
		// 数字颜色
		int iColor = color.toInt() | (int) (alpha * 255) << 24;
		// 片元个数
		{
			String fragment = TextHelper.toAbbreviatedNumber(core.getFragment());
			int w = fontRenderer.getStringWidth(fragment);
			fontRenderer.drawString(fragment, -w / 2, 24, iColor);
		}
		// 一条线
		int lenWidth = 43;
		GlStateManager.disableTexture2D();
		GL11.glLineWidth(2);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(-lenWidth, 32, 0).color(color.r, color.g, color.b, alpha).endVertex();
		bufferbuilder.pos(lenWidth, 32, 0).color(color.r, color.g, color.b, alpha).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		// 率
		{
			int offset = -6;
			float ir = (float) (reactor.getInstableRatio() * 100);
			String text = ir >= 0.01f ? String.format("|| %.2f%% ||", ir) : "|| <0.01% ||";
			int w1 = fontRenderer.getStringWidth(text);
			fontRenderer.drawString(text, -w1 + offset, 31, iColor);
			float ifr = (float) (reactor.getInstableFragment() / reactor.getInstableFragmentCapacity() * 100);
			text = String.format(" %s(%.1f%%)", TextHelper.toAbbreviatedNumber(reactor.getInstableFragment(), 1), ifr);
			fontRenderer.drawString(text, offset, 31, iColor);
		}
		// 能量线
		if (!isRunningMantra()) {
			String text = String.format("| %d |", reactor.getPowerLine());
			int w = fontRenderer.getStringWidth(text);
			fontRenderer.drawString(text, -w / 2, -40, iColor);
		} else {
			float progress = reactor.mantraChargeProgress;
			String text = String.format("%.1f%%", progress * 100);
			int w = fontRenderer.getStringWidth(text);
			fontRenderer.drawString(text, -w / 2, -45, iColor);
		}
	}

	public void renderMantraSubSelect(float mantraRatio, float rotation, float partialTicks) {
		mantraRatio = mantraRatio * mantraRatio;
		mantraRatio = mantraRatio * mantraRatio;
		List<Mantra> mantras = getMantraList();
		int size = mantras.size();
		Mantra mantra = mantras.get(selectMantraIndex);
		int degree = size > 4 ? (360 / size) : 90;
		GlStateManager.pushMatrix();
		GlStateManager.rotate(rotation, 0, 0, 1);
		GlStateManager.rotate(degree * selectMantraIndex, 0, 0, 1);
		GlStateManager.translate(0, -100 * (1 - mantraRatio), 0);
		float scale = 1 + 0.2f * mantraRatio;
		GlStateManager.scale(scale, scale, scale);
		drawMantraIcon(mantra, 1, partialTicks);
		scale = 1 / scale;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(0, 100 * (1 - mantraRatio), 0);
		GlStateManager.rotate(-degree * selectMantraIndex, 0, 0, 1);
		List<IFragmentMantraLauncher> list = mantra.getFragmentMantraLaunchers();
		size = list.size();
		degree = size > 4 ? (360 / size) : 90;
		Color c = new Color(mantra.getColor(null));
		for (int i = 0; i < size; i++) {
			IFragmentMantraLauncher launcher = list.get(i);
			float a = mantraRatio;
			if (!launcher.canUse(reactor.getReactorCore())) a = a * 0.2f;
			GlStateManager.color(c.r, c.g, c.b, a);
			GlStateManager.rotate(degree * i, 0, 0, 1);
			GlStateManager.translate(0, -30, 0);
			launcher.renderIcon(28, a, partialTicks);
			GlStateManager.translate(0, 30, 0);
			GlStateManager.rotate(-degree * i, 0, 0, 1);
		}
		GlStateManager.popMatrix();
	}

	protected void drawMantraFog(Mantra mantra, float rotation, float a, float partialTicks) {
		Color c = new Color(mantra.getColor(null));
		GlStateManager.color(c.r, c.g, c.b, a);

		GlStateManager.rotate(-rotation * 2, 0, 0, 1);
		TextureBinder.bindTexture(GuiMantraShitf.FOG);
		RenderFriend.drawTextureRectInCenter(0, 0, 80, 80);
		GlStateManager.rotate(rotation * 2, 0, 0, 1);
	}

	protected void drawMantraIcon(Mantra mantra, float a, float partialTicks) {
		Color c = new Color(mantra.getColor(null));
		GlStateManager.color(c.r, c.g, c.b, a);
		mantra.renderShiftIcon(new NBTTagCompound(), 32, a, partialTicks);
	}

	private void renderMantraSelect(float rotation, float partialTicks) {
		List<Mantra> mantras = getMantraList();
		int size = mantras.size();
		if (size == 0) return;
		GlStateManager.pushMatrix();
		GlStateManager.rotate(rotation, 0, 0, 1);
		int degree = size > 4 ? (360 / size) : 90;
		for (int i = 0; i < size; i++) {
			Mantra mantra = mantras.get(i);
			GlStateManager.rotate(degree * i, 0, 0, 1);
			GlStateManager.translate(0, -100, 0);
			float a = toolRatio * (selectMantraIndex == i ? 1 : 0.8f);
			drawMantraFog(mantra, rotation, a, partialTicks);
			drawMantraIcon(mantra, a, partialTicks);
			GlStateManager.translate(0, 100, 0);
			GlStateManager.rotate(-degree * i, 0, 0, 1);
		}
		GlStateManager.popMatrix();
	}

	public void renderCenterIcon(float iconScale, float partialTicks) {
		GlStateManager.scale(iconScale, iconScale, iconScale);
		if (isRunningMantra()) {
			IFragmentMantraLauncher.MLPair pair = reactor.getRunningMantraPair();
			pair.launcher.renderIcon(32, 1, partialTicks);
		} else {
			ElementTransitionReactor core = reactor.getReactorCore();
			Element element = core.getElement();
			element.drawElemntIconInGUI(new ElementStack(element), 0, 0,
					Element.DRAW_GUI_FLAG_CENTER | Element.DRAW_GUI_FLAG_NO_INFO);
		}
		iconScale = 1 / iconScale;
		GlStateManager.scale(iconScale, iconScale, iconScale);
	}

	public void renderBackGround(float aH, float aR) {
		GlStateManager.color(colorLight.r, colorLight.g, colorLight.b, aH);
		GuiElementReactor.HALO.bind();
		RenderFriend.drawTextureRectInCenter(0, 0, 256, 256, 0, 0, 256, 256, 256, 256);

		GlStateManager.color(color.r, color.g, color.b, aR);
		GuiElementReactor.RING.bind();
		RenderFriend.drawTextureRectInCenter(0, 0, 256, 256, 0, 0, 256, 256, 256, 256);
	}

	// 画转化图
	public void renderTransition(float t) {
		ElementTransitionReactor met = reactor.getReactorCore();

		float maxRange = Math.min(gui.height, gui.width) / 2;
		float levelToRange = maxRange / GuiElementReactor.maxLevel;
		GuiElementReactor.ARC.bind();
		Element[] elements = GuiElementReactor.getInvolvedElements();
		for (Element element : elements) {
			ElementTransition et = element.getTransition();
			float ct = (float) Math.pow(t, et.getLevel());
			Color color = new Color(element.getColor(new ElementStack(element)));
			GlStateManager.color(color.r, color.g, color.b, ct);
			float scale = t < 1 ? (1 - ct) * 2 + 1 : 1;
			float rScale = 1 / scale;
			GlStateManager.scale(scale, scale, scale);
			GuiElementReactor.renderArc(et.getKernelAngle(), et.getRegionAngle(), et.getLevel() * levelToRange, color);
			GlStateManager.scale(rScale, rScale, rScale);
		}
		GlStateManager.color(color.r, color.g, color.b, t);
		GuiElementReactor.renderArc(0, 360, levelToRange, color);

		GuiElementReactor.COMS.bind();
		GlStateManager.color(Math.min(1, color.r + 0.2f), Math.min(1, color.g + 0.2f), Math.min(1, color.b + 0.2f), t);
		float angle = (met.getAngle() - 90) / 180 * 3.1415926f;
		float step = met.getStep() - 0.05f;
		float xoff = MathHelper.cos(angle) * step * levelToRange;
		float yoff = MathHelper.sin(angle) * step * levelToRange;
		RenderFriend.drawTextureRectInCenter(xoff, yoff, 6, 6, 38, 0, 6, 6, 256, 256);
	}

	@Override
	public GERActionState getNextState() {
		return canNextState ? new GERWaitingStartState() : null;
	}

}
