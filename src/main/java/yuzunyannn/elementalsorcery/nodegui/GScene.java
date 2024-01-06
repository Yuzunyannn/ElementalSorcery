package yuzunyannn.elementalsorcery.nodegui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderRect;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;

@SideOnly(Side.CLIENT)
public class GScene {

	protected GNode root = new GNode();
	protected List<GNode> interactorList = new ArrayList<>();
	protected LinkedList<GNode> mouseList = new LinkedList<>();
	protected Set<GNode> hoverSet = new HashSet<>();
	protected Map<GNode, Boolean> interactorMap = new IdentityHashMap();
	protected int displayWidth = 1, displayHeight = 1;
	protected int width = 1, height = 1;

	public GScene() {
		this.root.scene = this;
		this.root.updateGlobalProps();
	}

	public void setDisplaySize(int w, int h) {
		this.displayWidth = w;
		this.displayHeight = h;
	}

	public void setSize(int w, int h) {
		this.width = w;
		this.height = h;
	}

	public void addChild(GNode node) {
		this.root.addChild(node);
	}

	public void tick() {
		this.root.update();
	}

	public void draw(float partialTicks) {
		root.draw(partialTicks);
	}

	public void onMouseEvent(Vec3d worldPos) {
		boolean isClick = Mouse.getEventButtonState();
		int btnId = Mouse.getEventButton();

		if (isClick) {
			for (GNode node : interactorList) {
				IGInteractor interactor = node.interactor;
				if (interactor.testHit(node, worldPos)) {
					boolean isHandler = interactor.onMousePressed(node, worldPos);
					if (isHandler) mouseList.add(node);
					if (interactor.blockMousePressed(node, worldPos)) break;
				}
			}
		} else if (btnId != -1) {
			for (GNode node : mouseList) node.interactor.onMouseReleased(node, worldPos);
			mouseList.clear();
			handleHover(worldPos);
		} else {
			if (!mouseList.isEmpty()) {
				for (GNode node : mouseList) node.interactor.onMouseDrag(node, worldPos);
			} else {
				handleHover(worldPos);
//				for (GNode node : interactorList) {
//					IGInteractor interactor = node.interactor;
//					interactor.onMouseHover(node, worldPos, interactor.testHit(node, worldPos));
//				}
			}
		}
	}

	public void handleHover(Vec3d worldPos) {
		Iterator<GNode> iter = hoverSet.iterator();
		while (iter.hasNext()) {
			GNode node = iter.next();
			if (!node.testHit(worldPos)) {
				IGInteractor interactor = node.interactor;
				interactor.onMouseHover(node, worldPos, false);
				iter.remove();
			}
		}
		if (!hoverSet.isEmpty()) return;
		for (GNode node : interactorList) {
			IGInteractor interactor = node.interactor;
			if (interactor.isHoverable(node) && interactor.testHit(node, worldPos)) {
				interactor.onMouseHover(node, worldPos, true);
				hoverSet.add(node);
				break;
			}
		}
	}

	public void addInteractNode(GNode node) {
		if (node.interactor == null) return;
		if (interactorMap.containsKey(node)) return;
		int i = MathSupporter.binarySearch(interactorList, (s) -> s.gZ - node.gZ);
		if (i < 0) i = -i - 1;
		interactorList.add(i, node);
		interactorMap.put(node, true);
	}

	public void removeInteractNode(GNode node) {
		if (!interactorMap.containsKey(node)) return;
		interactorMap.remove(node);
		interactorList.remove(node);
		mouseList.remove(node);
	}

	public void clear() {
		this.root.removeAllChild();
	}

	protected LinkedList<RenderRect> scissorStack = new LinkedList<RenderRect>();

	public void pushScissor(RenderRect rect) {
		if (scissorStack.isEmpty()) GL11.glEnable(GL11.GL_SCISSOR_TEST);
		scissorStack.push(rect);
		doScissor(rect);
	}

	public void popScissor() {
		scissorStack.pop();
		if (scissorStack.isEmpty()) GL11.glDisable(GL11.GL_SCISSOR_TEST);
		else doScissor(scissorStack.getFirst());
	}

	protected void doScissor(RenderRect rect) {
		float xScale = displayWidth / (float) this.width;
		float yScale = displayHeight / (float) this.height;
		float width = rect.right - rect.left;
		float height = rect.bottom - rect.top;
		int rx = (int) (xScale * rect.left);
		int ry = (int) (yScale * rect.top);
		int rw = (int) (xScale * width);
		int rh = (int) (yScale * height);
		GL11.glScissor(rx, ry, rw, rh);
	}
}
