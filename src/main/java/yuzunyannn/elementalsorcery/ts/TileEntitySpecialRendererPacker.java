package yuzunyannn.elementalsorcery.ts;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TileEntitySpecialRendererPacker<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

	final public TileEntitySpecialRenderer<T> parent;

	public TileEntitySpecialRendererPacker(TileEntitySpecialRenderer<T> other) {
		this.parent = other;
	}

	@Override
	public void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		parent.render(te, x, y, z, 0, destroyStage, alpha);
	}

	@Override
	public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
		parent.setRendererDispatcher(rendererDispatcherIn);
	}

	@Override
	public FontRenderer getFontRenderer() {
		return parent.getFontRenderer();
	}

	@Override
	public boolean isGlobalRenderer(T te) {
		return parent.isGlobalRenderer(te);
	}

	@Override
	public void renderTileEntityFast(T te, double x, double y, double z, float partialTicks, int destroyStage,
			float partial, net.minecraft.client.renderer.BufferBuilder buffer) {
		parent.renderTileEntityFast(te, x, y, z, 0, destroyStage, partial, buffer);
	}

}
