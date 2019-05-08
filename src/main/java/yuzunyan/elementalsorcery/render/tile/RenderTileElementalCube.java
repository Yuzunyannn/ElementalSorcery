package yuzunyan.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.api.ability.IElementInventory;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.capability.ElementInventory;
import yuzunyan.elementalsorcery.render.IRenderItem;
import yuzunyan.elementalsorcery.tile.TileElementalCube;
import yuzunyan.elementalsorcery.util.obj.Model;
import yuzunyan.elementalsorcery.util.obj.Vertex;
import yuzunyan.elementalsorcery.util.render.TextureBinder;
@SideOnly(Side.CLIENT)
public class RenderTileElementalCube extends TileEntitySpecialRenderer<TileElementalCube> implements IRenderItem {

	private TextureBinder TEXTURE = new TextureBinder("textures/blocks/elemental_cube.png");
	private TextureBinder TEXTURE_OVER = new TextureBinder("textures/blocks/elemental_cube_cover.png");
	private Model MODEL = new Model();
	final public TileEntityItemStackRenderer itemRender = new TileEntityItemStackRenderer() {
		@Override
		public void renderByItem(ItemStack stack, float partialTicks) {
			RenderTileElementalCube.this.render(stack, partialTicks);
		};
	};

	public RenderTileElementalCube() {
		init();
	}

	@Override
	public void render(TileElementalCube tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {

		GlStateManager.pushMatrix();
		RenderHelper.disableStandardItemLighting();

		GlStateManager.translate(x, y, z);

		float rotate = tile.rotate + tile.SOTATE_PRE_TICK * partialTicks;
		float wake = tile.wake > 0 ? tile.wake_up + TileElementalCube.WAKE_UP_RARE * partialTicks
				: tile.wake_up - TileElementalCube.WAKE_UP_RARE * partialTicks;
		wake = MathHelper.clamp(wake, 0, 1);
		GlStateManager.translate(0.5F, tile.getHigh(rotate, wake), 0.5F);
		GlStateManager.scale(0.5F, 0.5F, 0.5F);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		GlStateManager.rotate(tile.getRoate((float) (rotate * 180.0f / Math.PI), wake), 1, 1, 1);
		TEXTURE.bind();
		MODEL.render();
		GlStateManager.enableAlpha();
		GlStateManager.color(tile.color.x * tile.color_rate + (1.0F - tile.color_rate) * tile.ORIGIN_COLOR.x,
				tile.color.y * tile.color_rate + (1.0F - tile.color_rate) * tile.ORIGIN_COLOR.y,
				tile.color.z * tile.color_rate + (1.0F - tile.color_rate) * tile.ORIGIN_COLOR.z);
		TEXTURE_OVER.bind();
		MODEL.render();
		GlStateManager.disableAlpha();

		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		RenderHelper.disableStandardItemLighting();

		GlStateManager.color(1.0F, 1.0F, 1.0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);

		if (stack.isOnItemFrame()) {
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
		} else if (IRenderItem.isGUI(stack)) {
			GlStateManager.rotate(45.0F, 1, 1, 1);
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
		} else {
			GlStateManager.scale(0.25F, 0.25F, 0.25F);
			GlStateManager.translate(0.0F, 0.1F, 0.0F);
		}
		TEXTURE.bind();
		MODEL.render();

		IElementInventory inventory = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		if (inventory != null) {
			ElementStack estack = inventory.getStackInSlot(0);
			if (!estack.isEmpty()) {
				Vertex color = new Vertex().toColor(estack.getColor());
				GlStateManager.color(color.x, color.y, color.z);
				TEXTURE_OVER.bind();
				MODEL.render();
			}

		}

		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();

	}

	private void init() {
		// ��
		MODEL.addFace(new Vertex(-0.5F, -0.5F, -0.5F), new Vertex(0.5F, -0.5F, -0.5F), new Vertex(0.5F, -0.5F, 0.5F),
				new Vertex(-0.5F, -0.5F, 0.5F)).setTexVetices(new Vertex(0.0F, 0.0F), new Vertex(1.0F, 0.0F),
						new Vertex(1.0F, 1.0F), new Vertex(0.0F, 1.0F));
		// ��
		MODEL.addFace(new Vertex(-0.5F, 0.5F, -0.5F), new Vertex(-0.5F, 0.5F, 0.5F), new Vertex(0.5F, 0.5F, 0.5F),
				new Vertex(0.5F, 0.5F, -0.5F)).setTexVetices(new Vertex(0.0F, 0.0F), new Vertex(0.0F, 1.0F),
						new Vertex(1.0F, 1.0F), new Vertex(1.0F, 0.0F));
		// ��
		MODEL.addFace(new Vertex(-0.5F, -0.5F, -0.5F), new Vertex(-0.5F, -0.5F, 0.5F), new Vertex(-0.5F, 0.5F, 0.5F),
				new Vertex(-0.5F, 0.5F, -0.5F)).setTexVetices(new Vertex(0.0F, 0.0F), new Vertex(0.0F, 1.0F),
						new Vertex(1.0F, 1.0F), new Vertex(1.0F, 0.0F));
		// ��
		MODEL.addFace(new Vertex(0.5F, -0.5F, -0.5F), new Vertex(0.5F, 0.5F, -0.5F), new Vertex(0.5F, 0.5F, 0.5F),
				new Vertex(0.5F, -0.5F, 0.5F)).setTexVetices(new Vertex(0.0F, 0.0F), new Vertex(1.0F, 0.0F),
						new Vertex(1.0F, 1.0F), new Vertex(0.0F, 1.0F));
		// ��
		MODEL.addFace(new Vertex(-0.5F, -0.5F, -0.5F), new Vertex(-0.5F, 0.5F, -0.5F), new Vertex(0.5F, 0.5F, -0.5F),
				new Vertex(0.5F, -0.5F, -0.5F)).setTexVetices(new Vertex(0.0F, 0.0F), new Vertex(0.0F, 1.0F),
						new Vertex(1.0F, 1.0F), new Vertex(1.0F, 0.0F));
		// ǰ
		MODEL.addFace(new Vertex(-0.5F, -0.5F, 0.5F), new Vertex(0.5F, -0.5F, 0.5F), new Vertex(0.5F, 0.5F, 0.5F),
				new Vertex(-0.5F, 0.5F, 0.5F)).setTexVetices(new Vertex(0.0F, 0.0F), new Vertex(1.0F, 0.0F),
						new Vertex(1.0F, 1.0F), new Vertex(0.0F, 1.0F));
	}

}
