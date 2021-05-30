package yuzunyannn.elementalsorcery.util.render;

import java.io.IOException;

import org.lwjgl.opengl.GL20;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.util.IOHelper;
import yuzunyannn.elementalsorcery.util.text.TextHelper;

public class Shader {

	private int program;
	private ResourceLocation shaderPath;

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

		String info = GL20.glGetShaderInfoLog(shaderId, 256);
		if (!info.isEmpty()) throw new RuntimeException(info);

		int program = GL20.glCreateProgram();
		GL20.glAttachShader(program, shaderId);
		GL20.glLinkProgram(program);

		GL20.glDeleteShader(shaderId);

//		GL13.glActiveTexture(program);

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

	public void bind() {
		OpenGlHelper.glUseProgram(program);
	}

	public void unbind() {
		OpenGlHelper.glUseProgram(0);
	}

	public void setUniform(String name, float n) {
		int local = GL20.glGetUniformLocation(program, name);
		if (local == -1) return;
		GL20.glUniform1f(local, n);
	}

	public void setUniform(String name, double n) {
		setUniform(name, (float) n);
	}

}
