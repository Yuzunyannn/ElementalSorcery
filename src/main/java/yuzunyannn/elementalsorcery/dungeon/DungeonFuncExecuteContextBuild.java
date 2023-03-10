package yuzunyannn.elementalsorcery.dungeon;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.gfunc.GameFunc;

public class DungeonFuncExecuteContextBuild extends DungeonFuncExecuteContext {
	protected int index;
	protected GameFunc coreFunc;

	public DungeonFuncExecuteContextBuild(DungeonAreaRoom room, int index, World world, BlockPos at) {
		this.setRoom(room);
		this.setSrcObj(world, at);
		this.index = index;
		this.coreFunc = room.getFunc(index);
	}

	public void doExecute() {
		GameFunc nFunc = doExecute(coreFunc);
		if (index != room.funcGlobalIndex) nFunc = GameFunc.NOTHING;
		room.setFunc(index, nFunc);
	}

}
