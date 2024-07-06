package yuzunyannn.elementalsorcery.nodegui;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.RenderRect;
import yuzunyannn.elementalsorcery.api.util.render.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

@SideOnly(Side.CLIENT)
public class GImage extends GNode {

	protected ResourceLocation textureResource = RenderObjects.ASTONE;
	protected RenderTexutreFrame frame = RenderTexutreFrame.FULL_FRAME;
	protected RenderRect split9Rect = null;

	public GImage() {

	}

	public GImage(ResourceLocation textureResource) {
		this.setTextureResource(textureResource);
	}

	public GImage(TextureBinder textureResource) {
		this.setTextureResource(textureResource);
	}

	public GImage(ResourceLocation textureResource, RenderTexutreFrame frame) {
		this.setTextureResource(textureResource);
		this.setFrame(frame);
		this.setSize(frame.width * frame.texWidth, frame.height * frame.texHeight);
	}

	public GImage(TextureBinder textureResource, RenderTexutreFrame frame) {
		this.setTextureResource(textureResource);
		this.setFrame(frame);
		this.setSize(frame.width * frame.texWidth, frame.height * frame.texHeight);
	}

	public void setTextureResource(ResourceLocation textureResource) {
		this.textureResource = textureResource == null ? this.textureResource : textureResource;
	}

	public void setTextureResource(TextureBinder textureBinder) {
		setTextureResource(textureBinder.getResource());
	}

	public void setFrame(RenderTexutreFrame frame) {
		setFrame(frame, false);
	}

	public void setFrame(RenderTexutreFrame frame, boolean updateSize) {
		this.frame = frame == null ? RenderTexutreFrame.FULL_FRAME : frame;
		if (!updateSize) return;
		this.setSize(frame.width * frame.texWidth, frame.height * frame.texHeight);
	}

	public void setSplit9(RenderRect rect) {
		this.split9Rect = rect;
	}

	public void setSplit9() {
		this.setSplit9(RenderFriend.SPLIT9_AVERAGE_RECT);
	}

	public void bindTextre() {
		mc.getTextureManager().bindTexture(textureResource);
	}

	@Override
	protected void render(float partialTicks) {
		this.bindTextre();
		if (this.split9Rect != null) {
			this.render9(partialTicks);
			return;
		}
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		double aw = width * anchorX;
		double bw = width - aw;
		double ah = height * (1 - anchorY);
		double bh = height - ah;
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(-aw, +ah, 0.0D).tex(frame.x, frame.y + frame.height).endVertex();
		bufferbuilder.pos(+bw, +ah, 0.0D).tex(frame.x + frame.width, frame.y + frame.height).endVertex();
		bufferbuilder.pos(+bw, -bh, 0.0D).tex(frame.x + frame.width, frame.y).endVertex();
		bufferbuilder.pos(-aw, -bh, 0.0D).tex(frame.x, frame.y).endVertex();
		tessellator.draw();
	}

	protected void render9(float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		render9(bufferbuilder, false);
		tessellator.draw();
	}

	protected void pushBatch9BufferBuilder(BufferBuilder bufferbuilder, double ox, double oy, double cx, double cy,
			double cw, double ch, double tcx, double tcy, double tcw, double tch) {

		if (hasScale) {
			cx *= rScaleX;
			cy *= rScaleY;
			cw *= rScaleX;
			ch *= rScaleY;
		}

		float r = color.r, g = color.g, b = color.b, a = rAlpha;

		if (rRotationZ != 0) {
			Vec3d vec1 = new Vec3d(cx, cy, 0);
			Vec3d vec2 = new Vec3d(cx, cy + ch, 0);
			Vec3d vec3 = new Vec3d(cx + cw, cy + ch, 0);
			Vec3d vec4 = new Vec3d(cx + cw, cy, 0);

			double rotationZ = this.rRotationZ / 180 * 3.1415926;
			vec1 = MathSupporter.rotation(vec1, AXIS_Z, rotationZ);
			vec2 = MathSupporter.rotation(vec2, AXIS_Z, rotationZ);
			vec3 = MathSupporter.rotation(vec3, AXIS_Z, rotationZ);
			vec4 = MathSupporter.rotation(vec4, AXIS_Z, rotationZ);

			bufferbuilder.pos(ox + vec1.x, oy + vec1.y, rZ).tex(tcx, tcy).color(r, g, b, a).endVertex();
			bufferbuilder.pos(ox + vec2.x, oy + vec2.y, rZ).tex(tcx, tcy + tch).color(r, g, b, a).endVertex();
			bufferbuilder.pos(ox + vec3.x, oy + vec3.y, rZ).tex(tcx + tcw, tcy + tch).color(r, g, b, a).endVertex();
			bufferbuilder.pos(ox + vec4.x, oy + vec4.y, rZ).tex(tcx + tcw, tcy).color(r, g, b, a).endVertex();
		} else {
			bufferbuilder.pos(ox + cx, oy + cy, rZ).tex(tcx, tcy).color(r, g, b, a).endVertex();
			bufferbuilder.pos(ox + cx, oy + cy + ch, rZ).tex(tcx, tcy + tch).color(r, g, b, a).endVertex();
			bufferbuilder.pos(ox + cx + cw, oy + cy + ch, rZ).tex(tcx + tcw, tcy + tch).color(r, g, b, a).endVertex();
			bufferbuilder.pos(ox + cx + cw, oy + cy, rZ).tex(tcx + tcw, tcy).color(r, g, b, a).endVertex();
		}
	}

	protected void render9(BufferBuilder bufferbuilder, boolean isBatch) {

		double cw, ch, cx, cy;
		double tcw, tch, tcx, tcy;
		double ox, oy;
		double width = this.width;
		double height = this.height;

		if (isBatch) {
			ox = rX;
			oy = rY;
		} else ox = oy = 0;

		// left top
		cx = -width * anchorX;
		cy = -height * anchorY;

		cw = split9Rect.left * frame.width * frame.texWidth;
		ch = split9Rect.top * frame.height * frame.texHeight;
		tcx = frame.x;
		tcy = frame.y;
		tcw = split9Rect.left * frame.width;
		tch = split9Rect.top * frame.height;

		if (isBatch) pushBatch9BufferBuilder(bufferbuilder, ox, oy, cx, cy, cw, ch, tcx, tcy, tcw, tch);
		else {
			bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
			bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();
		}

		// left
		cy = cy + ch;
		ch = height - ch * 2;
		tcy = tcy + tch;
		tch = (split9Rect.bottom - split9Rect.top) * frame.height;

		if (isBatch) pushBatch9BufferBuilder(bufferbuilder, ox, oy, cx, cy, cw, ch, tcx, tcy, tcw, tch);
		else {
			bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
			bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();
		}

		// left bottom
		cy = cy + ch;
		ch = (height - ch) / 2;
		tcy = tcy + tch;
		tch = (1 - split9Rect.bottom) * frame.height;

		if (isBatch) pushBatch9BufferBuilder(bufferbuilder, ox, oy, cx, cy, cw, ch, tcx, tcy, tcw, tch);
		else {
			bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
			bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();
		}

		// bottom
		cx = cx + cw;
		cw = width - cw * 2;
		tcx = tcx + tcw;
		tcw = (split9Rect.right - split9Rect.left) * frame.width;

		if (isBatch) pushBatch9BufferBuilder(bufferbuilder, ox, oy, cx, cy, cw, ch, tcx, tcy, tcw, tch);
		else {
			bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
			bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();
		}

		// right bottom
		cx = cx + cw;
		cw = (width - cw) / 2;
		tcx = tcx + tcw;
		tcw = (1 - split9Rect.right) * frame.width;

		if (isBatch) pushBatch9BufferBuilder(bufferbuilder, ox, oy, cx, cy, cw, ch, tcx, tcy, tcw, tch);
		else {
			bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
			bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();
		}

		// right
		ch = height - ch * 2;
		cy = cy - ch;
		tch = (split9Rect.bottom - split9Rect.top) * frame.height;
		tcy = tcy - tch;

		if (isBatch) pushBatch9BufferBuilder(bufferbuilder, ox, oy, cx, cy, cw, ch, tcx, tcy, tcw, tch);
		else {
			bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
			bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();
		}

		// right top
		ch = (height - ch) / 2;
		cy = cy - ch;
		tch = split9Rect.top * frame.height;
		tcy = tcy - tch;

		if (isBatch) pushBatch9BufferBuilder(bufferbuilder, ox, oy, cx, cy, cw, ch, tcx, tcy, tcw, tch);
		else {
			bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
			bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();
		}

		// top
		cw = width - cw * 2;
		cx = cx - cw;
		tcw = (split9Rect.right - split9Rect.left) * frame.width;
		tcx = tcx - tcw;
		if (isBatch) pushBatch9BufferBuilder(bufferbuilder, ox, oy, cx, cy, cw, ch, tcx, tcy, tcw, tch);
		else {
			bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
			bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();
		}

		// center
		cy = cy + ch;
		ch = height - ch * 2;
		tcy = tcy + tch;
		tch = (split9Rect.bottom - split9Rect.top) * frame.height;

		if (isBatch) pushBatch9BufferBuilder(bufferbuilder, ox, oy, cx, cy, cw, ch, tcx, tcy, tcw, tch);
		else {
			bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
			bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
			bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();
		}
	}

	@Override
	protected void render(BufferBuilder bufferbuilder, float partialTicks) {
		if (this.split9Rect != null) {
			this.render9(bufferbuilder, true);
			return;
		}

		double aw = width * anchorX;
		double bw = width - aw;
		double ah = height * (1 - anchorY);
		double bh = height - ah;
		float r = color.r, g = color.g, b = color.b, a = rAlpha;

		if (hasScale) {
			aw = aw * rScaleX;
			bw = bw * rScaleX;
			ah = ah * rScaleY;
			bh = bh * rScaleY;
		}

		if (rRotationZ != 0) {
			Vec3d vec1 = new Vec3d(-aw, +ah, rZ);
			Vec3d vec2 = new Vec3d(+bw, +ah, rZ);
			Vec3d vec3 = new Vec3d(+bw, -bh, rZ);
			Vec3d vec4 = new Vec3d(-aw, -bh, rZ);

			double rotationZ = this.rRotationZ / 180 * 3.1415926;
			vec1 = MathSupporter.rotation(vec1, AXIS_Z, rotationZ);
			vec2 = MathSupporter.rotation(vec2, AXIS_Z, rotationZ);
			vec3 = MathSupporter.rotation(vec3, AXIS_Z, rotationZ);
			vec4 = MathSupporter.rotation(vec4, AXIS_Z, rotationZ);

			bufferbuilder.pos(rX + vec1.x, rY + vec1.y, vec1.z).tex(frame.x, frame.y + frame.height).color(r, g, b, a).endVertex();
			bufferbuilder.pos(rX + vec2.x, rY + vec2.y, vec2.z).tex(frame.x + frame.width, frame.y + frame.height).color(r, g, b, a).endVertex();
			bufferbuilder.pos(rX + vec3.x, rY + vec3.y, vec3.z).tex(frame.x + frame.width, frame.y).color(r, g, b, a).endVertex();
			bufferbuilder.pos(rX + vec4.x, rY + vec4.y, vec4.z).tex(frame.x, frame.y).color(r, g, b, a).endVertex();
		} else {
			bufferbuilder.pos(rX - aw, rY + ah, rZ).tex(frame.x, frame.y + frame.height).color(r, g, b, a).endVertex();
			bufferbuilder.pos(rX + bw, rY + ah, rZ).tex(frame.x + frame.width, frame.y + frame.height).color(r, g, b, a).endVertex();
			bufferbuilder.pos(rX + bw, rY - bh, rZ).tex(frame.x + frame.width, frame.y).color(r, g, b, a).endVertex();
			bufferbuilder.pos(rX - aw, rY - bh, rZ).tex(frame.x, frame.y).color(r, g, b, a).endVertex();
		}
	}

}
