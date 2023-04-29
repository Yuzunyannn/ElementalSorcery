package yuzunyannn.elementalsorcery.api.gfunc;

import java.util.function.Function;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.util.SeedRandom;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class GameFuncRepeat extends GameFuncTimes {

	GameFunc func;
	GameFunc currFunc = GameFunc.NOTHING;

	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		super.loadFromJson(json, context);
		func = GameFunc.create(json.getObject("func"));
	}

	@Override
	protected void execute(GameFuncExecuteContext originContext) {
		if (currFunc == GameFunc.NOTHING) currFunc = func.copy();
		if (currFunc == GameFunc.NOTHING) return;
		
		GameFuncExecuteContext context = originContext.toSubfunc(currFunc);
		currFunc = context.doExecute(currFunc);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		func = GameFunc.create(nbt.getCompoundTag("func"));
		currFunc = GameFunc.create(nbt.getCompoundTag("currFunc"));
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setTag("func", func.serializeNBT());
		nbt.setTag("currFunc", currFunc.serializeNBT());
		return nbt;
	}

	@Override
	public GameFuncFinOp afterExecute(GameFuncExecuteContext context) {
		if (func == NOTHING) return GameFuncFinOp.ABANDON;
		if (currFunc == NOTHING) {
			if (times < 0) return GameFuncFinOp.ABANDON;
			if (--times <= 0) return GameFuncFinOp.ABANDON;
		}
		return GameFuncFinOp.KEEP;
	}

	@Override
	public void setSeed(long seed) {
		super.setSeed(seed);
		func.setSeed(SeedRandom.nextSeed(seed, 49937));
	}

	@Override
	public GameFunc visit(Function<GameFunc, GameFunc> visitor) {
		GameFunc nFunc = visitor.apply(this);
		if (nFunc != this) return nFunc;
		nFunc = func.visit(visitor);
		if (nFunc == func) return this;
		if (nFunc == NOTHING) return NOTHING;
		func = nFunc;
		return this;
	}

	@Override
	public String toString() {
		return "<repeat: " + times + "x" + func;
	}

}
