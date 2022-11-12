package yuzunyannn.elementalsorcery.api.mantra;

public interface IProgressable {

	double getProgress();

	void setProgress(double progress);

	default void addProgress(double progress) {
		setProgress(getProgress() + progress);
	}

}
