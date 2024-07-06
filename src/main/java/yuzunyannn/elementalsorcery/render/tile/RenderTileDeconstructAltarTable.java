package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.render.model.ModelDeconstructAltarTable;
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructAltarTable;

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
		RenderFriend.bindDestoryTexture(texture, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
		RenderFriend.renderItemLayout(tile.getStack(), x + 0.5, y + 0.5, z + 0.5);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderFriend.renderSpecialItem(stack, texture, MODEL, true);
	}

}
