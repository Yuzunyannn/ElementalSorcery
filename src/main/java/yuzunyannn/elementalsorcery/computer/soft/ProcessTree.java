package yuzunyannn.elementalsorcery.computer.soft;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.util.var.IVariableType;

public class ProcessTree implements INBTSerializable<NBTTagCompound> {

	public static class VTProcessMap implements IVariableType<ProcessTree> {

		@Override
		public ProcessTree newInstance(NBTBase base) {
			ProcessTree map = new ProcessTree();
			if (base != null) map.deserializeNBT((NBTTagCompound) base);
			return map;
		}

		@Override
		public NBTBase serializable(ProcessTree obj) {
			return obj.serializeNBT();
		}

	}

	protected int pidCounter;

	public int newProcess(String appId) {
		
//		APP app = APP.REGISTRY.newInstance(new ResourceLocation(appId), this, pid);
		// TODO
		
		return 0;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {

	}

}
