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

	protected double x, y, z;
	protected double prevX, prevY, prevZ;
	protected double scaleX = 1, scaleY = 1, scaleZ = 1;
	protected double prevScaleX = 1, prevScaleY = 1, prevScaleZ = 1;
	protected float anchorX, anchorY, anchorZ;
	protected float rotationZ;
	protected float prevRotationZ;
	protected boolean hasRotation = false;
	protected boolean hasScale = false;
	protected double width = 1, height = 1, depth;
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

	public void setGaps(boolean gaps) {
		this.gaps = gaps;
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

	public void setPosition(int x, int y, int z) {
		setPosition((double) x, (double) y, (double) z);
	}

	public void setPosition(Vec3d vec) {
		setPosition(vec.x, vec.y, vec.z);
	}

	public void setPosition(Vec3i vec) {
		setPosition(vec.getX(), vec.getY(), vec.getZ());
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

	private class WorldPosScaleGetter {
		Vec3d pos;
		double scaleX = 1, scaleY = 1, scaleZ = 1;
	}

	private WorldPosScaleGetter doWorldPosScaleGetter(WorldPosScaleGetter getter) {
		if (this.parent == null) {
			getter.pos = new Vec3d(x, y, z);
			return getter;
		}
		getter = this.parent.doWorldPosScaleGetter(getter);
		Vec3d myPos = new Vec3d(x * getter.scaleX, y * getter.scaleY, z * getter.scaleZ);
		if (this.parent.hasRotation)
			myPos = MathSupporter.rotation(myPos, AXIS_Z, this.parent.rotationZ / 180 * 3.1415926);
		getter.pos = getter.pos.add(myPos);
		if (hasScale) {
			getter.scaleX = getter.scaleX * this.scaleX;
			getter.scaleY = getter.scaleY * this.scaleY;
			getter.scaleZ = getter.scaleZ * this.scaleZ;
		}
		return getter;
	}

	public Vec3d getPostionInWorldPos() {
		WorldPosScaleGetter getter = doWorldPosScaleGetter(new WorldPosScaleGetter());
		return getter.pos;
	}

	public float getRotationInWorldPos() {
		if (this.parent == null) return this.rotationZ;
		return this.rotationZ + this.parent.getRotationInWorldPos();
	}

	public void addChild(GNode node) {
		node.removeFromParent();
		int i = MathSupporter.binarySearch(children, (s) -> node.z - s.z);
		if (i < 0) i = -i - 1;
		children.add(i, node);
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

	protected void onExitScene() {
		for (GNode node : this.children) node.onExitScene();
		this.scene.removeInteractNode(this);
		this.scene = null;
	}

	protected void onEnterScene(GScene scene) {
		this.scene = scene;
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
		WorldPosScaleGetter getter = doWorldPosScaleGetter(new WorldPosScaleGetter());

		Vec3d vec = getter.pos;
		vec = worldPos.subtract(vec);

		float width = (float) (this.width * getter.scaleX);
		float height = (float) (this.height * getter.scaleY);

		float aw = width * anchorX;
		float ah = height * anchorY;

		float left = -aw;
		float right = width - aw;
		float top = -ah;
		float bottom = height - ah;

		float rotation = this.getRotationInWorldPos();
		if (rotation != 0) vec = MathSupporter.rotation(vec, AXIS_Z, -rotation / 180 * 3.1415926);

		return vec.x >= left && vec.x <= right && vec.y >= top && vec.y <= bottom;
	}

	protected boolean inUpdate;

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
		int i = 0;
		while (i < children.size()) {
			GNode node = children.get(i);
			node.update();
			if (children.isEmpty()) break;
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

	protected double dx, dy, dz;
	protected double dScaleX, dScaleY, dScaleZ;
	protected float dRotationZ;
	protected float dAlpha;

	protected void refreshDrawParams(float partialTicks) {
		dAlpha = alpha * (parent == null ? 1 : parent.dAlpha);
		if (this.gaps) {
			dx = RenderFriend.getPartialTicks(x, prevX, partialTicks);
			dy = RenderFriend.getPartialTicks(y, prevY, partialTicks);
			dz = RenderFriend.getPartialTicks(z, prevZ, partialTicks);
			if (hasRotation) dRotationZ = RenderFriend.getPartialTicks(rotationZ, prevRotationZ, partialTicks);
			if (hasScale) {
				dScaleX = RenderFriend.getPartialTicks(scaleX, prevScaleX, partialTicks);
				dScaleY = RenderFriend.getPartialTicks(scaleY, prevScaleY, partialTicks);
				dScaleZ = RenderFriend.getPartialTicks(scaleZ, prevScaleZ, partialTicks);
			}
		} else {
			dx = this.x;
			dy = this.y;
			dz = this.z;
			if (hasRotation) dRotationZ = this.rotationZ;
			if (hasScale) {
				dScaleX = scaleX;
				dScaleY = scaleY;
				dScaleZ = scaleZ;
			}
		}
	}

	public void draw(float partialTicks) {
		refreshDrawParams(partialTicks);
		GlStateManager.color(color.r, color.g, color.b, dAlpha);
		GlStateManager.translate(dx, dy, dz);
		if (hasScale) GlStateManager.scale(dScaleX, dScaleY, dScaleZ);
		if (hasRotation) GlStateManager.rotate(dRotationZ, 0, 0, 1);
		this.render(partialTicks);
		for (GNode node : this.children) node.draw(partialTicks);
		if (hasRotation) GlStateManager.rotate(-dRotationZ, 0, 0, 1);
		if (hasScale) GlStateManager.scale(-dScaleX, -dScaleY, -dScaleZ);
		GlStateManager.translate(-dx, -dy, -dz);
	}

	protected void draw(BufferBuilder bufferbuilder, float partialTicks) {
		refreshDrawParams(partialTicks);
		render(bufferbuilder, partialTicks);
	}

	protected void render(float partialTicks) {

	}

	protected void render(BufferBuilder bufferbuilder, float partialTicks) {

	}
}
