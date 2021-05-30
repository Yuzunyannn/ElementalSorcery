package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ContainerFairyCube;
import yuzunyannn.elementalsorcery.entity.fcube.EntityFairyCube;
import yuzunyannn.elementalsorcery.entity.fcube.FairyCubeModule;
import yuzunyannn.elementalsorcery.util.ColorHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.Shaders;

@SideOnly(Side.CLIENT)
public class GuiFairyCube extends GuiScreen {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/fairy_cube.png");
	final ContainerFairyCube container;

	public int tick;

	public GuiFairyCube(ContainerFairyCube containerFairyCube) {
		this.container = containerFairyCube;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.mc.player.openContainer = this.container;
		for (int i = 0; i < modulePositions.length; i++) moduleRenders.add(new ModuleRenderInfo(i));
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		if (this.mc.player != null) this.container.onContainerClosed(this.mc.player);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
			this.mc.player.closeScreen();
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void updateScreen() {
		tick++;
		if (updateVersion != container.updateVersion) {
			updateVersion = container.updateVersion;
			if (container.fairyCube != null) {
				List<FairyCubeModule> modules = container.fairyCube.getModules();
				for (int i = 0; i < modules.size() && i < moduleRenders.size(); i++) {
					moduleRenders.get(i).updateModule(modules.get(i));
				}
			}
		}

		for (ModuleRenderInfo info : moduleRenders) info.update();
		if (sendCD > 0) sendCD--;
	}

	public int selected = -1;
	public float hoverX, hoverY;

	@Override
	public void handleMouseInput() throws IOException {
		int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		int cX = this.width / 2;
		int cY = this.height / 2;
		int xoff = mouseX - cX + 1;
		int yoff = mouseY - cY + 1;
		int half = 52 / 2;
		selected = -1;
		for (int i = 0; i < modulePositions.length; i++) {
			Vec3d vec = modulePositions[i];
			if (vec.x - half < xoff && vec.x + half > xoff && vec.y - half < yoff && vec.y + half > yoff) {
				selected = i;
				hoverX = xoff - (float) (vec.x - half);
				hoverY = yoff - (float) (vec.y - half);
				break;
			}
		}
		if (Mouse.getEventButtonState()) {
			if (selected != -1) {
				moduleRenders.get(selected).click();
			}
		}
	}

	public static final int MOFF = 85;
	public static final int PMOFF = 54;

	public static final Vec3d[] modulePositions = new Vec3d[] { new Vec3d(-MOFF, -MOFF, 0),
			new Vec3d(-MOFF + PMOFF, -MOFF, 0), new Vec3d(-MOFF, -MOFF + PMOFF, 0), new Vec3d(MOFF, -MOFF, 0),
			new Vec3d(MOFF - PMOFF, -MOFF, 0), new Vec3d(MOFF, -MOFF + PMOFF, 0), new Vec3d(-MOFF, MOFF, 0),
			new Vec3d(-MOFF + PMOFF, MOFF, 0), new Vec3d(-MOFF, MOFF - PMOFF, 0), new Vec3d(MOFF, MOFF, 0),
			new Vec3d(MOFF - PMOFF, MOFF, 0), new Vec3d(MOFF, MOFF - PMOFF, 0) };

	public List<ModuleRenderInfo> moduleRenders = new ArrayList<>(12);
	public int updateVersion = 0;
	public int sendCD = 0;

	public class ModuleRenderInfo {
		public final int index;
		public FairyCubeModule module;
		public float alpha = 0.5f, prevAlpha = 0.5f;
		public float srate = 0, prevSRate = 0;
		public float nilRate = 0;
		public float shiftRate = 0;

		public float oH, nH;
		public float oS = 1, nS = 0;
		public float oV = 1, nV = 1;

		public ModuleRenderInfo(int index) {
			this.index = index;
		}

		public void setAnimeRate(float rate) {
			Shaders.HSV.setUniform("r_anime", rate);
		}

		public void setAnimeHSV(float h, float s, float v) {
			Shaders.HSV.setUniform("a_hue", h);
			Shaders.HSV.setUniform("a_saturation", s);
			Shaders.HSV.setUniform("a_value", v);
		}

		public void setHSV(float h, float s, float v) {
			Shaders.HSV.setUniform("u_hue", h);
			Shaders.HSV.setUniform("u_saturation", s);
			Shaders.HSV.setUniform("u_value", v);
		}

		public void updateModule(FairyCubeModule module) {
			if (module == null) {
				nH = oH = 0;
				nS = 0;
				oS = 1;
				nV = oV = 1;
			} else {
				if (this.module == null) {
					oH = 0;
					oS = 1;
					oV = 1;
				} else {
					oH = nH;
					oS = nS;
					oV = nV;
				}
				int status = module.getCurrStatus();
				if (status == 0) {
					nH = 0;
					nS = 0;
					nV = 1;
				} else {
					int color = module.getStatusColor(status);
					Vec3d c = ColorHelper.color(color);
					Vec3d hsvColor = ColorHelper.rgbToHSV((float) c.x, (float) c.y, (float) c.z);
					nH = (float) hsvColor.x - 155.54347f;
					nS = (float) hsvColor.y / 0.8598131f;
					nV = (float) hsvColor.z / 0.8392157f;
				}
				if (this.module != null) shiftRate = 1;
			}
			this.module = module;
		}

		public void click() {
			if (module == null) nilRate = 1;
			else if (sendCD > 0) return;
			sendCD = 15;
			container.sendChangeStatus(index);
		}

		public void update() {
			boolean isSelect = selected == index;
			this.prevAlpha = this.alpha;
			this.prevSRate = this.srate;

			if (nilRate > 0) {
				isSelect = true;
				nilRate = Math.max(nilRate - 0.1f, 0);
				this.srate = MathHelper.sin(nilRate * 3.14f) * 0.5f;
			}
			if (shiftRate > 0) {
				shiftRate = Math.max(shiftRate - 0.075f, 0);
				srate = shiftRate;
			}

			if (isSelect) this.alpha = Math.min(this.alpha + 0.1f, 1);
			else this.alpha = Math.max(this.alpha - 0.1f, module == null ? 0.5f : 0.75f);
		}

		public void render(float partialTicks) {
			setHSV(nH, nS, nV);
			setAnimeHSV(oH, oS, oV);
			mc.getTextureManager().bindTexture(TEXTURE);
			float sr = RenderHelper.getPartialTicks(srate, prevSRate, partialTicks);
			setAnimeRate(sr);
			float a = RenderHelper.getPartialTicks(alpha, prevAlpha, partialTicks);
			GlStateManager.color(1, 1, 1, a);
			RenderHelper.drawTexturedRectInCenter(0, 0, 52, 52, 0, 55, 52, 52, 256, 256);

			if (module != null) {
				module.onRenderGUIIcon();
				boolean isSelect = selected == index;
				String statusValue = null;
				if (isSelect) statusValue = module.getStatusValue(module.getCurrStatus());
				if (statusValue != null && !statusValue.isEmpty()) {
					statusValue = TextFormatting.BOLD + statusValue;
					int width = fontRenderer.getStringWidth(statusValue);
					fontRenderer.drawString(statusValue, -width / 2 + 1, 16 - 3, ((int) (a * 255) << 24) | 0x133435);
				} else {
					mc.getTextureManager().bindTexture(TEXTURE);

					RenderHelper.drawTexturedRectInCenter(-3, 16, 8, 8, 48, 107, 16, 16, 256, 256);
					int level = module.getLimitLevel();
					int realLevel = MathHelper.floor(module.getLevel());
					if (realLevel > level) {
						RenderHelper.drawTexturedRectInCenter(-10, 13, 5, 5, 32, 107, 16, 16, 256, 256);
					}
					String levString = TextFormatting.BOLD.toString() + level;
					fontRenderer.drawString(levString, -3 + 6, 16 - 3, ((int) (a * 255) << 24) | 0x133435);
				}

			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		partialTicks = mc.getRenderPartialTicks();
		this.drawDefaultBackground();

		EntityFairyCube fairyCube = this.container.fairyCube;
		if (fairyCube == null) return;

		int cX = this.width / 2;
		int cY = this.height / 2;
		int move = 112;
		int rect = 124;

		Gui.drawRect(cX - rect, cY - rect, cX + rect, cY + rect, 0x44b5f4de);

		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(cX, cY, 0);

		this.mc.getTextureManager().bindTexture(TEXTURE);

		for (int i = 0; i < 4; i++) {
			GlStateManager.translate(0, -move, 0);
			RenderHelper.drawTexturedRectInCenter(0, 0, 256, 32, 0, 0, 256, 32, 256, 256);
			GlStateManager.translate(0, move, 0);
			GlStateManager.rotate(90, 0, 0, 1);
		}

		// eyes
		if (tick % 100 < 3) {
			RenderHelper.drawTexturedRectInCenter(-20, -10, 23, 8, 8, 32, 23, 8, 256, 256);
			RenderHelper.drawTexturedRectInCenter(20, -10, 23, 8, 8, 32, 23, 8, 256, 256);
		} else {
			RenderHelper.drawTexturedRectInCenter(-20, -10, 8, 23, 0, 32, 8, 23, 256, 256);
			RenderHelper.drawTexturedRectInCenter(20, -10, 8, 23, 0, 32, 8, 23, 256, 256);
		}

		Shaders.HSV.bind();
		for (int i = 0; i < modulePositions.length; i++) {
			GlStateManager.pushMatrix();
			Vec3d vec = modulePositions[i];
			GlStateManager.translate(vec.x, vec.y, vec.z);
			moduleRenders.get(i).render(partialTicks);
			GlStateManager.popMatrix();
		}
		Shaders.HSV.unbind();

		GlStateManager.color(1, 1, 1, 1.0f);
		this.mc.getTextureManager().bindTexture(TEXTURE);
		GlStateManager.translate(0, 20, 0);
		RenderHelper.drawTexturedRectInCenter(-4, 0, 8, 8, 16, 107, 16, 16, 256, 256);
		RenderHelper.drawTexturedRectInCenter(-4, 12, 8, 8, 0, 107, 16, 16, 256, 256);

		float exp = fairyCube.getLevelUpgradeProgress();
		int expCount = Math.round(exp * 10);
		for (int i = 0; i < expCount; i++) {
			int xoff = -expCount * 4 + i * 8 + 4;
			RenderHelper.drawTexturedRectInCenter(xoff, 24, 4, 4, 0, 124, 4, 4, 256, 256);
		}

		String levString = TextFormatting.BOLD.toString() + fairyCube.getCubeLevel();
		this.fontRenderer.drawString(levString, 3, 12 - 3, 0x12cc80);
		String powString = TextFormatting.BOLD.toString() + MathHelper.ceil(fairyCube.getPhysicalStrength());
		this.fontRenderer.drawString(powString, 3, -3, 0x12cc80);

		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
	}

}
