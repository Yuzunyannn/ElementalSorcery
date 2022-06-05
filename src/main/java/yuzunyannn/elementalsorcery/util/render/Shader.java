package yuzunyannn.elementalsorcery.util.render;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.IOHelper;

public class Shader {

	protected int program;
	protected ResourceLocation shaderPath;
	protected int originProgram = -1;

	public Shader(String res) throws IOException {
		this(TextHelper.toESResourceLocation(res));
	}

	public Shader(ResourceLocation res) throws IOException {
		shaderPath = res;
		this.program = load();
	}

	private int load() throws IOException {
		String data = IOHelper.readString(shaderPath);

		int shaderId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(shaderId, data);
		GL20.glCompileShader(shaderId);

		String info = GL20.glGetShaderInfoLog(shaderId, 1024);
		if (!info.isEmpty()) errorPart: {
			if (info.toLowerCase().indexOf("no error") != -1) break errorPart;
			if (info.indexOf("error") != -1) throw new RuntimeException(info);
			// ElementalSorcery.logger.warn("Shader Warnning:\n" + info);
		}

		int program = GL20.glCreateProgram();
		GL20.glAttachShader(program, shaderId);
		GL20.glLinkProgram(program);

		GL20.glDeleteShader(shaderId);

		// GL13.glActiveTexture(program);

		return program;
	}

	public void reload() throws IOException {
		int newShader = load();
		this.close();
		this.program = newShader;
	}

	public void close() {
		if (program == 0) return;
		GL20.glDeleteProgram(program);
		program = 0;
	}

	public boolean isActive() {
		return originProgram != -1;
	}

	public void bind() {
		originProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
		OpenGlHelper.glUseProgram(program);
	}

	public void unbind() {
		OpenGlHelper.glUseProgram(originProgram);
		originProgram = -1;
	}

	public void setUniform(String name, float n) {
		int local = GL20.glGetUniformLocation(program, name);
		if (local == -1) return;
		GL20.glUniform1f(local, n);
	}

	public void setUniform(String name, double n) {
		setUniform(name, (float) n);
	}

	public void setUniform(String name, int n) {
		int local = GL20.glGetUniformLocation(program, name);
		if (local == -1) return;
		GL20.glUniform1i(local, n);
	}

	public void setUniform(String name, boolean n) {
		setUniform(name, n ? 1 : 0);
	}

	public void setUniform(String name, float x, float y, float z) {
		int local = GL20.glGetUniformLocation(program, name);
		if (local == -1) return;
		GL20.glUniform3f(local, x, y, z);
	}

	public void setUniform(String name, Vec3d vec) {
		setUniform(name, (float) vec.x, (float) vec.y, (float) vec.z);
	}

	public void setUniform(String name, Color c) {
		setUniform(name, c.r, c.g, c.b);
	}

}
