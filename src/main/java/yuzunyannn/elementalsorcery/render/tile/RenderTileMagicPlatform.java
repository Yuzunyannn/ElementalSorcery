package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.tile.TileMagicPlatform;

@SideOnly(Side.CLIENT)
public class RenderTileMagicPlatform extends TileEntitySpecialRenderer<TileMagicPlatform> {

	@Override
	public void render(TileMagicPlatform tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		RenderFriend.renderItemLayout(tile.getStack(), x + 0.5, y - 0.05, z + 0.5);
	}

}
