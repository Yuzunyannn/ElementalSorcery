package yuzunyannn.elementalsorcery.render.effect;

public abstract class EffectGroup implements Iterable<Effect> {

	abstract public void update();

	abstract public void add(Effect e);

	public boolean isGUI() {
		return false;
	}

	protected abstract void clear();

	protected abstract void render(float partialTicks);
}
