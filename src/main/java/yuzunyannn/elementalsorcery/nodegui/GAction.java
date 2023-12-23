package yuzunyannn.elementalsorcery.nodegui;

public class GAction {

	protected boolean isStart = false;

	public boolean isStart() {
		return isStart;
	}

	public void onStart(GNode node) {
		isStart = true;
	}

	public void reset(GNode node) {
		isStart = false;
	}

	public boolean isOver() {
		return true;
	}

	public void update(GNode node) {

	}

}
