package yuzunyannn.elementalsorcery.api.element;

public interface IExplosionExecutor {

	float getExplosionSize();

	int getRandSeed();

	void setRandSeed(int seed);

	void doExplosionBlock();

	void doExplosionEntity();

}
