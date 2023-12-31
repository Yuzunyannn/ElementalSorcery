package yuzunyannn.elementalsorcery.nodegui;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.RenderRect;
import yuzunyannn.elementalsorcery.api.util.client.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
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
		this.frame = frame == null ? RenderTexutreFrame.FULL_FRAME : frame;
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

		double cw, ch, cx, cy;
		double tcw, tch, tcx, tcy;

		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

		// left top
		cx = -width * anchorX;
		cy = -height * anchorY;

		cw = split9Rect.left * frame.width * frame.texWidth;
		ch = split9Rect.top * frame.height * frame.texHeight;
		tcx = frame.x;
		tcy = frame.y;
		tcw = split9Rect.left * frame.width;
		tch = split9Rect.top * frame.height;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// left
		cy = cy + ch;
		ch = height - ch * 2;
		tcy = tcy + tch;
		tch = (split9Rect.bottom - split9Rect.top) * frame.height;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// left bottom
		cy = cy + ch;
		ch = (height - ch) / 2;
		tcy = tcy + tch;
		tch = (1 - split9Rect.bottom) * frame.height;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// bottom
		cx = cx + cw;
		cw = width - cw * 2;
		tcx = tcx + tcw;
		tcw = (split9Rect.right - split9Rect.left) * frame.width;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// right bottom
		cx = cx + cw;
		cw = (width - cw) / 2;
		tcx = tcx + tcw;
		tcw = (1 - split9Rect.right) * frame.width;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// right
		ch = height - ch * 2;
		cy = cy - ch;
		tch = (split9Rect.bottom - split9Rect.top) * frame.height;
		tcy = tcy - tch;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// right top
		ch = (height - ch) / 2;
		cy = cy - ch;
		tch = split9Rect.top * frame.height;
		tcy = tcy - tch;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// top
		cw = width - cw * 2;
		cx = cx - cw;
		tcw = (split9Rect.right - split9Rect.left) * frame.width;
		tcx = tcx - tcw;
		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// center
		cy = cy + ch;
		ch = height - ch * 2;
		tcy = tcy + tch;
		tch = (split9Rect.bottom - split9Rect.top) * frame.height;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		tessellator.draw();
	}

	@Override
	protected void render(BufferBuilder bufferbuilder, float partialTicks) {
		double aw = width * anchorX;
		double bw = width - aw;
		double ah = height * anchorY;
		double bh = height - ah;

		if (hasRotation) {
			Vec3d vec1 = new Vec3d(rX - aw, rY + ah, rZ);
			Vec3d vec2 = new Vec3d(rX + bw, rY + ah, rZ);
			Vec3d vec3 = new Vec3d(rX + bw, rY - bh, rZ);
			Vec3d vec4 = new Vec3d(rX - aw, rY - bh, rZ);

			vec1 = MathSupporter.rotation(vec1, AXIS_Z, rotationZ);
			vec2 = MathSupporter.rotation(vec2, AXIS_Z, rotationZ);
			vec3 = MathSupporter.rotation(vec3, AXIS_Z, rotationZ);
			vec4 = MathSupporter.rotation(vec4, AXIS_Z, rotationZ);

			bufferbuilder.pos(vec1.x, vec1.y, vec1.z).tex(frame.x, frame.y + frame.height).endVertex();
			bufferbuilder.pos(vec2.x, vec2.y, vec2.z).tex(frame.x + frame.width, frame.y + frame.height).endVertex();
			bufferbuilder.pos(vec3.x, vec3.y, vec3.z).tex(frame.x + frame.width, frame.y).endVertex();
			bufferbuilder.pos(vec4.x, vec4.y, vec4.z).tex(frame.x, frame.y).endVertex();
		} else {
			bufferbuilder.pos(rX - aw, rY + ah, rZ).tex(frame.x, frame.y + frame.height).endVertex();
			bufferbuilder.pos(rX + bw, rY + ah, rZ).tex(frame.x + frame.width, frame.y + frame.height).endVertex();
			bufferbuilder.pos(rX + bw, rY - bh, rZ).tex(frame.x + frame.width, frame.y).endVertex();
			bufferbuilder.pos(rX - aw, rY - bh, rZ).tex(frame.x, frame.y).endVertex();
		}
	}

}
