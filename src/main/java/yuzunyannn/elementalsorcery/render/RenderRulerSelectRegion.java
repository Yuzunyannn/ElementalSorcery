package yuzunyannn.elementalsorcery.render;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.event.IRenderClient;
import yuzunyannn.elementalsorcery.event.ITickTask;
import yuzunyannn.elementalsorcery.item.tool.ItemMagicRuler;

@SideOnly(Side.CLIENT)
public class RenderRulerSelectRegion implements IRenderClient, ITickTask {

	private static List<EnumHand> inner = new LinkedList<>();

	/** 展示物品显示区域 */
	static public boolean showItem(EntityPlayer player, EnumHand hand) {
		ItemStack ruler = player.getHeldItem(hand);
		if (inner.contains(hand)) {
			return true;
		}
		Integer dimensionId = ItemMagicRuler.getDimensionId(ruler);
		if (dimensionId == null || dimensionId != player.dimension)
			return false;
		BlockPos pos = ItemMagicRuler.getRulerPos(ruler, true);
		if (pos == null)
			return false;
		pos = ItemMagicRuler.getRulerPos(ruler, false);
		if (pos == null)
			return false;
		RenderRulerSelectRegion render = new RenderRulerSelectRegion(player, hand);
		EventClient.addTickTask(render);
		EventClient.addRenderTask(render);
		return true;
	}

	final EntityPlayer player;
	ItemStack stack;
	EnumHand hand;
	private boolean isEnd = false;
	private BlockPos pos1;
	private BlockPos pos2;
	// 需要结束
	private int wantEnd = 0;
	// 动态变化的角度
	private float theta = 0.0f;
	// 渐变时间
	private float fadeTime = 0.0f;
	// 是否处于渐变退场状态
	private boolean inFade = false;

	private final static float FADE_RATE = 0.035f;
	private final static float DTHETA = 0.05f;

	private float r, g, b;

	protected RenderRulerSelectRegion(EntityPlayer player, EnumHand hand) {
		this.player = player;
		this.hand = hand;
		this.stack = this.player.getHeldItem(hand);
		this.pos1 = ItemMagicRuler.getRulerPos(stack, true);
		this.pos2 = ItemMagicRuler.getRulerPos(stack, false);
		if (this.pos1 == null || this.pos2 == null)
			this.end();
		EnumDyeColor dyeColor = ItemMagicRuler.getColor(this.stack);
		this.setColor(dyeColor.getColorValue());
		inner.add(hand);
	}

	public void setColor(int color) {
		this.r = (0xff & (color >> 16)) / 255.0f;
		this.g = (0xff & (color >> 8)) / 255.0f;
		this.b = (0xff & (color >> 0)) / 255.0f;
	}

	@Override
	public int onTick() {
		this.theta += DTHETA;
		if (this.player.getEntityWorld() != Minecraft.getMinecraft().world) {
			this.end();
		}
		ItemStack stack = this.player.getHeldItem(hand);
		if (stack != this.stack) {
			this.checkSame();
			this.wantEnd = this.wantEnd | 0x01;
		} else {
			this.wantEnd = this.wantEnd & 0xFE;
		}
		if (EventClient.tick % 10 == 0)
			this.checkPos();
		if (this.wantEnd != 0) {
			if (fadeTime > 0) {
				inFade = true;
				fadeTime -= FADE_RATE;
				if (fadeTime < 0)
					fadeTime = 0.0f;
			} else
				this.end();
		} else {
			if (fadeTime < 1) {
				inFade = false;
				fadeTime += FADE_RATE;
				if (fadeTime >= 1.0f)
					fadeTime = 1.0f;
			}
		}
		return isEnd ? ITickTask.END : ITickTask.SUCCESS;
	}

	private void checkPos() {
		BlockPos pos1 = ItemMagicRuler.getRulerPos(this.stack, true);
		BlockPos pos2 = ItemMagicRuler.getRulerPos(this.stack, false);
		if (pos1 == null || pos2 == null) {
			this.wantEnd = this.wantEnd | 0x02;
		} else {
			this.pos1 = pos1;
			this.pos2 = pos2;
			this.wantEnd = this.wantEnd & 0xFD;
		}
	}

	private boolean endWithNotSame() {
		return (this.wantEnd & 0x04) != 0;
	}

	// 检测相同的物品
	private void checkSame() {
		if (this.endWithNotSame())
			return;
		ItemStack stack = this.player.getHeldItem(hand);
		if (stack.getItem() == this.stack.getItem()) {
			EnumDyeColor dyeColor = ItemMagicRuler.getColor(this.stack);
			this.setColor(dyeColor.getColorValue());
			BlockPos pos1 = ItemMagicRuler.getRulerPos(stack, true);
			BlockPos pos2 = ItemMagicRuler.getRulerPos(stack, false);
			if (this.pos1.equals(pos1) || this.pos2.equals(pos2)) {
				this.stack = stack;
				this.checkPos();
			} else {
				this.wantEnd = this.wantEnd | 0x04;
				inner.remove(this.hand);
			}
		}
	}

	private void end() {
		isEnd = true;
		if (!this.endWithNotSame())
			inner.remove(this.hand);
	}

	final Tessellator tessellator = Tessellator.getInstance();
	final BufferBuilder bufferbuilder = tessellator.getBuffer();

	@Override
	public int onRender(float partialTicks) {
		if (isEnd)
			return IRenderClient.END;
		GlStateManager.pushMatrix();
		GlStateManager.translate(pos1.getX(), pos1.getY(), pos1.getZ());
		float lx = pos2.getX() - pos1.getX();
		float ly = pos2.getY() - pos1.getY();
		float lz = pos2.getZ() - pos1.getZ();
		if (lx < 0) {
			GlStateManager.translate(lx, 0, 0);
			lx = -lx;
		}
		if (ly < 0) {
			GlStateManager.translate(0, ly, 0);
			ly = -ly;
		}
		if (lz < 0) {
			GlStateManager.translate(0, 0, lz);
			lz = -lz;
		}
		lx += 1;
		ly += 1;
		lz += 1;
		float fadeTime;
		if (inFade) {
			fadeTime = this.fadeTime - FADE_RATE * partialTicks;
			if (fadeTime < 0)
				fadeTime = 0;
		} else {
			fadeTime = this.fadeTime + FADE_RATE * partialTicks;
			if (fadeTime > 1)
				fadeTime = 1;
		}
		float theta = this.theta + DTHETA * partialTicks;

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.glLineWidth(5);
		bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		vertexCubeLine(bufferbuilder, null, lx, ly, lz, r, g, b, fadeTime);
		tessellator.draw();

		GlStateManager.disableCull();
		final float dis = 0.025f;
		// 下面
		for (int i = 0; i < lx; i++) {
			for (int j = 0; j < lz; j++) {
				float anime = (MathHelper.sin(theta + j + i) + 1) * 0.5f;
				final float lenDis = 0.4f * anime + 0.1f;
				final float lenDis_ = 1.0f - lenDis;
				anime = (1 - anime) * fadeTime;
				bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
				bufferbuilder.pos(i + lenDis, -dis, j + lenDis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis_, -dis, j + lenDis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis_, -dis, j + lenDis_).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis, -dis, j + lenDis_).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis, -dis, j + lenDis).color(r, g, b, anime).endVertex();
				tessellator.draw();
			}
		}
		// 上面
		for (int i = 0; i < lx; i++) {
			for (int j = 0; j < lz; j++) {
				float anime = (MathHelper.sin((float) theta + j + i) + 1) * 0.5f;
				final float lenDis = 0.4f * anime + 0.1f;
				final float lenDis_ = 1.0f - lenDis;
				anime = (1 - anime) * fadeTime;
				bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
				bufferbuilder.pos(i + lenDis, ly + dis, j + lenDis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis_, ly + dis, j + lenDis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis_, ly + dis, j + lenDis_).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis, ly + dis, j + lenDis_).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis, ly + dis, j + lenDis).color(r, g, b, anime).endVertex();
				tessellator.draw();
			}
		}
		// 左
		for (int i = 0; i < lx; i++) {
			for (int j = 0; j < ly; j++) {
				float anime = (MathHelper.sin(theta + j + i) + 1) * 0.5f;
				final float lenDis = 0.4f * anime + 0.1f;
				final float lenDis_ = 1.0f - lenDis;
				anime = (1 - anime) * fadeTime;
				bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
				bufferbuilder.pos(i + lenDis, j + lenDis, -dis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis_, j + lenDis, -dis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis_, j + lenDis_, -dis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis, j + lenDis_, -dis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis, j + lenDis, -dis).color(r, g, b, anime).endVertex();
				tessellator.draw();
			}
		}
		// 右
		for (int i = 0; i < lx; i++) {
			for (int j = 0; j < ly; j++) {
				float anime = (MathHelper.sin(theta + j + i) + 1) * 0.5f;
				final float lenDis = 0.4f * anime + 0.1f;
				final float lenDis_ = 1.0f - lenDis;
				anime = (1 - anime) * fadeTime;
				bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
				bufferbuilder.pos(i + lenDis, j + lenDis, lz + dis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis_, j + lenDis, lz + dis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis_, j + lenDis_, lz + dis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis, j + lenDis_, lz + dis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(i + lenDis, j + lenDis, lz + dis).color(r, g, b, anime).endVertex();
				tessellator.draw();
			}
		}
		// 后
		for (int i = 0; i < ly; i++) {
			for (int j = 0; j < lz; j++) {
				float anime = (MathHelper.sin(theta + j + i) + 1) * 0.5f;
				final float lenDis = 0.4f * anime + 0.1f;
				final float lenDis_ = 1.0f - lenDis;
				anime = (1 - anime) * fadeTime;
				bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
				bufferbuilder.pos(-dis, i + lenDis, j + lenDis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(-dis, i + lenDis_, j + lenDis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(-dis, i + lenDis_, j + lenDis_).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(-dis, i + lenDis, j + lenDis_).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(-dis, i + lenDis, j + lenDis).color(r, g, b, anime).endVertex();
				tessellator.draw();
			}
		}
		// 前
		for (int i = 0; i < ly; i++) {
			for (int j = 0; j < lz; j++) {
				float anime = (MathHelper.sin(theta + j + i) + 1) * 0.5f;
				final float lenDis = 0.4f * anime + 0.1f;
				final float lenDis_ = 1.0f - lenDis;
				anime = (1 - anime) * fadeTime;
				bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
				bufferbuilder.pos(lx + dis, i + lenDis, j + lenDis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(lx + dis, i + lenDis_, j + lenDis).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(lx + dis, i + lenDis_, j + lenDis_).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(lx + dis, i + lenDis, j + lenDis_).color(r, g, b, anime).endVertex();
				bufferbuilder.pos(lx + dis, i + lenDis, j + lenDis).color(r, g, b, anime).endVertex();
				tessellator.draw();
			}
		}
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
		return IRenderClient.SUCCESS;
	}

	static public void vertexCubeLine(BufferBuilder bufferbuilder, BlockPos pos, float lx, float ly, float lz, float r,
			float g, float b, float fadeTime) {
		int xoff = 0;
		int yoff = 0;
		int zoff = 0;
		if (pos != null) {
			xoff = pos.getX();
			yoff = pos.getY();
			zoff = pos.getZ();
		}

		bufferbuilder.pos(xoff, yoff, zoff).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(xoff + lx, yoff, zoff).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(xoff + lx, yoff, zoff + lz).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(xoff, yoff, zoff + lz).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(xoff, yoff, zoff).color(r, g, b, fadeTime).endVertex();

		bufferbuilder.pos(xoff, yoff + ly, zoff).color(r, g, b, fadeTime).endVertex();

		bufferbuilder.pos(xoff + lx, yoff + ly, zoff).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(xoff + lx, yoff, zoff).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(xoff + lx, yoff + ly, zoff).color(r, g, b, fadeTime).endVertex();

		bufferbuilder.pos(xoff + lx, yoff + ly, zoff + lz).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(xoff + lx, yoff, zoff + lz).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(xoff + lx, yoff + ly, zoff + lz).color(r, g, b, fadeTime).endVertex();

		bufferbuilder.pos(xoff, yoff + ly, zoff + lz).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(xoff, yoff, zoff + lz).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(xoff, yoff + ly, zoff + lz).color(r, g, b, fadeTime).endVertex();

		bufferbuilder.pos(xoff, yoff + ly, zoff).color(r, g, b, fadeTime).endVertex();
	}
}
