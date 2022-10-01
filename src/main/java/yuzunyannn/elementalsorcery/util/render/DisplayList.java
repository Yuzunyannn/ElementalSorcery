package yuzunyannn.elementalsorcery.util.render;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;

public abstract class DisplayList {

	private int displayList;
	private boolean compiled;

	public void render() {
		if (!compiled) compileDisplayList();
		GlStateManager.callList(this.displayList);
	}

	private void compileDisplayList() {
		this.displayList = GLAllocation.generateDisplayLists(1);
		GlStateManager.glNewList(this.displayList, 4864);

		doRender();

		GlStateManager.glEndList();
		this.compiled = true;
	}

	protected abstract void doRender();

}
