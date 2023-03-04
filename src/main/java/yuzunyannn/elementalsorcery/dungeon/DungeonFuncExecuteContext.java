package yuzunyannn.elementalsorcery.dungeon;

import java.util.function.Consumer;

import yuzunyannn.elementalsorcery.api.gfunc.GameFunc;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;

public class DungeonFuncExecuteContext extends GameFuncExecuteContext {

	public DungeonFuncExecuteContext(GameFunc currFunc, Consumer<GameFunc> exchanger) {
		super(currFunc, exchanger);
	}

	protected DungeonAreaRoom room;

	public DungeonAreaRoom getRoom() {
		return room;
	}

	public DungeonFuncExecuteContext setRoom(DungeonAreaRoom room) {
		this.room = room;
		return this;
	}
}
