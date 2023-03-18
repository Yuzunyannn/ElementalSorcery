package yuzunyannn.elementalsorcery.api.gfunc;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class GameFuncTimes extends GameFunc {

	protected int times;

	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		super.loadFromJson(json, context);
		times = json.hasNumber("times") ? json.getNumber("times").intValue() : 1;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		times = nbt.getInteger("times");
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		if (times != 0) nbt.setInteger("times", times);
		return nbt;
	}

	@Override
	public GameFuncFinOp afterExecute(GameFuncExecuteContext context) {
		if (times < 0) return GameFuncFinOp.ABANDON;
		if (--times <= 0) return GameFuncFinOp.ABANDON;
		return GameFuncFinOp.KEEP;
	}

}
