package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.Buildings;

public class PageBuilding extends PageEasy {

	public final Building building;
	private float rX, rY, rZ;

	public PageBuilding(Building building) {
		this.building = building;
	}

	@Override
	public void drawValue(IPageManager pageManager) {
		int width = this.getWidthSize(pageManager);
		pageManager.drawTitle(I18n.format(this.getTitle()), 0, 6, width, 4210752);
	}

	@Override
	public void drawBackground(int xoff, int yoff, IPageManager pageManager) {
		int x = pageManager.getGui().getXSize() / 2;
		int y = pageManager.getGui().getYSize() / 2  + 10;
		AxisAlignedBB box = building.getBox();
		double lx = box.maxX - box.minX;
		double lz = box.maxZ - box.minZ;
		double distance = Math.max(lx, lz);
		float scale = 1.5f;
			scale = (float) (2.25f / MathHelper.sqrt(distance * 0.5));
		pageManager.drawBuilding(building, xoff + x, yoff + y, rX, rY, rZ, scale);
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
			int dx = mouseX - lastX;
			int dy = lastY - mouseY;
			lastX = mouseX;
			lastY = mouseY;
			rY += dx;
			rX += dy;
		}
	}
}
