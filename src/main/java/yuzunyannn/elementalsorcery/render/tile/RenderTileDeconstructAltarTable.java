package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelDeconstructAltarTable;
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructAltarTable;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderTileDeconstructAltarTable extends TileEntitySpecialRenderer<TileDeconstructAltarTable>
		implements IRenderItem {

	static public final TextureBinder TEXTURE_NORMAL = new TextureBinder("textures/blocks/deconstruct_altar_table.png");
	static public final TextureBinder TEXTURE_ADV = new TextureBinder(
			"textures/blocks/deconstruct_altar_table_adv.png");
	static public final ModelDeconstructAltarTable MODEL = new ModelDeconstructAltarTable();

	final TextureBinder texture;

	public RenderTileDeconstructAltarTable(boolean isAdv) {
		if (isAdv) texture = TEXTURE_ADV;
		else texture = TEXTURE_NORMAL;
	}

	@Override
	public void render(TileDeconstructAltarTable tile, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {
		RenderHelper.bindDestoryTexture(texture, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);

		ItemStack stack = tile.getStack();
		if (stack.isEmpty()) return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
		yuzunyannn.elementalsorcery.util.render.RenderHelper.layItemPositionFix(stack);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderHelper.render(stack, texture, MODEL, false);
	}

}
