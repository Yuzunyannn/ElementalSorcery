package yuzunyannn.elementalsorcery.api.computer;

public interface IDeviceLinkTimeoutable {

	public boolean tryReconnect(IDeviceEnv env, int dtick);

}
