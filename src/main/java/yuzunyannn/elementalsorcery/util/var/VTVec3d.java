package yuzunyannn.elementalsorcery.util.var;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class VTVec3d implements IVariableType<Vec3d> {

	@Override
	public Vec3d newInstance(NBTBase base) {
		if (base instanceof NBTTagIntArray) {
			NBTTagIntArray array = (NBTTagIntArray) base;
			int[] ints = array.getIntArray();
			if (ints.length < 3) return Vec3d.ZERO;
			return new Vec3d(Float.intBitsToFloat(ints[0]), Float.intBitsToFloat(ints[1]),
					Float.intBitsToFloat((ints[2])));
		}
		return Vec3d.ZERO;
	}

	@Override
	public NBTBase serializable(Vec3d obj) {
		return new NBTTagIntArray(new int[] { Float.floatToIntBits((float) obj.x), Float.floatToIntBits((float) obj.y),
				Float.floatToIntBits((float) obj.z) });
	}

	public Vec3d cast(Object obj) {
		if (obj instanceof Vec3i) return new Vec3d((Vec3i) obj);
		return (Vec3d) obj;
	};
}
