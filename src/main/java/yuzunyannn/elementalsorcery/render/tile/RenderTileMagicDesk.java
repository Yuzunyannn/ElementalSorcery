package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.render.item.SpellbookRenderInfo;
import yuzunyannn.elementalsorcery.render.model.ModelMagicDesk;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;

@SideOnly(Side.CLIENT)
public class RenderTileMagicDesk extends TileEntitySpecialRenderer<TileMagicDesk> implements IRenderItem {

	private final ModelMagicDesk MODEL = new ModelMagicDesk();
	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/magic_desk.png");

	@Override
	public void render(TileMagicDesk tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y + 1.2, z + 0.5, 0.1, alpha);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);

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
		if (spellbook == null) {
			GlStateManager.translate(0, 0, 0.1);
			RenderFriend.layItemPositionFix(book);
			Minecraft.getMinecraft().getRenderItem().renderItem(book, ItemCameraTransforms.TransformType.FIXED);
			return;
		}
		SpellbookRenderInfo info = spellbook.renderInfo;
		float n_rate = (1 - info.bookSpread);
		GlStateManager.translate(0, 0.45 * info.bookSpread + 0.35 * n_rate, 0.35 * n_rate);
		GlStateManager.rotate(n_rate * 90, -1, 0, 0);
		Minecraft.getMinecraft().getRenderItem().renderItem(book, ItemCameraTransforms.TransformType.GROUND);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		if (IRenderItem.isTransform(stack, ItemCameraTransforms.TransformType.FIXED)) {
			TEXTURE.bind();
			GlStateManager.translate(0.5, 0.85, 0.5);
			GlStateManager.scale(0.025 * 2, 0.025 * 2, 0.025 * 2);
			MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		} else
			if (IRenderItem.isTransform(stack, ItemCameraTransforms.TransformType.GROUND) || IRenderItem.isGUI(stack)) {
				RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, true, 0.05, 0.025, 0.46, 0.3);
			} else {
				TEXTURE.bind();
				GlStateManager.translate(0.5, 0.7, 0.75);
				GlStateManager.rotate(30, 1, 0, 0);
				GlStateManager.scale(0.025, 0.025, 0.025);
				MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
			}
	}
}
