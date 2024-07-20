package yuzunyannn.elementalsorcery.api.mantra;

import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.util.GameCast;
import yuzunyannn.elementalsorcery.api.util.ICastEnv;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;

public interface ICasterObject extends IWorldObject {

	default void setPositionVector(Vec3d pos) {
		setPositionVector(pos, true);
	}

	void setPositionVector(Vec3d pos, boolean force);
	
	default <T> T to(Class<T> cls) {
		return GameCast.cast(ICastEnv.EMPTY, this, cls);
	}
}
