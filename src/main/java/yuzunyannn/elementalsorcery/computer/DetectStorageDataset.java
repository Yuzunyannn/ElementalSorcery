package yuzunyannn.elementalsorcery.computer;

import java.util.HashMap;
import java.util.Map;

import yuzunyannn.elementalsorcery.computer.StorageMonitor.Node;

public class DetectStorageDataset {

	protected Map<String, Node> children = new HashMap<>();

	void add() {

	}

	Node structure(Node node, Node detectNodeParent) {
		Node myNode = node.copy(detectNodeParent, true);
		Map<String, Node> children = detectNodeParent == null ? this.children : detectNodeParent.children;
		children.put(myNode.key, myNode);
		return myNode;
	}
}
