package yuzunyannn.elementalsorcery.api.computer;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.GameCast;
import yuzunyannn.elementalsorcery.api.util.GameDisplayCast;
import yuzunyannn.elementalsorcery.api.util.ICastEnv;

public class DNRequest extends DNBase implements ICastEnv {

	public static DNRequest empty() {
		return new DNRequest();
	}

	protected IDevice src;
	protected World world;
	protected Map<Class<?>, Function<String, ?>> finderMap;
	protected List<Object> logList;

	public DNRequest() {
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

	@Nullable
	public IDevice getSrcDevice() {
		return src;
	}

	public void setSrcDevice(IDevice from) {
		src = from;
	}

	public void setLogList(List<Object> logList) {
		this.logList = logList;
	}

	public List<Object> getLogList() {
		return logList;
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