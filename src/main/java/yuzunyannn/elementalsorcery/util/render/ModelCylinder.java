package yuzunyannn.elementalsorcery.util.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCylinder {

	static public final float PI = (float) Math.PI;

	private int displayList;
	private boolean compiled;

	public int stepNum = 200;
	public double height = 10;

	public void render() {
		if (!compiled) compileDisplayList();
		GlStateManager.callList(this.displayList);
	}

	public Vec3d getPoint(float u, float dy) {
		double x = Math.sin(PI * u * 2);
		double z = -Math.cos(PI * u * 2);
		double y = dy * height;
		return new Vec3d(x, y, z);
	}

	private void add(BufferBuilder bufferbuilder, Vec3d[] coords, Vec3d[] points, int n) {
		Vec3d normal = points[n].normalize();
		bufferbuilder.pos(points[n].x, points[n].y, points[n].z).tex(coords[n].x, coords[n].y);
		bufferbuilder.normal((float) normal.x, (float) normal.y, (float) normal.z).endVertex();
	}

	public void buildBuff(BufferBuilder bufferbuilder) {
		float step = 1 / (float) stepNum;

		Vec3d[] points = new Vec3d[stepNum + 2];
		Vec3d[] coords = new Vec3d[points.length];

		for (int i = 0; i < points.length; i++) {
			points[i] = getPoint(step * i, i % 2 == 0 ? 1 : -1);
			coords[i] = new Vec3d(step * i, i % 2 == 0 ? 0 : 1, 0);
		}

		for (int i = 0; i < points.length; i++) add(bufferbuilder, coords, points, i);
	}

	private void compileDisplayList() {
		this.displayList = GLAllocation.generateDisplayLists(1);
		GlStateManager.glNewList(this.displayList, 4864);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
		buildBuff(bufferbuilder);
		tessellator.draw();

		GlStateManager.glEndList();
		this.compiled = true;
	}
}
