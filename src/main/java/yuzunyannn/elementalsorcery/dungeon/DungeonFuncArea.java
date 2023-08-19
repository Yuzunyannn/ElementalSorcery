package yuzunyannn.elementalsorcery.dungeon;

import java.util.Random;
import java.util.function.Function;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.api.gfunc.GameFunc;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncFinOp;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncJsonCreateContext;
import yuzunyannn.elementalsorcery.util.SeedRandom;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncArea extends GameFunc {

	GameFunc func;
	protected DungeonIntegerLoader range = DungeonIntegerLoader.of(3);
	protected DungeonIntegerLoader onceTryTimes = DungeonIntegerLoader.of(3);

	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		super.loadFromJson(json, context);
		range = DungeonIntegerLoader.get(json, "range", 3);
		onceTryTimes = DungeonIntegerLoader.get(json, "onceTryTimes", 3);
		func = GameFunc.create(json.getObject("func"));
	}

	@Override
	protected void execute(GameFuncExecuteContext originContext) {
		BlockPos originPos = originContext.getBlockPos();
		Random rand = getCurrRandom();

		int times = onceTryTimes.getInteger(rand.nextInt());

		for (int i = 0; i < times; i++) {

			int range = this.range.getInteger(rand.nextInt());

			BlockPos at = BlockHelper.tryFind(originContext.getWorld(), (w, pos) -> {
				if (BlockHelper.isSolidBlock(w, pos)) return BlockHelper.isReplaceBlock(w, pos.up());
				return false;
			}, originPos, 8, range, 2);

			if (at == null) continue;

			GameFuncExecuteContext context = originContext.toSubfunc(func);
			context.setBlockPos(at.up());
			func = context.doExecute(func);

			if (func == NOTHING) return;
		}

	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		func = GameFunc.create(nbt.getCompoundTag("func"));
		range = DungeonIntegerLoader.get(nbt.getTag("range"), 3);
		onceTryTimes = DungeonIntegerLoader.get(nbt.getTag("onceTryTimes"), 3);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setTag("func", func.serializeNBT());
		nbt.setTag("range", range.serializeNBT());
		nbt.setTag("onceTryTimes", onceTryTimes.serializeNBT());
		return nbt;
	}

	@Override
	public GameFuncFinOp afterExecute(GameFuncExecuteContext context) {
		if (func == NOTHING) return GameFuncFinOp.ABANDON;
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
		return "<Area> func: " + func;
	}

}
