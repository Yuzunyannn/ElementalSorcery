package yuzunyannn.elementalsorcery.render.effect.grimoire;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public class EffectBlockLine extends Effect {

	public TextureBinder texture;
	public Color color = new Color();
	public EnumFacing toFacing = EnumFacing.NORTH;
	public double width;
	public double distance;
	public double maxLength;
	public double motion;
	public boolean flip;

	protected double progress, prevProgress;

	public EffectBlockLine(World worldIn, Vec3d start) {
		super(worldIn, start.x, start.y, start.z);

	}

	@Override
	public boolean isDead() {
		return progress >= 1 || super.isDead();
	}

	@Override
	public void onUpdate() {
		prevProgress = progress;
		progress = Math.min(1, progress + motion / distance);
	}

	@Override
	protected void doRender(float partialTicks) {
		double x = getRenderX(partialTicks);
		double y = getRenderY(partialTicks);
		double z = getRenderZ(partialTicks);
		float r = color.r;
		float g = color.g;
		float b = color.b;
		final float a = 1;
		double w = this.width / 2;
		double fw = w;
		if (flip) w = 0;
		else fw = 0;

		double progress = RenderFriend.getPartialTicks(this.progress, this.prevProgress, partialTicks);
		double distance = this.distance + this.maxLength;
		double ml = this.maxLength;

		int dx = toFacing.getXOffset();
		int dz = toFacing.getZOffset();
		int dy = toFacing.getYOffset();

		double atLoc = progress * distance;

		if (texture == null) GlStateManager.disableTexture2D();
		else texture.bind();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		if (dx != 0) {
			double l = dx * Math.min(distance - atLoc, Math.min(atLoc, ml));
			if (distance - atLoc > ml) x = x + dx * atLoc - l;
			else x = x + dx * (distance - ml) - l;
			bufferbuilder.pos(x, y - fw, z - w).tex(0, 0).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x + l, y - fw, z - w).tex(0, 1).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x + l, y + fw, z + w).tex(1, 1).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x, y + fw, z + w).tex(1, 0).color(r, g, b, a).endVertex();
		} else if (dz != 0) {
			double l = dz * Math.min(distance - atLoc, Math.min(atLoc, ml));
			if (distance - atLoc > ml) z = z + dz * atLoc - l;
			else z = z + dz * (distance - ml) - l;
			bufferbuilder.pos(x - w, y - fw, z).tex(0, 0).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x - w, y - fw, z + l).tex(0, 1).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x + w, y + fw, z + l).tex(1, 1).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x + w, y + fw, z).tex(1, 0).color(r, g, b, a).endVertex();
		} else if (dy != 0) {
			double l = dy * Math.min(distance - atLoc, Math.min(atLoc, ml));
			if (distance - atLoc > ml) y = y + dy * atLoc - l;
			else y = y + dy * (distance - ml) - l;
			bufferbuilder.pos(x - w, y, z - fw).tex(0, 0).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x - w, y + l, z - fw).tex(0, 1).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x + w, y + l, z + fw).tex(1, 1).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x + w, y, z + fw).tex(1, 0).color(r, g, b, a).endVertex();
		}

		tessellator.draw();

		if (texture == null) GlStateManager.enableTexture2D();
	}
}
