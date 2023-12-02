package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.IRenderOutline;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.block.env.BlockStoneDecoration;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelMagicAlternator;
import yuzunyannn.elementalsorcery.render.model.ModelStarCPU;
import yuzunyannn.elementalsorcery.tile.dungeon.TileStoneDecoration;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

@SideOnly(Side.CLIENT)
public class RenderTileStoneDecoration extends TileEntitySpecialRenderer<TileStoneDecoration>
		implements IRenderItem, IRenderOutline<TileStoneDecoration> {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/a_common_128x128.png");
	static public final ModelBase MODEL_ALTERNATOR = new ModelMagicAlternator();
	static public final ModelBase MODEL_COMPUTER_A = BlockStoneDecoration.MODEL_COMPUTER_A;
	static public final ModelBase MODEL_STAR_CPU = new ModelStarCPU();

	@Override
	public void render(TileStoneDecoration tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
//		if (true) return;

		EnumFacing facing = tile.getFacing();
		switch (tile.getDecorationType()) {
		case ALTERNATOR:
			RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
			RenderFriend.startTileEntitySpecialRenderForReverseModel(x + 0.5, y, z + 0.5, alpha);
			GlStateManager.rotate(facing.getHorizontalAngle(), 0, 1, 0);
			MODEL_ALTERNATOR.render(null, 0, 0, 0, 0, 0, 1);
			RenderFriend.endTileEntitySpecialRenderForReverseModel();
			RenderFriend.bindDestoryTextureEnd(destroyStage);
			break;
		case COMPUTER_A:
			RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
			RenderFriend.startTileEntitySpecialRenderForReverseModel(x + 0.5, y, z + 0.5, alpha);
			GlStateManager.rotate(facing.getHorizontalAngle(), 0, 1, 0);
			MODEL_COMPUTER_A.render(null, 0, 0, 0, 0, 0, 1);
			RenderFriend.endTileEntitySpecialRenderForReverseModel();
			RenderFriend.bindDestoryTextureEnd(destroyStage);
			break;
		case STAR_CPU:
			RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
			RenderFriend.startTileEntitySpecialRenderForReverseModel(x + 0.5, y, z + 0.5, alpha);
			GlStateManager.rotate(facing.getHorizontalAngle(), 0, 1, 0);
			MODEL_STAR_CPU.render(null, 0, 0, 0, 0, 0, 1);
			RenderFriend.endTileEntitySpecialRenderForReverseModel();
			RenderFriend.bindDestoryTextureEnd(destroyStage);
			break;
		case DUNGEON_REACTOR:
			RenderFriend.bindDestoryTexture(RenderTileElementReactor.TEXTURE, destroyStage, rendererDispatcher,
					DESTROY_STAGES);
			RenderFriend.startTileEntitySpecialRender(x + 0.5, y + 0.5, z + 0.5, 0.5f, alpha);
			GlStateManager.disableCull();
			GlStateManager.color(0.25f, 0.1f, 0.5f, alpha);
			GlStateManager.rotate(EventClient.getGlobalRotateInRender(partialTicks), 0, 1, 0);
			RenderTileElementReactor.MODEL_SPHERE.render();
			GlStateManager.enableCull();
			RenderFriend.endTileEntitySpecialRender();
			RenderFriend.bindDestoryTextureEnd(destroyStage);
			break;
		}
	}

	@Override
	public void renderTileOutline(TileStoneDecoration tile, EntityPlayer player, BlockPos pos, float partialTicks) {
		EnumFacing facing = tile.getFacing();

		RenderFriend.RenderCallbacks callback = new RenderFriend.RenderCallbacks() {
			@Override
			public void beforeRender() {
				GlStateManager.scale(-1, -1, 1);
				GlStateManager.rotate(facing.getHorizontalAngle(), 0, 1, 0);
			}

			@Override
			public void endRender() {
				GlStateManager.rotate(-facing.getHorizontalAngle(), 0, 1, 0);
				GlStateManager.scale(-1, -1, 1);
			}
		};

		switch (tile.getDecorationType()) {
		case ALTERNATOR:
			RenderFriend.renderOutlineByModel(MODEL_ALTERNATOR, player, pos, partialTicks, false, callback);
			break;
		case COMPUTER_A:
			RenderFriend.renderOutlineByModel(MODEL_COMPUTER_A, player, pos, partialTicks, true, callback);
			break;
		case STAR_CPU:
			RenderFriend.renderOutlineByModel(MODEL_STAR_CPU, player, pos, partialTicks, false, callback);
			break;
		case DUNGEON_REACTOR:
			break;
		}
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		BlockStoneDecoration.EnumDecType enumType = BlockStoneDecoration.EnumDecType
				.fromMeta(ItemHelper.getOrCreateTagCompound(stack).getInteger("tId"));
		GlStateManager.disableCull();
		switch (enumType) {
		case ALTERNATOR:
			RenderFriend.renderSpecialItemForModelForReverseModel(stack, TEXTURE, MODEL_ALTERNATOR, true);
			break;
		case COMPUTER_A:
			RenderFriend.renderSpecialItemForModelForReverseModel(stack, TEXTURE, MODEL_COMPUTER_A, true);
			break;
		case STAR_CPU:
			RenderFriend.renderSpecialItemForModelForReverseModel(stack, TEXTURE, MODEL_STAR_CPU, true);
			break;
		case DUNGEON_REACTOR:
			GlStateManager.disableCull();
			GlStateManager.color(0.25f, 0.1f, 0.5f);
			RenderFriend.renderSpecialItem(stack, RenderTileElementReactor.TEXTURE, RenderTileElementReactor.MODEL,
					true, 0.4, 0.175, 0.3, 0.15);
			GlStateManager.enableCull();
			break;
		}
		GlStateManager.enableCull();
	}

}
