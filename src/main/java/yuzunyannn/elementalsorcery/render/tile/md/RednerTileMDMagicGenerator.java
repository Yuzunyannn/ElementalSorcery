package yuzunyannn.elementalsorcery.render.tile.md;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicGen;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

public class RednerTileMDMagicGenerator extends RednerTileMDBase<TileMDMagicGen> {

	@Override
	public void render(TileMDMagicGen tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderHelper.render(stack, TEXTURE, MODEL_BASE, false);
	}

}
