package yuzunyannn.elementalsorcery.nodegui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.input.Mouse;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingBlocks;
import yuzunyannn.elementalsorcery.util.item.BigItemStack;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;

public class GTutorialBuilding extends GNode {

	public static class BlockEntry implements Comparable<BlockEntry> {
		final BlockPos pos;
		final IBlockState state;
		final ItemStack stack;
		final int stackHash;
		TileEntity tileCache;
		boolean tileCacheChecked;

		public BlockEntry(BlockPos pos, IBlockState state, ItemStack stack, int hash) {
			this.pos = pos;
			this.state = state;
			this.stack = stack;
			this.stackHash = hash;
		}

		@Override
		public int hashCode() {
			return pos.hashCode();
		}

		@Override
		public int compareTo(BlockEntry o) {
			return pos.compareTo(o.pos);
		}

		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj instanceof Vec3i) {
				BlockEntry other = (BlockEntry) obj;
				return pos.equals(other.pos);
			}
			return false;
		}

		public TileEntity getRenderTileIfSepcialRender() {
			if (tileCacheChecked) return tileCache;
			tileCacheChecked = true;
			if (state.getBlock() instanceof BlockContainer) {
				try {
					tileCache = state.getBlock().createTileEntity(mc.world, state);
					return tileCache;
				} catch (Exception e) {}
			}
			return null;
		}

		public boolean isRenderModel() {
			return state.getRenderType() == EnumBlockRenderType.MODEL;
		}
	}

	public float bRotationX = 145, bRotationY = 45, bRotationZ;
	public float bScale = 1;
	protected Map<Integer, Set<BlockEntry>> layerMap = new HashMap<>();
	protected List<Integer> layerList = new ArrayList<>();
	public double offsetX, offsetY;
	public final List<Integer> showLayerList = new ArrayList<>();

	public GTutorialBuilding() {
		this.anchorX = this.anchorY = 0.5f;
		this.setInteractor(new IGInteractor() {

			double lastX;
			double lastY;

			@Override
			public boolean blockMouseEvent(GNode node, Vec3d worldPos) {
				return Mouse.getEventButton() == 0;
			}

			@Override
			public void onMouseWheel(GNode node, Vec3d worldPos, int detal) {
				float n = detal / 120f;
				bScale = MathHelper.clamp(bScale + n / 10, 0.5f, 2);
			}

			@Override
			public boolean onMousePressed(GNode node, Vec3d worldPos) {
				if (Mouse.getEventButton() != 0) return false;
				lastX = worldPos.x;
				lastY = worldPos.y;
				return true;
			}

			@Override
			public void onMouseDrag(GNode node, Vec3d worldPos) {
				double dx = lastX - worldPos.x;
				double dy = lastY - worldPos.y;
				lastX = worldPos.x;
				lastY = worldPos.y;
				bRotationY += dx;
				bRotationX += dy;
			}

			@Override
			public void onMouseReleased(GNode node, Vec3d worldPos) {

			}
		});
	}

	public void importBuilding(Building building) {
		BuildingBlocks iter = building.getBuildingIterator();
		while (iter.next()) {
			BlockPos blockpos = iter.getPos().toImmutable();
			IBlockState iblockstate = iter.getState();
			Set<BlockEntry> entrySet = layerMap.get(blockpos.getY());
			if (entrySet == null) {
				layerMap.put(blockpos.getY(), entrySet = new HashSet<>());
				int y = blockpos.getY();
				int i = MathSupporter.binarySearch(layerList, (s) -> y - s.doubleValue());
				if (i < 0) i = -i - 1;
				layerList.add(i, y);
			}
			Building.BlockInfo inf = iter.getBlockInfo();
			entrySet.add(new BlockEntry(blockpos, iblockstate, iter.getItemStack(), inf.getTypeIndex()));
		}
	}

	public int getBuildingHeight() {
		if (layerList.isEmpty()) return 0;
		return layerList.get(layerList.size() - 1) - layerList.get(0) + 1;
	}

	public int getMinLayer() {
		if (layerList.isEmpty()) return 0;
		return layerList.get(0);
	}

	public int getMaxLayer() {
		if (layerList.isEmpty()) return 0;
		return layerList.get(layerList.size() - 1);
	}

	public List<Integer> getLayerList() {
		return layerList;
	}

	public List<Integer> getShowLayerList() {
		return showLayerList;
	}

	public List<BigItemStack> getItemStackByShowLayer() {
		Map<Integer, BigItemStack> stackMap = new HashMap<>();
		List<Integer> layerList = this.showLayerList.isEmpty() ? this.layerList : this.showLayerList;
		for (Integer y : layerList) {
			Set<BlockEntry> entrySet = layerMap.get(y);
			if (entrySet == null) continue;
			for (BlockEntry entry : entrySet) {
				BigItemStack stack = stackMap.get(entry.stackHash);
				if (stack == null) stackMap.put(entry.stackHash, stack = new BigItemStack(entry.stack.copy()));
				else stack.grow(entry.stack.getCount());
			}
		}
		return new ArrayList<>(stackMap.values());
	}

	@Override
	protected void render(float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(offsetX * bScale * 16, offsetY * bScale * 16, 0);
		RenderFriend.disableLightmap(true);
		try {
			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.rotate(bRotationX, 1, 0, 0);
			GlStateManager.rotate(bRotationZ, 0, 0, 1);
			GlStateManager.rotate(bRotationY, 0, 1, 0);
			GlStateManager.scale(bScale * 16, bScale * 16, bScale * 16);
			GlStateManager.translate(-0.5, -0.5, -0.5);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
			BlockModelRenderer render = blockrendererdispatcher.getBlockModelRenderer();

			List<Integer> layerList = this.showLayerList.isEmpty() ? this.layerList : this.showLayerList;
			for (Integer y : layerList) {
				Set<BlockEntry> entrySet = layerMap.get(y);
				if (entrySet == null) continue;
				for (BlockEntry entry : entrySet) {
					BlockPos pos = entry.pos;
					IBlockState state = entry.state;
					TileEntity tile = entry.getRenderTileIfSepcialRender();
					if (tile != null) toNext: {
						TileEntitySpecialRenderer<TileEntity> tileRender = TileEntityRendererDispatcher.instance
								.getRenderer(tile);
						if (tileRender == null) break toNext;
						try {
							tileRender.render(tile, pos.getX(), pos.getY(), pos.getZ(), mc.getRenderPartialTicks(), -1,
									1);
						} catch (Exception e) {} finally {
							mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
							RenderFriend.disableLightmap(true);
						}
					}

					if (entry.isRenderModel()) {
						GlStateManager.disableCull();
						bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
						render.renderModelFlat(mc.world, blockrendererdispatcher.getModelForState(state), state, pos,
								bufferbuilder, false, MathHelper.getPositionRandom(pos));
						tessellator.draw();
					}
				}
			}
		} catch (Exception e) {}

		RenderFriend.disableLightmap(true);
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}
}
