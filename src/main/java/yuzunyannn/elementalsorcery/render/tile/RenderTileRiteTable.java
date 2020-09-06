package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelRiteTable;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderTileRiteTable extends TileEntitySpecialRenderer<TileRiteTable> implements IRenderItem {

	public static final TextureBinder[] TEXTURE = new TextureBinder[] {
			new TextureBinder("textures/blocks/rite_table.png"), new TextureBinder("textures/blocks/rite_table1.png"),
			new TextureBinder("textures/blocks/rite_table2.png"), new TextureBinder("textures/blocks/rite_table3.png"),
			new TextureBinder("textures/blocks/rite_table4.png"),
			new TextureBinder("textures/blocks/rite_table5.png") };
	private static final ModelRiteTable MODEL = new ModelRiteTable();

	@Override
	public void render(TileRiteTable tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		int lev = tile.getLevel();
		lev = TileRiteTable.pLevel(lev);
		if (lev >= TEXTURE.length) lev = TEXTURE.length - 1;
		RenderHelper.bindDestoryTexture(TEXTURE[lev], destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);

		ItemStackHandler inv = tile.getInventory();
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5f, y, z + 0.5f);
			GlStateManager.scale(0.6f, 0.6f, 0.6f);
			float ox = 0, oy = 0;
			ox = MathHelper.cos(i * 1.92f) * 0.6f;
			oy = MathHelper.sin(i * 2.8f) * 0.6f;
			GlStateManager.translate(ox, 0.67f, oy);
			yuzunyannn.elementalsorcery.util.render.RenderHelper.layItemPositionFix(stack);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		int lev = 0;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) {
			lev = nbt.getInteger("level");
			lev = TileRiteTable.pLevel(lev);
			if (lev >= TEXTURE.length) lev = TEXTURE.length - 1;
		}
		RenderHelper.render(stack, TEXTURE[lev], MODEL, false, 0.038, 0.0175, 0.2f, 0);
	}

}
