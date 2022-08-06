package yuzunyannn.elementalsorcery.api.util.var;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class VTBlockPos implements IVariableType<BlockPos> {

	@Override
	public BlockPos newInstance(NBTBase base) {
		if (base instanceof NBTTagIntArray) {
			NBTTagIntArray array = (NBTTagIntArray) base;
			int[] ints = array.getIntArray();
			if (ints.length < 3) return BlockPos.ORIGIN;
			return new BlockPos(ints[0], ints[1], ints[2]);
		}
		return BlockPos.ORIGIN;
	}

	@Override
	public NBTBase serializable(BlockPos obj) {
		return new NBTTagIntArray(new int[] { obj.getX(), obj.getY(), obj.getZ() });
	}

	public BlockPos cast(Object obj) {
		if (obj instanceof Vec3d) return new BlockPos((Vec3d) obj);
		return (BlockPos) obj;
	};
}
