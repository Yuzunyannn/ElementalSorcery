package yuzunyan.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import yuzunyan.elementalsorcery.capability.Spellbook;
import yuzunyan.elementalsorcery.render.IRenderItem;
import yuzunyan.elementalsorcery.render.item.RenderItemSpellbook;
import yuzunyan.elementalsorcery.render.model.ModelMagicDesk;
import yuzunyan.elementalsorcery.tile.TileMagicDesk;
import yuzunyan.elementalsorcery.util.render.TextureBinder;

public class RenderTileMagicDesk extends TileEntitySpecialRenderer<TileMagicDesk> implements IRenderItem {

	private final ModelMagicDesk MODEL = new ModelMagicDesk();
	public final TextureBinder TEXTURE = new TextureBinder("textures/blocks/magic_desk.png");

	@Override
	public void render(TileMagicDesk tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.enableCull();
		GlStateManager.translate(x + 0.5, y + 1.2, z + 0.5);
		TEXTURE.bind();
		GlStateManager.scale(0.1, 0.1, 0.1);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		GlStateManager.popMatrix();

		ItemStack book = tile.getBook();
		if (!book.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 1.2, z + 0.4);
			this.renderBook(book);
			GlStateManager.popMatrix();
		}

	}

	public void renderBook(ItemStack book) {
		Spellbook spellbook = book.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		if (spellbook == null)
			return;
		RenderItemSpellbook.RenderInfo info = spellbook.render_info;
		float n_rate = (1 - info.bookSpread);
		GlStateManager.translate(0, 0.45 * info.bookSpread + 0.375 * n_rate, 0.35 * n_rate);
		GlStateManager.rotate(n_rate * 90, -1, 0, 0);
		Minecraft.getMinecraft().getRenderItem().renderItem(book, ItemCameraTransforms.TransformType.GROUND);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.enableCull();
		if (IRenderItem.isGUI(stack)) {
			GlStateManager.translate(0.5, 0.65, 0.5);
			GlStateManager.scale(0.05, 0.05, 0.05);
			GlStateManager.rotate(45, 0, 1, 0);
			GlStateManager.rotate(30, 1, 0, 1);
			// GlStateManager.rotate(EventClient.global_rotate +
			// EventClient.DGLOBAL_ROTATE * partialTicks, 0, 1, 0);
		} else {
			GlStateManager.translate(0.5, 0.7, 0.5);
			GlStateManager.scale(0.025, 0.025, 0.025);
		}
		TEXTURE.bind();
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		GlStateManager.popMatrix();
	}
}
