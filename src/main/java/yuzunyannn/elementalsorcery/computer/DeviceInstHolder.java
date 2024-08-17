package yuzunyannn.elementalsorcery.computer;

import java.util.Map;
import java.util.UUID;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.GameCast;
import yuzunyannn.elementalsorcery.api.util.ICastEnv;
import yuzunyannn.elementalsorcery.util.ds.WeakValueHashMap;

/**
 * 这个类是为了解决{@link yuzunyannn.elementalsorcery.computer.Computer#update()}中描述的问题<br/>
 * computer同一个UUID因为只能有一个实例，那么这个实例就会被存在弱值表中<br/>
 * 下次更新的时候，Item使用的引用如果值相同会被换成一个实例<br/>
 * 可能遇到的问题就是ItemStack被意外复制成了两份拥有同样UUID的物品，不过真出现了这样数据本身就会坏，有没有这个实例问题不大
 * 
 */
public class DeviceInstHolder {

	private static DeviceInstHolder server;
	@SideOnly(Side.CLIENT)
	private static DeviceInstHolder client;

	public static DeviceInstHolder from(boolean isRemote) {
		if (isRemote) return getClientInst();
		else {
			if (server == null) server = new DeviceInstHolder();
			return server;
		}
	}

	@SideOnly(Side.CLIENT)
	private static DeviceInstHolder getClientInst() {
		if (client == null) client = new DeviceInstHolder();
		return client;
	}

	Map<UUID, IDeviceHoldable> map = new WeakValueHashMap<>();

	public <T extends IDeviceHoldable<T>> T steal(UUID key, T obj, Class<T> cls) {
		if (obj.inHold()) return obj;
		T last = this.pop(key, cls);
		if (last != null) obj = obj.changeInstance(last);
		hold(key, obj);
		return obj;
	}

	public void hold(UUID key, IDeviceHoldable obj) {
		if (obj.inHold()) return;
		IDeviceHoldable old = map.put(key, obj);
		if (old != null) old.setInHold(false);
		obj.setInHold(true);
	}

	public <T extends IDeviceHoldable> T pop(UUID key, Class<T> cls) {
		IDeviceHoldable obj = map.remove(key);
		if (obj == null) return null;
		obj.setInHold(false);
		return GameCast.cast(ICastEnv.EMPTY, obj, cls);
	}

}
