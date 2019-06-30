package yuzunyan.elementalsorcery.render;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

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
import yuzunyan.elementalsorcery.event.EventClient;
import yuzunyan.elementalsorcery.event.IRenderClient;
import yuzunyan.elementalsorcery.event.ITickTask;
import yuzunyan.elementalsorcery.item.ItemMagicRuler;

@SideOnly(Side.CLIENT)
public class RenderRulerSelectRegion implements IRenderClient, ITickTask {

	private static List<ItemStack> inner = new LinkedList<>();

	/** 展示物品显示区域 */
	static public boolean showItem(EntityPlayer player, EnumHand hand) {
		ItemStack ruler = player.getHeldItem(hand);
		if (inner.contains(ruler)) {
			return true;
		}
		BlockPos pos = ItemMagicRuler.getRulerPos(ruler, true);
		if (pos == null)
			return false;
		pos = ItemMagicRuler.getRulerPos(ruler, false);
		if (pos == null)
			return false;
		EnumDyeColor dyeColor = ItemMagicRuler.getColor(ruler);
		RenderRulerSelectRegion render = new RenderRulerSelectRegion(player, hand, dyeColor.getColorValue());
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
	// 动态变化的角度
	private float theta = 0.0f;
	// 渐变时间
	private float fadeTime = 0.0f;
	// 是否处于渐变退场状态
	private boolean inFade = false;

	private final static float FADE_RATE = 0.02f;
	private final static float DTHETA = 0.025f;

	private float r, g, b;

	protected RenderRulerSelectRegion(EntityPlayer player, EnumHand hand, int color) {
		this.player = player;
		this.hand = hand;
		this.stack = this.player.getHeldItem(hand);
		this.pos1 = ItemMagicRuler.getRulerPos(stack, true);
		this.pos2 = ItemMagicRuler.getRulerPos(stack, false);
		if (this.pos1 == null || this.pos2 == null)
			this.end();
		this.r = (0xff & (color >> 16)) / 255.0f;
		this.g = (0xff & (color >> 8)) / 255.0f;
		this.b = (0xff & (color >> 0)) / 255.0f;
		inner.add(this.stack);
	}

	@Override
	public int onTick() {
		this.theta += DTHETA;
		ItemStack stack = this.player.getHeldItem(hand);
		if (stack != this.stack) {
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
		if (EventClient.tick % 10 == 0) {
			this.pos1 = ItemMagicRuler.getRulerPos(this.stack, true);
			this.pos2 = ItemMagicRuler.getRulerPos(this.stack, false);
			if (this.pos1 == null || this.pos2 == null)
				this.end();
		}

		return isEnd ? ITickTask.END : ITickTask.SUCCESS;
	}

	private void end() {
		isEnd = true;
		inner.remove(this.stack);
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

		bufferbuilder.pos(0, 0, 0).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(lx, 0, 0).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(lx, 0, lz).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(0, 0, lz).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(0, 0, 0).color(r, g, b, fadeTime).endVertex();

		bufferbuilder.pos(0, ly, 0).color(r, g, b, fadeTime).endVertex();

		bufferbuilder.pos(lx, ly, 0).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(lx, 0, 0).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(lx, ly, 0).color(r, g, b, fadeTime).endVertex();

		bufferbuilder.pos(lx, ly, lz).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(lx, 0, lz).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(lx, ly, lz).color(r, g, b, fadeTime).endVertex();

		bufferbuilder.pos(0, ly, lz).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(0, 0, lz).color(r, g, b, fadeTime).endVertex();
		bufferbuilder.pos(0, ly, lz).color(r, g, b, fadeTime).endVertex();

		bufferbuilder.pos(0, ly, 0).color(r, g, b, fadeTime).endVertex();

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
}
