package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.render.model.ModelElementCraftingTable;
import yuzunyannn.elementalsorcery.tile.altar.TileElementCraftingTable;

@SideOnly(Side.CLIENT)
public class RenderTileElementCraftingTable extends TileEntitySpecialRenderer<TileElementCraftingTable>
		implements IRenderItem {

	private final ModelElementCraftingTable MODEL = new ModelElementCraftingTable();
	static public final TextureBinder TEXTURE = new TextureBinder("textures/blocks/element_crafting_table.png");

	@Override
	public void render(TileElementCraftingTable tile, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.1, alpha);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, true, 0.05, 0.025, 0, 0);
	}

}
