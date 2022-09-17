package yuzunyannn.elementalsorcery.api.mantra;

import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;

public interface ICasterObject extends IWorldObject {

	default void setPositionVector(Vec3d pos) {
		setPositionVector(pos, true);
	}

	void setPositionVector(Vec3d pos, boolean force);
}
