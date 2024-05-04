package yuzunyannn.elementalsorcery.nodegui;

public interface IGNodeLayoutable {

	void layout();

	public void setMaxWidth(double maxWidth);

	default public void setResidueWidth(double residueWidth) {
	}

	public double getMaxWidth();
}
