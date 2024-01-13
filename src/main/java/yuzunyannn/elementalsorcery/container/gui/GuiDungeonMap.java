package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.building.BuildingFace;
import yuzunyannn.elementalsorcery.container.ContainerDungeonMap;
import yuzunyannn.elementalsorcery.dungeon.DungeonArea;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaDoor;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoom;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoomSpecialThing;
import yuzunyannn.elementalsorcery.dungeon.DungeonRoomDoor;
import yuzunyannn.elementalsorcery.dungeon.DungeonRoomType;
import yuzunyannn.elementalsorcery.dungeon.IDungeonSpecialThing;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

@SideOnly(Side.CLIENT)
public class GuiDungeonMap extends GuiScreen {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID, "textures/gui/dungeon_map.png");

	ContainerDungeonMap container;

	public int tick = 0;

	public float mapWidth = 280;
	public float mapHeight = 200;

	public float mapOffsetX;
	public float mapOffsetY;
	public float prevMapOffsetX;
	public float prevMapOffsetY;
	public float prevMapScale = 1;
	public float mapScale = 1;

	public float targetMapOffsetX;
	public float targetMapOffsetY;
	public float targetMapScale = 1;

	public int currHoverRoomId = -1;
	public int currSelectedRoomId = -1;

	public BlockPos currHoverThingPos = null;
	public IDungeonSpecialThing currHoverThing = null;
	public int thingSendCD = 0;

	public float targetMapRotation = 0;
	public float mapRotation = 0;
	public float prevMapRotation = 0;
//	public static boolean isRotaionLock = false;

	public double mapMaxX = -Double.MAX_VALUE, mapMinX = Double.MAX_VALUE;
	public double mapMaxZ = -Double.MAX_VALUE, mapMinZ = Double.MAX_VALUE;
	public BlockPos basePos = BlockPos.ORIGIN;

	public GuiDungeonMap(EntityPlayer player, BlockPos pos) {
		this.container = new ContainerDungeonMap(player, pos);

	}

	@Override
	public void initGui() {
		super.initGui();
		this.mc.player.openContainer = this.container;
		// 初始化默认方向
		EnumFacing facing = this.container.player.getHorizontalFacing();
		this.targetMapRotation = this.prevMapRotation = this.mapRotation = -facing.getHorizontalAngle() + 180;
	}

	public void close() {
		this.mc.player.closeScreen();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void updateScreen() {

		this.tick++;

		mapOffsetX = targetMapOffsetX;
		mapOffsetY = targetMapOffsetY;
		prevMapOffsetX = mapOffsetX;
		prevMapOffsetY = mapOffsetY;

		mapScale = targetMapScale;
		prevMapScale = mapScale;

		prevMapRotation = mapRotation;

		mapRotation = mapRotation + (targetMapRotation - mapRotation) * 0.2f;

		if (this.tick % 20 == 0) checkFlagChange();
		if (thingSendCD > 0) thingSendCD--;
	}

	protected void checkFlagChange() {
		DungeonArea area = this.container.getDungeonArea();
		if (area == null) return;
		for (Entry<Integer, Integer> entry : this.container.roomShowMap.entrySet()) {
			DungeonAreaRoom room = area.getRoomById(entry.getKey());
			if (room == null) continue;
			if (room.runtimeChangeFlag < entry.getValue()) {
				// 能更就更，更不下來，拉倒，等下次
				room.requireUpdateFromServer();
				room.runtimeChangeFlag = entry.getValue();
			}
		}
	}

	public float lastMouseX;
	public float lastMouseY;
	public float lastStartMouseX;
	public float lastStartMouseY;
	public boolean lastInArea = false;

	@Override
	public void handleMouseInput() throws IOException {
		int k = Mouse.getEventDWheel();
		if (k != 0) {
			int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
			int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
			if (!isMouseInMapArea(x, y)) return;
			if (this.isMouseInDirectionArea(x, y)) {
				targetMapRotation += k / 120f * 5;
				return;
			}
			int dx = x - this.width / 2;
			int dy = y - this.height / 2;
			float lastScale = targetMapScale;
			float changeRate = k / 120f / 4;
			targetMapScale = Math.max(1f, targetMapScale += changeRate);
			targetMapOffsetX = (targetMapOffsetX - dx) / lastScale * targetMapScale + dx;
			targetMapOffsetY = (targetMapOffsetY - dy) / lastScale * targetMapScale + dy;
			return;
		}
		super.handleMouseInput();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		lastStartMouseX = mouseX;
		lastStartMouseY = mouseY;
		lastInArea = isMouseInMapArea(mouseX, mouseY);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if (!lastInArea) return;
		float dx = mouseX - lastMouseX;
		float dy = mouseY - lastMouseY;
		float theta = mapRotation / 180f * 3.1415926f;
		targetMapOffsetX += dx * Math.cos(theta) + dy * Math.sin(theta);
		targetMapOffsetY += dy * Math.cos(theta) - dx * Math.sin(theta);
		lastMouseX = mouseX;
		lastMouseY = mouseY;

		double mapWidth = (this.mapMaxX - this.mapMinX) * mapScale;
		double fixLX = mapWidth / 2 - (basePos.getX() - this.mapMinX) * mapScale;
		double fixRX = mapWidth / 2 - (basePos.getX() - this.mapMinX) * mapScale;
		if (targetMapOffsetX - mapWidth / 2 + fixRX > this.mapWidth / 2)
			targetMapOffsetX = (float) (this.mapWidth / 2 + mapWidth / 2 - fixRX);
		else if (targetMapOffsetX + mapWidth / 2 + fixLX < -this.mapWidth / 2)
			targetMapOffsetX = (float) (-this.mapWidth / 2 - mapWidth / 2 - fixLX);

		double mapHeight = (this.mapMaxZ - this.mapMinZ) * mapScale;
		double fixBY = mapHeight / 2 - (basePos.getZ() - this.mapMinZ) * mapScale + 1;
		double fixTY = mapHeight / 2 - (basePos.getZ() - this.mapMinZ) * mapScale;
		if (targetMapOffsetY - mapHeight / 2 + fixBY > this.mapHeight / 2)
			targetMapOffsetY = (float) (this.mapHeight / 2 + mapHeight / 2 - fixBY);
		else if (targetMapOffsetY + mapHeight / 2 + fixTY < -this.mapHeight / 2)
			targetMapOffsetY = (float) (-this.mapHeight / 2 - mapHeight / 2 - fixTY);

	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		if (!lastInArea) {
			if (this.currHoverThing != null && this.currHoverThingPos != null) {
				if (thingSendCD > 0) return;
				NBTTagCompound nbt = new NBTTagCompound();
				NBTHelper.setBlockPos(nbt, "st_pos", currHoverThingPos);
				this.container.sendToServer(nbt);
				thingSendCD = 20;
			}
			return;
		}
		if (Math.abs(mouseX - lastStartMouseX) < 4 && Math.abs(mouseY - lastStartMouseY) < 4) {
			if (this.isMouseInDirectionArea(mouseX, mouseY)) {
//				if (this.targetMapRotation == 0) isRotaionLock = false;
//				else isRotaionLock = true;
				this.targetMapRotation = 0;
				while (this.mapRotation > 360) this.mapRotation -= 360;
				while (this.mapRotation < 0) this.mapRotation += 360;
				this.prevMapRotation = this.mapRotation;
			} else this.currSelectedRoomId = this.currHoverRoomId;
		}
	}

	public boolean isMouseInMapArea(int mouseX, int mouseY) {
		int cx = getMapDrawCenterX();
		int cy = getMapDrawCenterY();
		if (mouseX >= cx - mapWidth / 2 && mouseX <= cx + mapWidth / 2) {
			if (mouseY >= cy - mapHeight / 2 && mouseY <= cy + mapHeight / 2) return true;
		}
		return false;
	}

	public boolean isMouseInDirectionArea(int mouseX, int mouseY) {
		float cx = getMapDrawCenterX() + mapWidth / 2 - 10;
		float cy = getMapDrawCenterY() + mapHeight / 2 - 10;
		float size = 8;
		if (mouseX >= cx - size && mouseX <= cx + size) {
			if (mouseY >= cy - size && mouseY <= cy + size) return true;
		}
		return false;
	}

	public int getMapDrawCenterX() {
		return this.width / 2;
	}

	public int getMapDrawCenterY() {
		return this.height / 2;
	}

	public void drawBadScene() {

	}

	public void drawWaitScene() {

	}

	public static final RenderTexutreFrame frameBackGround = new RenderTexutreFrame(0, 0, 28, 28, 256, 256);
	public static final RenderTexutreFrame frameRoom = new RenderTexutreFrame(28 + 14, 0, 7, 7, 256, 256);
	public static final RenderTexutreFrame frameRoomHover = new RenderTexutreFrame(28 + 7, 0, 7, 7, 256, 256);
	public static final RenderTexutreFrame frameRoomSelect = new RenderTexutreFrame(28, 0, 7, 7, 256, 256);
	public static final RenderTexutreFrame frameOpenDoor = new RenderTexutreFrame(28, 7, 3, 3, 256, 256);
	public static final RenderTexutreFrame frameCloseDoor = new RenderTexutreFrame(28 + 3, 7, 3, 3, 256, 256);
	public static final RenderTexutreFrame frameExitDoor = new RenderTexutreFrame(0, 28, 3, 3, 256, 256);

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		partialTicks = mc.getRenderPartialTicks();
		this.drawDefaultBackground();

		int ret = drawMap(mouseX, mouseY, partialTicks);

		if (ret != 0) {
			if (ret == -1) this.drawWaitScene();
			else this.drawBadScene();
			return;
		}

		int mapCX = getMapDrawCenterX();
		int mapCY = getMapDrawCenterY();

		DungeonArea area = this.container.getDungeonArea();

		// 探索进度
		int open = this.container.roomShowMap.size();
		int total = area.getRooms().size();
		this.fontRenderer.drawString(
				I18n.format("info.dungeon.explor.progress", open, total, open / (float) total * 100),
				(int) (mapCX - mapWidth / 2), (int) (mapCY - mapHeight / 2 - 10), 0x3e9d8d);

		// 选中的房间！
		currHoverThing = null;

		DungeonAreaRoom room = area.getRoomById(this.currSelectedRoomId);
		if (room == null) return;

		float iconSize = 12;
		int mapSX = (int) (mapCX + this.mapWidth / 2 + 4 + iconSize / 2);
		int mapSY = (int) (mapCY - this.mapHeight / 2 + 4 + iconSize / 4);
		float yoffset = 0;

		String str = String.valueOf(room.getId());
		str = str + String.format("(0x%x)", room.getType().getStructure().toString().hashCode());
		this.fontRenderer.drawString(I18n.format("info.dungeon.room.gui", str), (int) (mapSX - iconSize / 2),
				(int) (mapSY - 5 + yoffset), 0xffffff);
		yoffset = yoffset + this.fontRenderer.FONT_HEIGHT + 2;

		Map<BlockPos, DungeonAreaRoomSpecialThing> map = room.getSpecialMap();
		for (Entry<BlockPos, DungeonAreaRoomSpecialThing> entry : map.entrySet()) {
			DungeonAreaRoomSpecialThing thing = entry.getValue();
			IDungeonSpecialThing handler = thing.getHandler();
			if (handler == null) continue;
			float cx = mapSX;
			float cy = mapSY + yoffset;
			handler.renderMiniIcon(mc, cx, cy, iconSize);
			boolean isHover = false;
			if (GuiNormal.isMouseIn(mouseX, mouseY, cx - iconSize / 2, cy - iconSize / 2, iconSize, iconSize)) {
				currHoverThing = handler;
				currHoverThingPos = entry.getKey();
				isHover = true;
			}
			String title = handler.getTitle(room, isHover);
			if (!title.isEmpty())
				this.fontRenderer.drawString(title, (int) (cx + iconSize - 2), (int) (cy - iconSize / 4), 0xffffff);

			yoffset += iconSize + 2;
		}
	}

	// 画地图
	protected int drawMap(double mouseX, double mouseY, float partialTicks) {

		int mapCX = getMapDrawCenterX();
		int mapCY = getMapDrawCenterY();

		DungeonArea area = this.container.getDungeonArea();
		if (area == null) return -1;

		Integer currRoomId = this.container.currRoomId;
		if (currRoomId == null) return -1;

		DungeonAreaRoom currRoom = area.getRoomById(currRoomId);
		if (currRoom == null) return -3;

		mc.getTextureManager().bindTexture(TEXTURE);
		RenderFriend.drawTextureRectInCenter(mapCX, mapCY, this.mapWidth, this.mapHeight, 0, 28, 28, 28, 256, 256);

		basePos = currRoom.getCenterPos();

		float mapScale = RenderFriend.getPartialTicks(this.mapScale, this.prevMapScale, partialTicks);
		float offsetX = RenderFriend.getPartialTicks(mapOffsetX, prevMapOffsetX, partialTicks);
		float offsetY = RenderFriend.getPartialTicks(mapOffsetY, prevMapOffsetY, partialTicks);
		float mapRotation = RenderFriend.getPartialTicks(this.mapRotation, this.prevMapRotation, partialTicks);

		GlStateManager.pushMatrix();
		GlStateManager.translate(mapCX + offsetX, mapCY + offsetY, 0);
		this.enableScissor(mapCX - this.mapWidth / 2, mapCY - this.mapHeight / 2, this.mapWidth, this.mapHeight);

		if (mapRotation != 0) {
			GlStateManager.translate(-offsetX, -offsetY, 0);
			GlStateManager.rotate(mapRotation, 0, 0, 1);
			GlStateManager.translate(offsetX, offsetY, 0);
		}
		GlStateManager.scale(mapScale, mapScale, mapScale);
		GlStateManager.translate(-basePos.getX(), -basePos.getZ(), 0);

		// 计算相对鼠标坐标
		float theta = mapRotation / 180f * 3.1415926f;
		double mx = mouseX - mapCX;
		double my = mouseY - mapCY;
		mouseX = mx * Math.cos(theta) + my * Math.sin(theta);
		mouseY = my * Math.cos(theta) - mx * Math.sin(theta);
		mouseX += -offsetX;
		mouseY += -offsetY;
		mouseX /= mapScale;
		mouseY /= mapScale;
		mouseX += basePos.getX();
		mouseY += basePos.getZ();

		// 逐一绘制房间
		this.currHoverRoomId = -1;
		for (Integer id : this.container.roomShowMap.keySet()) {
			DungeonAreaRoom room = area.getRoomById(id);
			if (room == null) continue;
			drawRoom(room, mouseX, mouseY, partialTicks);
		}

		// 玩家所在的小人头
		GlStateManager.translate(this.container.player.posX, this.container.player.posZ, 0);
		if (mapRotation != 0) GlStateManager.rotate(-mapRotation, 0, 0, 1);
		if (EventClient.tick % 60 < 2) RenderFriend.drawTextureRectInCenter(0, 0, 3, 3, 28, 28, 10, 9, 256, 256);
		else RenderFriend.drawTextureRectInCenter(0, 0, 3, 3, 28, 28 + 9, 10, 9, 256, 256);

		this.disableScissor();
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();

		mc.getTextureManager().bindTexture(TEXTURE);

		// 外边框
		RenderFriend.drawSplit9FrameInCenter(mapCX, mapCY, this.mapWidth + 4, this.mapHeight + 3.5f, frameBackGround,
				RenderFriend.SPLIT9_AVERAGE_RECT);

		// 指南针
		GlStateManager.translate(mapCX + this.mapWidth / 2 - 10, mapCY + this.mapHeight / 2 - 10, 0);
		if (mapRotation != 0) GlStateManager.rotate(mapRotation, 0, 0, 1);
		RenderFriend.drawTextureRectInCenter(0, 0, 16, 16, 49, 0, 27, 27, 256, 256);

		GlStateManager.popMatrix();

		return 0;
	}

	// 画房间
	protected void drawRoom(DungeonAreaRoom room, double mouseX, double mouseZ, float partialTicks) {
		BlockPos pos = room.getCenterPos();
		float x = pos.getX() + 0.5f;
		float z = pos.getZ() + 0.5f;

		mouseX -= x;
		mouseZ -= z;

		AxisAlignedBB aabb = room.getType().getStructure().getBox();
		aabb = BuildingFace.face(aabb, room.getFacing());

		if (mouseX >= aabb.minX && mouseX <= aabb.maxX) {
			if (mouseZ >= aabb.minZ && mouseZ <= aabb.maxZ) {
				this.currHoverRoomId = room.getId();
			}
		}

		mapMinX = Math.min(aabb.minX + x, mapMinX);
		mapMinZ = Math.min(aabb.minZ + z, mapMinZ);
		mapMaxX = Math.max(aabb.maxX + x, mapMaxX);
		mapMaxZ = Math.max(aabb.maxZ + z, mapMaxZ);

		float offsetX = (float) ((aabb.maxX + aabb.minX) / 2);
		float offsetZ = (float) ((aabb.maxZ + aabb.minZ) / 2);

		x += offsetX;
		z += offsetZ;

		float angle = room.getFacing().getHorizontalAngle() + 180;
		GlStateManager.translate(x, z, 0);
		GlStateManager.rotate(angle, 0, 0, 1);
		try {
			drawRoomModel(room, partialTicks);
			drawRoomSepicalThing(room, partialTicks);
		} catch (Exception e) {
			if (ESAPI.isDevelop) ESAPI.logger.warn("看看", e);
		}
		GlStateManager.rotate(-angle, 0, 0, 1);
		GlStateManager.translate(-x, -z, 0);
	}

	// 画房间的模型
	public void drawRoomModel(DungeonAreaRoom room, float partialTicks) {
		DungeonRoomType roomType = room.getType();
		AxisAlignedBB aabb = roomType.getStructure().getBox();
		float width = (float) (aabb.maxX - aabb.minX) + 1f;
		float height = (float) (aabb.maxZ - aabb.minZ) + 1f;

		RenderTexutreFrame frame = frameRoom;
		if (currSelectedRoomId == room.getId()) frame = frameRoomSelect;
		else if (currHoverRoomId == room.getId()) frame = frameRoomHover;
		RenderFriend.drawSplit9FrameInCenter(0, 0, width, height, frame, RenderFriend.SPLIT9_AVERAGE_RECT);

		List<DungeonAreaDoor> doors = room.getDoorLinks();
		float offsetX = (float) ((aabb.maxX + aabb.minX) / 2);
		float offsetZ = (float) ((aabb.maxZ + aabb.minZ) / 2);

		for (int doorIndex = 0; doorIndex < doors.size(); doorIndex++) {
			DungeonAreaDoor door = doors.get(doorIndex);
			if (!door.isLink()) continue;
			DungeonRoomDoor doorType = roomType.getDoors().get(doorIndex);
			EnumFacing facing = doorType.getOrient();
			int length = doorType.getDoorLenghToBorder(aabb);
			BlockPos pos = doorType.getCorePos().offset(facing, length);
			int doorWidth = doorType.getDoorWidth();
			float dx = pos.getX() - offsetX;
			float dz = pos.getZ() - offsetZ;
			frame = frameOpenDoor;
			if (!this.container.isOpen(door.getLinkRoomId())) frame = frameCloseDoor;
			if (facing.getAxis() == Axis.Z) RenderFriend.drawFrameInCenter(dx, dz, doorWidth, 1, frame);
			else RenderFriend.drawFrameInCenter(dx, dz, 1, doorWidth, frame);
		}
		// 默认房间在多画一个出口
		if (room.getId() == 0) {
			EnumFacing facing = EnumFacing.NORTH;
			BlockPos pos = new BlockPos(0, 0, 0).offset(facing, 7);
			float dx = pos.getX() - offsetX;
			float dz = pos.getZ() - offsetZ;
			frame = new RenderTexutreFrame(0, 28, 3, 3, 256, 256);
			if (facing.getAxis() == Axis.Z) RenderFriend.drawFrameInCenter(dx, dz, 3, 1, frame);
			else RenderFriend.drawFrameInCenter(dx, dz, 1, 3, frame);
		}
	}

	// 画房间上的装饰物
	public void drawRoomSepicalThing(DungeonAreaRoom room, float partialTicks) {

		DungeonRoomType roomType = room.getType();
		AxisAlignedBB aabb = roomType.getStructure().getBox();
		BlockPos cpos = room.getCenterPos();

		float offsetX = (float) ((aabb.maxX + aabb.minX) / 2);
		float offsetZ = (float) ((aabb.maxZ + aabb.minZ) / 2);

		Map<BlockPos, DungeonAreaRoomSpecialThing> map = room.getSpecialMap();
		for (Entry<BlockPos, DungeonAreaRoomSpecialThing> entry : map.entrySet()) {
			BlockPos pos = entry.getKey();
			DungeonAreaRoomSpecialThing thing = entry.getValue();
			if (thing.getHandler() == null) continue;
			thing.getHandler().renderMiniIcon(mc, pos.getX() - cpos.getX() - offsetX,
					pos.getZ() - cpos.getZ() - offsetZ, 4);
		}
	}

	public void enableScissor(float x, float y, float width, float height) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		float xScale = mc.displayWidth / (float) this.width;
		float yScale = mc.displayHeight / (float) this.height;
		int rx = (int) (xScale * x);
		int ry = (int) (yScale * (this.height - y - height));
		int rw = (int) (xScale * width);
		int rh = (int) (yScale * height);
		GL11.glScissor(rx, ry, rw, rh);
	}

	public void disableScissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
}
