package yuzunyannn.elementalsorcery.dungeon;

import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;

public class DungeonFuncExecuteContext extends GameFuncExecuteContext {

	protected DungeonAreaRoom room;

	public DungeonAreaRoom getRoom() {
		return room;
	}

	public DungeonFuncExecuteContext setRoom(DungeonAreaRoom room) {
		this.room = room;
		return this;
	}
}
