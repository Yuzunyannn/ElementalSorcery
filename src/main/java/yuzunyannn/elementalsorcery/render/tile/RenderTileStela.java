package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelStela;
import yuzunyannn.elementalsorcery.tile.TileStela;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderTileStela extends TileEntitySpecialRenderer<TileStela> implements IRenderItem {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/stela.png");
	private final ModelStela MODEL = new ModelStela();

	@Override
	public void render(TileStela tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		RenderHelper.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		this.raoteWithFace(tile.getFace());
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);

		ItemStack stack = tile
				.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, tile.getFace().getOpposite())
				.getStackInSlot(0);
		if (!stack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 0.125, z + 0.5);
			this.raoteWithFace(tile.getFace());
			GlStateManager.translate(0.25, 0, 0.275);
			yuzunyannn.elementalsorcery.util.render.RenderHelper.layItemPositionFix(stack);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
			GlStateManager.popMatrix();
		}

		stack = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, tile.getFace()).getStackInSlot(0);
		if (!stack.isEmpty()) {
			this.renderOncePaper(tile, x + 0.5, y - 0.135, z + 0.5, stack, 0);
			if (stack.getCount() >= 4) {
				this.renderOncePaper(tile, x + 0.5, y - 0.135 + 0.032, z + 0.5, stack, 0.04);
				if (stack.getCount() >= 8) {
					this.renderOncePaper(tile, x + 0.5, y - 0.135 + 0.032 * 2, z + 0.5, stack, -0.04);
					if (stack.getCount() >= 16) {
						this.renderOncePaper(tile, x + 0.5, y - 0.135 + 0.032 * 3, z + 0.5, stack, 0.02);
						if (stack.getCount() >= 32) {
							this.renderOncePaper(tile, x + 0.5, y - 0.135 + 0.032 * 4, z + 0.5, stack, -0.025);
						}
					}
				}
			}
		}
	}

	private void renderOncePaper(TileStela tile, double x, double y, double z, ItemStack stack, double detal) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		this.raoteWithFace(tile.getFace());
		GlStateManager.translate(-0.175 + detal, 0, -0.225 + detal);
		yuzunyannn.elementalsorcery.util.render.RenderHelper.layItemPositionFix(stack);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.popMatrix();
	}

	private void raoteWithFace(EnumFacing face) {
		switch (face) {
		case NORTH:
			GlStateManager.rotate(-90, 0, 1, 0);
			break;
		case EAST:
			GlStateManager.rotate(180, 0, 1, 0);
			break;
		case WEST:
			break;
		case SOUTH:
			GlStateManager.rotate(90, 0, 1, 0);
			break;
		default:
			break;
		}
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderHelper.render(stack, TEXTURE, MODEL, false);
	}

}
