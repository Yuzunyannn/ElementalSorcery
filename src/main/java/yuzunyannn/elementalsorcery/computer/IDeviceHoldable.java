package yuzunyannn.elementalsorcery.computer;

public interface IDeviceHoldable<T> {

	public boolean inHold();

	public void setInHold(boolean hold);

	public T changeInstance(T newInst);

}
