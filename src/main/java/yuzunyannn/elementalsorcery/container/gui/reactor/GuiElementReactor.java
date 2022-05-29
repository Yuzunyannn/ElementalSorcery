package yuzunyannn.elementalsorcery.container.gui.reactor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.container.ContainerElementReactor;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.explore.Explores;
import yuzunyannn.elementalsorcery.item.crystal.ItemNatureCrystal;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectFragment;
import yuzunyannn.elementalsorcery.render.effect.gui.GUIEffectBatch;
import yuzunyannn.elementalsorcery.render.effect.gui.GUIEffectBatchList;
import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor;
import yuzunyannn.elementalsorcery.util.element.ElementTransitionReactor;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.util.world.MapHelper;
import yuzunyannn.elementalsorcery.util.world.WorldLocation;

@SideOnly(Side.CLIENT)
public class GuiElementReactor extends GuiScreen {

	public static final int MAP_DRAW_SIZE = 220;

	private static DynamicTexture mapTexture;

	public static DynamicTexture getDynamicTexture() {
		if (mapTexture == null) mapTexture = new DynamicTexture(TileElementReactor.SELECT_MAP_SIZE * 2,
				TileElementReactor.SELECT_MAP_SIZE * 2);
		return mapTexture;
	}

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
	public final List<WorldLocation> ncList = new ArrayList<>();
	// 反应堆的状态
	public TileElementReactor.ReactorStatus reactorStatus;
	public int mapBlockVer;

	// 当前状态机
	protected GERActionState currState;
	protected final List<GERActionState> overGroups = new LinkedList<>();

	public GuiElementReactor(ContainerElementReactor container) {
		this.container = container;
		this.reactor = container.tileEntity;
		this.container.guiObject = this;
		this.reactorStatus = this.reactor.getStatus();
		NonNullList<ItemStack> list = container.player.inventory.mainInventory;
		for (ItemStack stack : list) {
			NBTTagCompound dat = ItemNatureCrystal.getData(stack);
			if (dat != null) {
				int id = Explores.BASE.getWorldId(dat);
				BlockPos pos = Explores.BASE.getPos(dat);
				if (pos != BlockPos.ORIGIN) ncList.add(new WorldLocation(id, pos));
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		this.mc.player.openContainer = this.container;
		if (this.currState == null)
			this.setCurrState(this.reactorStatus.isRunning ? new GERRunningState() : new GERWaitingStartState());
	}

	public void setCurrState(GERActionState currState) {
		if (this.currState != null) overGroups.add(this.currState);
		this.currState = currState;
		this.currState.init(this);
	}

	public FontRenderer getFontRender() {
		return this.fontRenderer;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		currState.update();
		GERActionState nextState = this.currState.getNextState();
		if (nextState != null && currState != nextState) setCurrState(nextState);

		Iterator<GERActionState> iter = overGroups.iterator();
		if (iter.hasNext()) {
			GERActionState group = iter.next();
			if (!group.updateOver()) iter.remove();
		}

		guiEffectFrontList.update();
		guiEffectBackList.update();

		if (this.reactorStatus != reactor.getStatus()) {
			this.reactorStatus = reactor.getStatus();
			currState.onStatusChange();
		}

		if (mapBlockVer != reactor.getWorldMap().blockUpdateVer) {
			MapHelper worldMap = reactor.getWorldMap();
			mapBlockVer = worldMap.blockUpdateVer;
			DynamicTexture texture = getDynamicTexture();
			int[] buff = texture.getTextureData();
			byte[] colors = worldMap.getColors();
			int len = Math.min(buff.length, colors.length);
			for (int i = 0; i < len; i++) {
				int color = worldMap.getColorByColorByteValue(colors[i]);
				BlockPos pos = worldMap.getOffsetFromIndex(i);
				float toCenter = MathHelper.sqrt(pos.getX() * pos.getX() + pos.getZ() * pos.getZ());
				if (toCenter / worldMap.getRange() > 0.9) {
					int alpha = (int) ((Math.max(1 - toCenter / worldMap.getRange(), 0) / 0.2f) * 255);
					color = color & ((alpha << 24) | 0xffffff);
				}
				buff[i] = color;
			}
			texture.updateDynamicTexture();
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
		for (GERActionState group : overGroups) group.render(partialTicks);
		guiEffectFrontList.render(partialTicks);

	}

	public void renderMap(float alpha) {
		DynamicTexture texture = getDynamicTexture();
		GlStateManager.bindTexture(texture.getGlTextureId());

		BlockPos pos = reactor.getWorldMap().getPos();
		List<MapHelper.EntityData> list = reactor.getWorldMap().getEntityList();
		GlStateManager.color(1, 1, 1, alpha);
		RenderHelper.drawTexturedRectInCenter(0, 0, MAP_DRAW_SIZE, MAP_DRAW_SIZE, 0, 0, 1, 1, 1, 1);

		COMS.bind();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		float pScale = 1.0f / TileElementReactor.SELECT_MAP_SIZE * MAP_DRAW_SIZE / 2;
		float fSize = 2;
		float range = reactor.getWorldMap().getRange();
		for (MapHelper.EntityData ed : list) {
			if (ed.pos == null) continue;
			Vec3d vec = ed.pos.subtract(new Vec3d(pos));
			if (vec.length() / range > 0.9) continue;
			Color c = new Color(ed.name.hashCode());
			float x = (float) vec.x * pScale;
			float y = (float) vec.z * pScale;
			bufferbuilder.pos(x - fSize, y - fSize, 0.0D).tex(64 / 256.0, 0);
			bufferbuilder.color(c.r, c.b, c.g, alpha).endVertex();
			bufferbuilder.pos(x - fSize, y + fSize, 0.0D).tex(64 / 256.0, 8 / 256.0);
			bufferbuilder.color(c.r, c.b, c.g, alpha).endVertex();
			bufferbuilder.pos(x + fSize, y + fSize, 0.0D).tex((64 + 9) / 256.0, 8 / 256.0);
			bufferbuilder.color(c.r, c.b, c.g, alpha).endVertex();
			bufferbuilder.pos(x + fSize, y - fSize, 0.0D).tex((64 + 9) / 256.0, 0);
			bufferbuilder.color(c.r, c.b, c.g, alpha).endVertex();
		}
		tessellator.draw();
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		currState.mouseMove(i, j);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
			this.mc.player.closeScreen();
			return;
		}
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		currState.mousePress(mouseX, mouseY);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		currState.mouseReleased(mouseX, mouseY);
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
