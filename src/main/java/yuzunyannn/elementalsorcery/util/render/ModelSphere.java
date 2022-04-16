package yuzunyannn.elementalsorcery.util.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSphere {

	static public final float PI = (float) Math.PI;

	private int displayList;
	private boolean compiled;

	public int uStepNum = 100;
	public int vStepNum = 100;

	public Vec3d getPoint(float u, float v) {
		double x = MathHelper.sin(PI * v) * MathHelper.cos(2 * PI * u);
		double z = MathHelper.sin(PI * v) * MathHelper.sin(2 * PI * u);
		double y = MathHelper.cos(PI * v);
		return new Vec3d(x, y, z);
	}

	public void render() {
		if (!compiled) compileDisplayList();
		GlStateManager.callList(this.displayList);
	}

	private void add(BufferBuilder bufferbuilder, Vec3d[] coords, Vec3d[] points, int n) {
		Vec3d normal = points[n].normalize();
		bufferbuilder.pos(points[n].x, points[n].y, points[n].z).tex(coords[n].x, coords[n].y);
		bufferbuilder.normal((float) normal.x, (float) normal.y, (float) normal.z).endVertex();
	}

	public void buildBuff(BufferBuilder bufferbuilder) {
		float uStep = 1 / (float) uStepNum, vStep = 1 / (float) vStepNum;

		Vec3d[] points = new Vec3d[2 + (vStepNum - 1) * (uStepNum + 1)];
		Vec3d[] coords = new Vec3d[points.length];

		points[0] = getPoint(0, 0);
		coords[0] = new Vec3d(0.5, 1, 0);
		int index = 1;
		for (int i = 1; i < vStepNum; i++) {
			for (int j = 0; j <= uStepNum; j++) {
				points[index] = getPoint(uStep * j, vStep * i);
				coords[index] = new Vec3d(1 - uStep * j, vStep * i, 0);
				index++;
			}
		}
		points[index] = getPoint(1, 1);
		coords[index] = new Vec3d(0.5, 0, 0);

		for (int i = 0; i <= uStepNum; i++) {
			add(bufferbuilder, coords, points, 0);
			add(bufferbuilder, coords, points, i + 1);
			add(bufferbuilder, coords, points, 1 + (i + 1) % (uStepNum + 1));
		}

		for (int i = 1; i < vStepNum - 1; i++) {
			int start = 1 + (i - 1) * (uStepNum + 1);
			for (int j = start; j < start + (1 + uStepNum); j++) {
				add(bufferbuilder, coords, points, j);
				add(bufferbuilder, coords, points, j + (1 + uStepNum));
				add(bufferbuilder, coords, points, start + (1 + uStepNum) + (j + 1 - start) % (1 + uStepNum));
				add(bufferbuilder, coords, points, j);
				add(bufferbuilder, coords, points, start + (j + 1 - start) % (1 + uStepNum));
				add(bufferbuilder, coords, points, start + uStepNum + 1 + (j + 1 - start) % (1 + uStepNum));
			}
		}
		int last = 1 + (uStepNum + 1) * (vStepNum - 1);
		int start = 1 + (uStepNum + 1) * (vStepNum - 2);
		for (int i = uStepNum * (vStepNum - 1) - 1; i < 1 + (uStepNum + 1) * (vStepNum - 1); i++) {
			add(bufferbuilder, coords, points, i);
			add(bufferbuilder, coords, points, last);
			add(bufferbuilder, coords, points, start + ((1 + i) - start) % (1 + uStepNum));
		}
	}

	private void compileDisplayList() {
		this.displayList = GLAllocation.generateDisplayLists(1);
		GlStateManager.glNewList(this.displayList, 4864);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
		buildBuff(bufferbuilder);
		tessellator.draw();

		GlStateManager.glEndList();
		this.compiled = true;
	}
}
