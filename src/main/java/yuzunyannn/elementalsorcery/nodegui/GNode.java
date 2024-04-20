package yuzunyannn.elementalsorcery.nodegui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;

@SideOnly(Side.CLIENT)
public class GNode {

	protected final static Minecraft mc = Minecraft.getMinecraft();

	protected ArrayList<GNode> children = new ArrayList<>();
	protected Map<String, GNode> nameMap = new HashMap<>();
	protected LinkedList<GAction> actions = new LinkedList<GAction>();
	protected GNode parent;
	protected GScene scene;

	protected boolean visible = true;

	protected double x, y, z;
	protected double scaleX = 1, scaleY = 1, scaleZ = 1;
	protected float rotationZ;
	protected float anchorX, anchorY, anchorZ;
	protected double width = 1, height = 1, depth = 0;

	protected double prevX, prevY, prevZ;
	protected double prevScaleX = 1, prevScaleY = 1, prevScaleZ = 1;
	protected float prevRotationZ;

	protected boolean hasRotation = false;
	protected boolean hasScale = false;

	protected Color color = new Color(0xffffff);
	protected float alpha = 1;
	protected String name;
	protected boolean gaps = false;
	protected IGInteractor interactor;

	public static final Vec3d AXIS_Z = new Vec3d(0, 0, 1);

	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		if (this.parent != null) this.parent.onChangeName(this, oldName, this.name);
	}

	public String getName() {
		return name;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setSize(double width, double height, double depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}

	public void setAnchor(double x, double y, double z) {
		this.anchorX = (float) x;
		this.anchorY = (float) y;
		this.anchorZ = (float) z;
	}

	public void setAnchor(double x, double y) {
		this.anchorX = (float) x;
		this.anchorY = (float) y;
	}

	public Vec3d getAnchor() {
		return new Vec3d(anchorX, anchorY, anchorZ);
	}

	public void setGaps(boolean gaps) {
		this.gaps = gaps;
		if (gaps) this.updateGaps();
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void setPosition(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setPositionX(double x) {
		this.x = x;
	}

	public void setPositionY(double y) {
		this.y = y;
	}

	public void setPositionZ(double z) {
		this.z = z;
	}

	public void setPosition(int x, int y, int z) {
		setPosition((double) x, (double) y, (double) z);
	}

	public void setPosition(Vec3d vec) {
		setPosition(vec.x, vec.y, vec.z);
	}

	public void setPosition(Vec3i vec) {
		setPosition(vec.getX(), vec.getY(), vec.getZ());
	}

	public Vec3d getPostion() {
		return new Vec3d(x, y, z);
	}

	public double getPostionX() {
		return x;
	}

	public double getPostionY() {
		return y;
	}

	public void postionIntegralization() {
		double dx = MathSupporter.decimalForIntegralization(this.gX);
		double dy = MathSupporter.decimalForIntegralization(this.gY);
		this.x = this.x + dx;
		this.y = this.y + dy;
	}

	public void setRotation(float rotationZ) {
		this.rotationZ = rotationZ;
		this.hasRotation = true;
	}

	public float getRotation() {
		return rotationZ;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public double getDepth() {
		return depth;
	}

	public Vec3d getSize() {
		return new Vec3d(width, height, depth);
	}

	public void setSize(Vec3d size) {
		this.width = (float) size.x;
		this.height = (float) size.y;
		this.depth = (float) size.z;
	}

	public Vec3d getScale() {
		return new Vec3d(scaleX, scaleY, scaleZ);
	}

	public void setScale(Vec3d scale) {
		this.scaleX = scale.x;
		this.scaleY = scale.y;
		this.scaleZ = scale.z;
		this.hasScale = true;
	}

	public void setScale(double scale) {
		this.scaleX = scale;
		this.scaleY = scale;
		this.scaleZ = scale;
		this.hasScale = true;
	}

	public void setColorRef(Color color) {
		if (color == null) return;
		this.color = color;
	}

	public void setColor(Color color) {
		if (color == null) return;
		this.color.setColor(color);
	}

	public void setColor(int color) {
		this.color.setColor(color);
	}

	public void setColor(float r, float g, float b) {
		this.color.setColor(r, g, b);
	}

	public void setAlpha(float a) {
		this.alpha = a;
	}

	public float getAlpha() {
		return alpha;
	}

//	private class WorldPosScaleGetter {
//		Vec3d pos;
//		double scaleX = 1, scaleY = 1, scaleZ = 1;
//	}

//	private WorldPosScaleGetter doWorldPosScaleGetter(WorldPosScaleGetter getter) {
//		if (this.parent == null) {
//			getter.pos = new Vec3d(x, y, z);
//			return getter;
//		}
//		getter = this.parent.doWorldPosScaleGetter(getter);
//		Vec3d myPos = new Vec3d(x * getter.scaleX, y * getter.scaleY, z * getter.scaleZ);
//		if (this.parent.hasRotation)
//			myPos = MathSupporter.rotation(myPos, AXIS_Z, this.parent.rotationZ / 180 * 3.1415926);
//		getter.pos = getter.pos.add(myPos);
//		if (hasScale) {
//			getter.scaleX = getter.scaleX * this.scaleX;
//			getter.scaleY = getter.scaleY * this.scaleY;
//			getter.scaleZ = getter.scaleZ * this.scaleZ;
//		}
//		return getter;
//	}

	public Vec3d getPostionInWorldPos() {
//		WorldPosScaleGetter getter = doWorldPosScaleGetter(new WorldPosScaleGetter());
//		return getter.pos;
		return new Vec3d(gX, gY, gZ);
	}

	public float getRotationInWorldPos() {
//		if (this.parent == null) return this.rotationZ;
//		return this.rotationZ + this.parent.getRotationInWorldPos();
		return this.gRotationZ;
	}

	public void addChild(GNode node) {
		node.removeFromParent();
		if (children.isEmpty() || children.get(children.size() - 1).z == node.z) children.add(node);
		else {
			int i = MathSupporter.binarySearch(children, (s) -> node.z - s.z);
			if (i < 0) i = -i - 1;
			children.add(i, node);
		}
		node.parent = this;
		onChildAdded(node);
		if (this.isInScene()) node.onEnterScene(scene);
	}

	public void removeChild(GNode node) {
		Iterator<GNode> iter = children.iterator();
		while (iter.hasNext()) {
			if (iter.next() == node) {
				iter.remove();
				node.parent = null;
				if (this.isInScene()) node.onExitScene();
				onChildRemoved(node);
				return;
			}
		}
	}

	public void removeAllChild() {
		Iterator<GNode> iter = children.iterator();
		while (iter.hasNext()) {
			GNode node = iter.next();
			iter.remove();
			node.parent = null;
			if (this.isInScene()) node.onExitScene();
			onChildRemoved(node);
		}
	}

	public List<GNode> getChildren() {
		return children;
	}

	@Nullable
	public GNode getChildByName(String name) {
		return nameMap.get(name);
	}

	boolean isInScene() {
		return scene != null;
	}

	public boolean isInScene(GScene scene) {
		return this.scene == scene;
	}

	public void removeFromParent() {
		if (this.parent != null) {
			this.parent.removeChild(this);
		}
	}

	public void runAction(GAction action) {
		this.actions.add(action);
	}

	public void clearActions() {
		this.actions.clear();
	}

	protected void onExitScene() {
		for (GNode node : this.children) node.onExitScene();
		this.scene.removeInteractNode(this);
		this.scene = null;
	}

	protected void onEnterScene(GScene scene) {
		this.scene = scene;
		this.updateGlobalProps();
		this.scene.addInteractNode(this);
		for (GNode node : this.children) node.onEnterScene(scene);
	}

	protected void onChangeName(GNode child, String oldName, String newName) {
		if (oldName != null) this.nameMap.remove(oldName);
		if (newName != null) this.nameMap.put(newName, child);
	}

	protected void onChildRemoved(GNode node) {
		this.onChangeName(node, node.getName(), null);
	}

	protected void onChildAdded(GNode node) {
		this.onChangeName(node, null, node.getName());
	}

	public void setInteractor(IGInteractor handler) {
		this.interactor = handler;
		if (this.scene != null) {
			this.scene.removeInteractNode(this);
			this.scene.addInteractNode(this);
		}
	}

	public boolean testHit(Vec3d worldPos) {
		if (!visible) return false;
//		WorldPosScaleGetter getter = doWorldPosScaleGetter(new WorldPosScaleGetter());

		Vec3d vec = getPostionInWorldPos();
		vec = worldPos.subtract(vec);

		double width = this.width * gScaleX;
		double height = this.height * gScaleY;

		double aw = width * anchorX;
		double ah = height * anchorY;

		double left = -aw;
		double right = width - aw;
		double top = -ah;
		double bottom = height - ah;

		float rotation = getRotationInWorldPos();
		if (rotation != 0) vec = MathSupporter.rotation(vec, AXIS_Z, -rotation / 180 * 3.1415926);

		return vec.x >= left && vec.x <= right && vec.y >= top && vec.y <= bottom;
	}

	public void update() {
		if (this.gaps) updateGaps();
		if (!actions.isEmpty()) {
			Iterator<GAction> iter = actions.iterator();
			while (iter.hasNext()) {
				GAction action = iter.next();
				if (!action.isStart()) action.onStart(this);
				action.update(this);
				if (action.isOver()) iter.remove();
			}
		}
		updateGlobalProps();
		int i = 0;
		while (i < children.size()) {
			GNode node = children.get(i);
			node.update();
			if (i >= children.size()) break;
			if (children.get(i) == node) i++;
		}
	}

	public void updateGaps() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.prevRotationZ = this.rotationZ;
		this.prevScaleX = this.scaleX;
		this.prevScaleY = this.scaleY;
		this.prevScaleZ = this.scaleZ;
	}

	protected double gX, gY, gZ;
	protected double gScaleX, gScaleY, gScaleZ;
	protected float gRotationZ;

	protected void updateGlobalProps() {
		if (this.parent == null) {
			gX = x;
			gY = y;
			gZ = z;
			gScaleX = scaleX;
			gScaleY = scaleY;
			gScaleZ = scaleZ;
			gRotationZ = rotationZ;
			return;
		}

		Vec3d myPos = new Vec3d(x * parent.gScaleX, y * parent.gScaleY, z * parent.gScaleZ);
		if (parent.rotationZ != 0) myPos = MathSupporter.rotation(myPos, AXIS_Z, parent.rotationZ / 180 * 3.1415926);

		gX = parent.gX + myPos.x;
		gY = parent.gY + myPos.y;
		gZ = parent.gZ + myPos.z;

		if (hasScale) {
			gScaleX = parent.gScaleX * scaleX;
			gScaleY = parent.gScaleY * scaleY;
			gScaleZ = parent.gScaleZ * scaleZ;
		} else {
			gScaleX = parent.gScaleX;
			gScaleY = parent.gScaleY;
			gScaleZ = parent.gScaleZ;
		}

		gRotationZ = parent.gRotationZ + rotationZ;
	}

	protected double rX, rY, rZ;
	protected double rScaleX, rScaleY, rScaleZ;
	protected float rRotationZ;
	protected float rAlpha;

	protected void updateRenderProps(float partialTicks) {
		rAlpha = alpha * (parent == null ? 1 : parent.rAlpha);
		if (this.gaps) {
			rX = RenderFriend.getPartialTicks(x, prevX, partialTicks);
			rY = RenderFriend.getPartialTicks(y, prevY, partialTicks);
			rZ = RenderFriend.getPartialTicks(z, prevZ, partialTicks);
			if (hasRotation) rRotationZ = RenderFriend.getPartialTicks(rotationZ, prevRotationZ, partialTicks);
			else rRotationZ = rotationZ;
			if (hasScale) {
				rScaleX = RenderFriend.getPartialTicks(scaleX, prevScaleX, partialTicks);
				rScaleY = RenderFriend.getPartialTicks(scaleY, prevScaleY, partialTicks);
				rScaleZ = RenderFriend.getPartialTicks(scaleZ, prevScaleZ, partialTicks);
			} else {
				rScaleX = scaleX;
				rScaleY = scaleY;
				rScaleZ = scaleZ;
			}
		} else {
			rX = x;
			rY = y;
			rZ = z;
			rRotationZ = this.rotationZ;
			rScaleX = scaleX;
			rScaleY = scaleY;
			rScaleZ = scaleZ;
		}
	}

	protected void extendRenderProps() {
		if (this.parent == null) return;

		Vec3d myPos = new Vec3d(rX * parent.rScaleX, rY * parent.rScaleY, rZ * parent.rScaleZ);
		if (parent.rRotationZ != 0) myPos = MathSupporter.rotation(myPos, AXIS_Z, parent.rRotationZ / 180 * 3.1415926);

		rX = parent.rX + myPos.x;
		rY = parent.rY + myPos.y;
		rZ = parent.rZ + myPos.z;

		if (hasScale) {
			rScaleX = parent.rScaleX * rScaleX;
			rScaleY = parent.rScaleY * rScaleY;
			rScaleZ = parent.rScaleZ * rScaleZ;
		} else {
			rScaleX = parent.rScaleX;
			rScaleY = parent.rScaleY;
			rScaleZ = parent.rScaleZ;
		}

		if (rScaleX != 1 || rScaleY != 1 || rScaleZ != 1) hasScale = true;

		rRotationZ = parent.rRotationZ + rRotationZ;
	}

	public void draw(float partialTicks) {
		if (!visible) return;
		updateRenderProps(partialTicks);
		GlStateManager.color(color.r, color.g, color.b, rAlpha);
		GlStateManager.translate(rX, rY, rZ);
		if (hasScale) GlStateManager.scale(rScaleX, rScaleY, rScaleZ);
		if (hasRotation) GlStateManager.rotate(rRotationZ, 0, 0, 1);
		render(partialTicks);
		for (GNode node : this.children) node.draw(partialTicks);
		if (hasRotation) GlStateManager.rotate(-rRotationZ, 0, 0, 1);
		if (hasScale) GlStateManager.scale(1 / rScaleX, 1 / rScaleY, 1 / rScaleZ);
		GlStateManager.translate(-rX, -rY, -rZ);
	}

	protected void draw(BufferBuilder bufferbuilder, float partialTicks) {
		updateRenderProps(partialTicks);
		extendRenderProps();
		render(bufferbuilder, partialTicks);
		for (GNode node : this.children) node.draw(bufferbuilder, partialTicks);
	}

	protected void render(float partialTicks) {

	}

	protected void render(BufferBuilder bufferbuilder, float partialTicks) {

	}

	@Override
	public String toString() {
		return "node " + this.name;
	}
}
