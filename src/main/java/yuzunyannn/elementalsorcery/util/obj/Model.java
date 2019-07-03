package yuzunyannn.elementalsorcery.util.obj;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Model {
	private ArrayList<Face> faces_quads;
	private ArrayList<Face> faces_triangles;

	// 三角形的顶点
	public Face addFace(Vertex v1, Vertex v2, Vertex v3) {
		if(faces_triangles==null)faces_triangles = new ArrayList<Face>();
		Face face = new Face();
		face.vertices = new Vertex[3];
		face.tex_vertices = new Vertex[3];
		face.vertices[0] = v1;
		face.vertices[1] = v2;
		face.vertices[2] = v3;
		faces_triangles.add(face);
		return face;
	}

	// 正方形的顶点
	public Face addFace(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
		if(faces_quads==null)faces_quads = new ArrayList<Face>();
		Face face = new Face();
		face.vertices = new Vertex[4];
		face.tex_vertices = new Vertex[4];
		face.vertices[0] = v1;
		face.vertices[1] = v2;
		face.vertices[2] = v3;
		face.vertices[3] = v4;
		faces_quads.add(face);
		return face;
	}

	@SideOnly(Side.CLIENT)
	public void render() {
		BufferBuilder vb = Tessellator.getInstance().getBuffer();
		if (faces_quads != null && faces_quads.size() > 0) {
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			for (Face face : faces_quads) {
				face.toBuffer(vb, 0, 0);
			}
			Tessellator.getInstance().draw();
		}
		if (faces_triangles != null && faces_triangles.size() > 0) {
			vb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
			for (Face face : faces_triangles) {
				face.toBuffer(vb, 0, 0);
			}
			Tessellator.getInstance().draw();
		}
	}
}
