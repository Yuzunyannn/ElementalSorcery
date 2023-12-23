package yuzunyannn.elementalsorcery.nodegui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GActionSequence extends GAction {

	protected int currIndex;
	protected List<GAction> actions;

	public GActionSequence(GAction... actions) {
		this.actions = Arrays.asList(actions);
	}

	public GActionSequence() {
		this.actions = new ArrayList<>();
	}

	public GActionSequence add(GAction action) {
		try {
			this.actions.add(action);
		} catch (UnsupportedOperationException e) {
			this.actions = new ArrayList<>(this.actions);
			this.actions.add(action);
		}
		return this;
	}

	@Override
	public void onStart(GNode node) {
		super.onStart(node);
		this.currIndex = 0;
	}
	
	@Override
	public void reset(GNode node) {
		super.reset(node);
		this.currIndex = 0;
	}

	@Override
	public boolean isOver() {
		return currIndex >= this.actions.size();
	}

	@Override
	public void update(GNode node) {
		if (actions.isEmpty()) return;
		GAction action = actions.get(currIndex);
		if (!action.isStart) action.onStart(node);
		action.update(node);
		if (action.isOver()) currIndex++;
	}

}
