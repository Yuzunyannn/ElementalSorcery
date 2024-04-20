package yuzunyannn.elementalsorcery.api.computer;

import javax.annotation.Nullable;

import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.GameCast;
import yuzunyannn.elementalsorcery.api.util.ICastEnv;

public class DNParams extends DNBase implements ICastEnv {

	public static DNParams empty() {
		return new DNParams();
	}

	protected IDevice src;
	protected World world;

	public DNParams() {
	}

	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Nullable
	public IDevice getSrcDevice() {
		return src;
	}

	public void setSrcDevice(IDevice from) {
		src = from;
	}

	public <T> T ask(String key, Class<T> cls) {
		Object obj = objMap.get(key);
		if (obj == null) return null;
		if (cls.isAssignableFrom(obj.getClass())) return (T) obj;
		return GameCast.cast(this, obj, cls);
	}

}