package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.building.Building;

public class PageBuilding extends PageEasy {

	public final Building building;
	public Building extra;
	private float rX, rY, rZ;

	public int xoff, yoff;
	public float scale = 1;

	public PageBuilding(Building building) {
		this.building = building;
		rX = 145;
		rY = 45;
	}

	public void addExtraBlock(BlockPos pos, IBlockState block) {
		if (block == null || block == Blocks.AIR.getDefaultState()) return;
		if (extra == null) extra = new Building();
		extra.add(block, pos);
	}

	public void addExtraBlockNotOverlap(BlockPos pos, IBlockState block) {
		if (block == null || block == Blocks.AIR.getDefaultState()) return;
		if (building.haveBlock(pos)) return;
		if (extra == null) extra = new Building();
		if (extra.haveBlock(pos)) return;
		extra.add(block, pos);
	}

	@Override
	public void drawValue(IPageManager pageManager) {
		int width = this.getWidthSize(pageManager);
		pageManager.drawTitle(I18n.format(this.getTitle()), 0, 6, width, 4210752);
	}

	@Override
	public void drawBackground(int xoff, int yoff, IPageManager pageManager) {
		int x = pageManager.getGui().getXSize() / 2 + this.xoff;
		int y = pageManager.getGui().getYSize() / 2 + 20 + this.yoff;
		AxisAlignedBB box = building.getBox();
		double lx = box.maxX - box.minX;
		double lz = box.maxZ - box.minZ;
		double distance = Math.max(lx, lz);
		if (extra != null) {
			box = extra.getBox();
			lx = box.maxX - box.minX;
			lz = box.maxZ - box.minZ;
			distance = Math.max(distance, Math.max(lx, lz));
		}
		float scale = 1.5f;
		scale = (float) (2.25f / MathHelper.sqrt(distance * 0.4)) * this.scale;
		pageManager.drawBuilding(building, xoff + x, yoff + y, rX, rY, rZ, scale);
		if (extra != null) pageManager.drawBuilding(extra, xoff + x, yoff + y, rX, rY, rZ, scale);
	}

	int lastX;
	int lastY;

	@Override
	public void mouseClick(int mouseX, int mouseY, int mouseButton, IPageManager pageManager) {
		if (mouseButton == 0) {
			lastX = mouseX;
			lastY = mouseY;
		}
	}

	@Override
	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick,
			IPageManager pageManager) {
		if (clickedMouseButton == 0) {
			int dx = lastX - mouseX;
			int dy = lastY - mouseY;
			lastX = mouseX;
			lastY = mouseY;
			rY += dx;
			rX += dy;
		}
	}
}
