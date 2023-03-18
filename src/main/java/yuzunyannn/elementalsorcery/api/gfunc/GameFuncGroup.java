package yuzunyannn.elementalsorcery.api.gfunc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.util.SeedRandom;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper.WeightRandom;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class GameFuncGroup extends GameFunc {

	public static NBTTagList serializeNBTList(List<GameFunc> list) {
		NBTTagList tagList = new NBTTagList();
		for (GameFunc func : list) tagList.appendTag(func.serializeNBT());
		return tagList;
	}

	public static List<GameFunc> deserializeNBTList(NBTTagList tagList) {
		List<GameFunc> list = new ArrayList<>();
		for (int i = 0; i < tagList.tagCount(); i++) {
			GameFunc func = create(tagList.getCompoundTagAt(i));
			list.add(func);
		}
		return list;
	}

	public static List<GameFunc> deserializeNBTList(NBTTagCompound nbt, String key) {
		return deserializeNBTList(nbt.getTagList(key, NBTTag.TAG_COMPOUND));
	}

	protected interface Dispatcher {
		void execute(GameFuncExecuteContext context);

		default void writeToNBT(NBTTagCompound nbt) {
		}

		default void readFromNBT(NBTTagCompound nbt) {
		}
	}

	protected List<GameFunc> funcs = new ArrayList<>();
	protected int mode;
	protected Dispatcher dispatcher = new Parallel();

	protected Dispatcher createDispatcher() {
		switch (mode) {
		case 1:
			return new Sequence();
		case 2:
			return new RandomDispatcher();
		case 3:
			return new WeightRandomDispatcher();
		default:
			return new Parallel();
		}
	}

	@Override
	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		super.loadFromJson(json,context);
		funcs.clear();
		JsonArray jfuncs = json.needArray("funcs");
		for (int i = 0; i < jfuncs.size(); i++) funcs.add(GameFunc.create(jfuncs.getObject(i)));
		mode = 0;
		if (json.hasNumber("mode")) mode = json.getNumber("mode").intValue();
		else if (json.hasString("mode")) {
			String modeStr = json.getString("mode");
			switch (modeStr) {
			case "sequence":
				mode = 1;
				break;
			case "random":
				mode = 2;
				for (GameFunc func : funcs) {
					if (func.hasConfig(WEIGHT)) {
						mode = 3;
						break;
					}
				}
				break;
			}
		}
		dispatcher = createDispatcher();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		funcs = deserializeNBTList(nbt, "funcs");
		mode = nbt.getInteger("mode");
		dispatcher = createDispatcher();
		dispatcher.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setTag("funcs", serializeNBTList(funcs));
		nbt.setInteger("mode", mode);
		dispatcher.writeToNBT(nbt);
		return nbt;
	}

	public GameFuncGroup addFunc(GameFunc func) {
		funcs.add(func);
		return this;
	}

	@Override
	protected void execute(GameFuncExecuteContext context) {
		dispatcher.execute(context);
	}

	@Override
	public GameFuncFinOp afterExecute(GameFuncExecuteContext context) {
		return funcs.isEmpty() ? GameFuncFinOp.ABANDON : GameFuncFinOp.KEEP;
	}

	@Override
	public void setSeed(long seed) {
		super.setSeed(seed);
		for (GameFunc func : funcs) {
			seed = SeedRandom.nextSeed(seed, 49937);
			func.setSeed(seed);
		}
	}

	@Override
	public GameFunc visit(Function<GameFunc, GameFunc> visitor) {
		GameFunc nFunc = visitor.apply(this);
		if (nFunc != this) return nFunc;
		for (int i = 0; i < funcs.size(); i++) {
			GameFunc func = funcs.get(i);
			nFunc = func.visit(visitor);
			if (nFunc == func) continue;
			if (nFunc == NOTHING) funcs.remove(i--);
			else funcs.set(i, nFunc);
		}
		return this;
	}

	@Override
	public String toString() {
		return "<group:" + dispatcher + "> " + funcs;
	}

	protected class Parallel implements Dispatcher {

		@Override
		public String toString() {
			return "parallel";
		}

		@Override
		public void execute(GameFuncExecuteContext originContext) {
			for (int i = 0; i < funcs.size(); i++) {
				GameFunc func = funcs.get(i);
				GameFuncExecuteContext context = originContext.toSubfunc(func);
				GameFunc nFunc = context.doExecute(func);
				if (nFunc == func) continue;
				if (nFunc == NOTHING) funcs.remove(i--);
				else funcs.set(i, nFunc);
			}
		}

	}

	protected class Sequence implements Dispatcher {

		@Override
		public String toString() {
			return "sequence";
		}

		@Override
		public void execute(GameFuncExecuteContext originContext) {
			if (funcs.isEmpty()) return;
			GameFunc func = funcs.get(0);
			GameFuncExecuteContext context = originContext.toSubfunc(func);
			GameFunc nFunc = context.doExecute(func);
			if (nFunc == func) return;
			if (nFunc == NOTHING) funcs.remove(0);
			else funcs.set(0, nFunc);
		}

	}

	protected class RandomDispatcher implements Dispatcher {

		@Override
		public String toString() {
			return "random";
		}

		@Override
		public void execute(GameFuncExecuteContext originContext) {
			if (funcs.isEmpty()) return;
			int index = getCurrRandom().nextInt(funcs.size());
			GameFunc func = funcs.get(index);
			GameFuncExecuteContext context = originContext.toSubfunc(func);
			GameFunc nFunc = context.doExecute(func);
			if (nFunc == func) return;
			if (nFunc == NOTHING) funcs.remove(index);
			else funcs.set(index, nFunc);
		}

	}

	protected class WeightRandomDispatcher implements Dispatcher {

		WeightRandom<Integer> wr = new WeightRandom();

		@Override
		public String toString() {
			return "random";
		}

		public void refreshWR() {
			wr = new WeightRandom();
			for (int i = 0; i < funcs.size(); i++) {
				GameFunc func = funcs.get(i);
				float weight = 1;
				if (func.hasConfig(WEIGHT)) weight = func.getConfig(WEIGHT);
				wr.add(i, weight);
			}
		}

		@Override
		public void execute(GameFuncExecuteContext originContext) {
			if (funcs.isEmpty()) return;
			if (wr.isEmpty()) refreshWR();
			int index = wr.get(getCurrRandom());
			GameFunc func = funcs.get(index);
			GameFuncExecuteContext context = originContext.toSubfunc(func);
			GameFunc nFunc = context.doExecute(func);
			if (nFunc == func) return;
			if (nFunc == NOTHING) {
				funcs.remove(index);
				refreshWR();
			} else funcs.set(index, nFunc);
		}

	}

}
