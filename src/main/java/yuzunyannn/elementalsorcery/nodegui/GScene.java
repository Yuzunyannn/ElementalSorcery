package yuzunyannn.elementalsorcery.nodegui;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;

@SideOnly(Side.CLIENT)
public class GScene {

	protected GNode root = new GNode();
	protected List<GNode> interactorList = new ArrayList<>();
	protected GNode cNode = null;
	protected Map<GNode, Boolean> interactorMap = new IdentityHashMap();

	public GScene() {
		this.root.scene = this;
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
				if (node.testHit(worldPos)) {
					IGInteractor interactor = node.interactor;
					boolean isHandler = interactor.onMousePressed(node, worldPos);
					if (isHandler) {
						cNode = node;
						break;
					}
				}
			}
		} else if (btnId != -1) {
			if (cNode != null) {
				cNode.interactor.onMouseReleased(cNode, worldPos);
				cNode = null;
			}
		} else {
			if (cNode != null) cNode.interactor.onMouseDrag(cNode, worldPos);
			else {
				for (GNode node : interactorList) {
					IGInteractor interactor = node.interactor;
					interactor.onMouseHover(node, worldPos, node.testHit(worldPos));
				}
			}
		}
	}

	public void addInteractNode(GNode node) {
		if (node.interactor == null) return;
		if (interactorMap.containsKey(node)) return;
		int i = MathSupporter.binarySearch(interactorList, (s) -> node.z - s.z);
		if (i < 0) i = -i - 1;
		interactorList.add(i, node);
		interactorMap.put(node, true);
	}

	public void removeInteractNode(GNode node) {
		if (!interactorMap.containsKey(node)) return;
		interactorMap.remove(node);
		interactorList.remove(node);
		if (cNode == node) cNode = null;
	}

	public void clear() {
		this.root.removeAllChild();
	}
}
