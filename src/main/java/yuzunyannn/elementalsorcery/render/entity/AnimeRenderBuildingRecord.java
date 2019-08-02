package yuzunyannn.elementalsorcery.render.entity;

import java.util.Iterator;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.crafting.ICraftingCommit;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyannn.elementalsorcery.crafting.altar.CraftingBuildingRecord;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.event.IRenderClient;
import yuzunyannn.elementalsorcery.render.RenderRulerSelectRegion;
import yuzunyannn.elementalsorcery.render.model.ModelBuildingAltar;

public class AnimeRenderBuildingRecord implements ICraftingLaunchAnime, IRenderClient {

	private World world;
	private ItemStack stack;
	private float theta = 0.0f;
	private boolean finish = false;
	CraftingBuildingRecord cnr;
	BlockPos lastPos = BlockPos.ORIGIN;
	BlockPos tilePos;
	Vec3d tile3DPos;
	LinkedList<BlockEffect> effects = new LinkedList<>();

	public AnimeRenderBuildingRecord(World world, ItemStack stack, CraftingBuildingRecord commit) {
		this.world = world;
		this.stack = stack;
		this.cnr = commit;
		tilePos = cnr.tile.getPos();
		tile3DPos = new Vec3d(tilePos.getX() + 0.5, tilePos.getY() + 0.5, tilePos.getZ() + 0.5);
		EventClient.addRenderTask(this);
	}

	@Override
	public void update(ICraftingCommit commit, World world, int endTick) {
		if (endTick >= 0)
			theta = endTick;
		else
			theta += 1;
		BlockPos pos = cnr.getCurrPos();
		if (pos != null && !pos.equals(this.lastPos)) {
			this.lastPos = pos;
			if (!world.isAirBlock(pos)) {
				IBlockState state = world.getBlockState(pos);
				ItemStack stack = new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state));
				if (!effects.isEmpty()) {
					if (!effects.getLast().stack.isItemEqual(stack))
						effects.addLast(new BlockEffect(stack));
				} else
					effects.addLast(new BlockEffect(stack));
			}
		}
		Iterator<BlockEffect> iter = effects.iterator();
		while (iter.hasNext()) {
			BlockEffect e = iter.next();
			e.endTime -= 0.05f;
			e.onUpdate();
			if (e.endTime <= 0)
				iter.remove();
		}
	}

	final Tessellator tessellator = Tessellator.getInstance();
	final BufferBuilder bufferbuilder = tessellator.getBuffer();

	@Override
	public void doRender(ICraftingCommit commit, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		float high = 0.25f;
		float xr = -90;
		float xoff = 0.1f;
		if (theta < 80) {
			float rate = (theta + partialTicks) / 80.0f;
			high = rate * high;
			xr = rate * xr;
			xoff = rate * xoff;
		}
		GlStateManager.translate((float) x + xoff, (float) y - 0.25f + high, (float) z);
		yuzunyannn.elementalsorcery.util.render.RenderHelper.layItemPositionFix(stack);
		GlStateManager.rotate(xr, 1, 0, 0);
		GlStateManager.rotate(theta + partialTicks, 0, 1, 0);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.popMatrix();
	}

	@Override
	public void endEffect(ICraftingCommit commit, World world, BlockPos pos, int flag) {
		CraftingBuildingRecord cnr = (CraftingBuildingRecord) commit;
		cnr.tile.endWork();
		finish = true;
		this.finish = true;
	}

	private Vec3d getFlyScaner(float partialTicks) {
		return ModelBuildingAltar
				.getRotationPos(EventClient.getGlobalRotateInRender(partialTicks) / 180.0f * 3.1514926f * 1.25f)
				.scale(0.0625);
	}

	@Override
	public int onRender(float partialTicks) {
		if (this.world != Minecraft.getMinecraft().world)
			return IRenderClient.END;
		if (this.finish)
			return IRenderClient.END;

		BlockPos pos = cnr.getCurrPos();
		if (pos != null) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(tilePos.getX(), tilePos.getY(), tilePos.getZ());
			GlStateManager.disableTexture2D();
			GlStateManager.glLineWidth(5);
			pos = pos.add(-tilePos.getX(), -tilePos.getY(), -tilePos.getZ());
			float x = pos.getX();
			float y = pos.getY();
			float z = pos.getZ();
			float r = cnr.r;
			float g = cnr.g;
			float b = cnr.b;
			float fadeTime = 1.0f;
			Vec3d vec = this.getFlyScaner(partialTicks);
			bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(0.5 + vec.x, vec.y, 0.5 + vec.z).color(r, g, b, fadeTime).endVertex();
			bufferbuilder.pos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5).color(r, g, b, fadeTime).endVertex();
			tessellator.draw();

			bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
			RenderRulerSelectRegion.vertexCubeLine(bufferbuilder, pos, 1, 1, 1, r, g, b, fadeTime);
			tessellator.draw();

			GlStateManager.enableTexture2D();
			GlStateManager.popMatrix();
		}

		GlStateManager.enableBlend();
		Iterator<BlockEffect> iter = effects.iterator();
		while (iter.hasNext()) {
			BlockEffect e = iter.next();
			e.onRender(partialTicks);
		}
		GlStateManager.disableBlend();

		return IRenderClient.SUCCESS;
	}

	public class BlockEffect {

		final ItemStack stack;
		float endTime = 1.0f;
		double x, y, z;
		double vx, vy, vz;
		Vec3d tar;

		public BlockEffect(ItemStack stack) {
			this.stack = stack;
			Vec3d loc = getFlyScaner(0);
			x = loc.x + 0.5 + tilePos.getX();
			y = loc.y + tilePos.getY();
			z = loc.z + 0.5 + tilePos.getZ();
			tar = tile3DPos.subtract(new Vec3d(x, y, z)).scale(0.005);
			vx = -tar.z * 0.5;
			vy = 0;
			vz = tar.x * 0.5;
		}

		public void onUpdate() {
			tar = tile3DPos.subtract(new Vec3d(x, y, z)).scale(0.005);
			x += vx;
			y += vy;
			z += vz;
			vx += tar.x;
			vy += tar.y;
			vz += tar.z;
		}

		public int onRender(float partialTicks) {
			if (this.endTime < 0)
				return 0;
			float scale;
			if (this.endTime > 0.5)
				scale = (1 - this.endTime) * 2;
			else
				scale = this.endTime * 2;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			GlStateManager.scale(scale * 0.175, scale * 0.175, scale * 0.175);
			GlStateManager.rotate(EventClient.getGlobalRotateInRender(partialTicks), 0, 1, 0);
			RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
			IBakedModel ibakedmodel = renderItem.getItemModelWithOverrides(stack, (World) null,
					(EntityLivingBase) null);
			renderItem.renderItem(stack, ibakedmodel);
			GlStateManager.popMatrix();
			return 0;
		}

	}
}
