package yuzunyannn.elementalsorcery.nodegui;

public class GActionFadeTo extends GActionFadeBy {

	protected float endAlpha;

	public GActionFadeTo(int tick, float endAlpha) {
		super(tick, 0);
		this.endAlpha = endAlpha;
	}

	@Override
	public void onStart(GNode node) {
		super.onStart(node);
		this.deltaAlpha = this.endAlpha - this.startAlpha;
	}

}
