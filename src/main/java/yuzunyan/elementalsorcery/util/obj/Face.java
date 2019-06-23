package yuzunyan.elementalsorcery.util.obj;

import net.minecraft.client.renderer.BufferBuilder;

public class Face {
	public Vertex[] vertices;
	public Vertex[] tex_vertices;

	public void setTexVetices(Vertex t1, Vertex t2, Vertex t3) throws IllegalArgumentException {
		if (tex_vertices == null || tex_vertices.length != 3) {
			throw new IllegalArgumentException("这个面不是个三角面");
		}
		tex_vertices[0] = t1;
		tex_vertices[1] = t2;
		tex_vertices[2] = t3;
	}

	public void setTexVetices(Vertex t1, Vertex t2, Vertex t3, Vertex t4) throws IllegalArgumentException {
		if (tex_vertices == null || tex_vertices.length != 4) {
			throw new IllegalArgumentException("这个面不是个矩形面");
		}
		tex_vertices[0] = t1;
		tex_vertices[1] = t2;
		tex_vertices[2] = t3;
		tex_vertices[3] = t4;
	}

	public void toBuffer(BufferBuilder vb, float tex_xoff, float tex_yoff) {
		for (int i = 0; i < vertices.length; ++i) {
			vb.pos(vertices[i].x, vertices[i].y, vertices[i].z)
					.tex(tex_vertices[i].x + tex_xoff, tex_vertices[i].y + tex_yoff).endVertex();
		}
	}

}
