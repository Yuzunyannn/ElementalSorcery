package yuzunyannn.elementalsorcery.api.computer;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.GameCast;
import yuzunyannn.elementalsorcery.api.util.ICastEnv;
import yuzunyannn.elementalsorcery.api.util.render.GameDisplayCast;

public class DNRequest extends DNBase implements ICastEnv {

	public static DNRequest empty() {
		return new DNRequest();
	}

	protected World world;
	protected Map<Class<?>, Function<String, ?>> finderMap;
	protected List<Object> logList;
	protected LinkedList<IDevice> srcs = new LinkedList<>();
	protected int extCount;
	protected int authority;

	public DNRequest() {
	}

	public void setAuthority(int authority) {
		this.authority = authority;
	}

	public int getAuthority() {
		return authority;
	}

	public int pSize() {
		return objMap.size() - extCount;
	}

	public void setExtCount(int extCount) {
		this.extCount = extCount;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public <T> T find(String hint, Class<T> cls) {
		if (finderMap == null) return null;
		Function<String, T> finder = (Function<String, T>) finderMap.get(cls);
		if (finder == null) return null;
		return finder.apply(hint);
	}

	public <T> void setFinder(Class<T> cls, Function<String, T> finder) {
		if (finderMap == null) finderMap = new IdentityHashMap<>();
		finderMap.put(cls, finder);
	}

	public void pushDevice(IDevice from) {
		srcs.push(from);
	}

	@Nullable
	public IDevice getBeginDevice() {
		if (srcs.isEmpty()) return null;
		return srcs.getLast();
	}

	public LinkedList<IDevice> getDeviceRoute() {
		return srcs;
	}

	public void setLogList(List<Object> logList) {
		this.logList = logList;
	}

	public List<Object> getLogList() {
		return logList;
	}

	public boolean isLogEnable() {
		return logList != null;
	}

	public void log(Object obj) {
		if (logList == null) return;
		logList.add(GameDisplayCast.cast(obj));
	}

	public <T> T ask(String key, Class<T> cls) {
		Object obj = objMap.get(key);
		if (obj == null) return null;
		if (cls.isAssignableFrom(obj.getClass())) return (T) obj;
		return GameCast.cast(this, obj, cls);
	}

}