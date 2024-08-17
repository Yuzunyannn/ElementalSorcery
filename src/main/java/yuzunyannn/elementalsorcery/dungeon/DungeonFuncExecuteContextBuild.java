package yuzunyannn.elementalsorcery.dungeon;

import java.util.HashMap;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.gfunc.GameFunc;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;

public class DungeonFuncExecuteContextBuild extends DungeonFuncExecuteContext {
	protected int index;
	protected GameFunc coreFunc;
	protected DungeonCategory category;

	public DungeonFuncExecuteContextBuild(DungeonAreaRoom room, int index, World world, BlockPos at) {
		this.setRoom(room);
		this.setSrcObj(world, at);
		this.index = index;
		this.coreFunc = room.getFunc(index);
		this.category = new DungeonCategory(extra = new VariableSet());
	}

	public void doExecute() {
		GameFunc nFunc = doExecute(coreFunc);
		if (index != room.funcGlobalIndex) nFunc = GameFunc.NOTHING;
		room.setFunc(index, nFunc);
	}

	public DungeonCategory getCategory() {
		return category;
	}

	@Override
	public void setExtra(VariableSet extra) {
		super.setExtra(extra);
		this.category = new DungeonCategory(getExtra());
	}

	public void setCategory(DungeonCategory category) {
		this.category = category;
		this.extra = category.extra;
	}

}
