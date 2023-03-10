package yuzunyannn.elementalsorcery.api.element;

public interface IExplosionExecutor {

	float getExplosionSize();

	long getRandSeed();

	void setRandSeed(long seed);

	void doExplosionBlock();

	void doExplosionEntity();

}
