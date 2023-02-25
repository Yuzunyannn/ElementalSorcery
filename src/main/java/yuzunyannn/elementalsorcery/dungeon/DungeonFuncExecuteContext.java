package yuzunyannn.elementalsorcery.dungeon;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DungeonFuncExecuteContext {

	public static enum DungeonFuncExecuteType {
		BUILD;
	}

	public final DungeonFuncExecuteType executeType;
	protected DungeonFunc currFunc;
	protected DungeonAreaRoom room;
	protected World world;
	protected BlockPos pos;
	protected DungeonFunc newFunc;
	protected Random rand = new Random();

	public DungeonFuncExecuteContext(DungeonFuncExecuteType executeType, DungeonFunc currFunc) {
		this.executeType = executeType;
		this.currFunc = currFunc;
	}

	public void doExecute() {
		this.currFunc.execute(this);
	}

	public Random getRand() {
		return rand;
	}

	public DungeonFuncExecuteContext setRand(Random rand) {
		this.rand = rand;
		return this;
	}

	public DungeonFunc getCurrFunc() {
		return currFunc;
	}

	public World getWorld() {
		return world;
	}

	public DungeonFuncExecuteContext setWorld(World world) {
		this.world = world;
		return this;
	}

	public BlockPos getBlockPos() {
		return this.pos;
	}

	public DungeonFuncExecuteContext setBlockPos(BlockPos pos) {
		this.pos = pos;
		return this;
	}

	public DungeonAreaRoom getRoom() {
		return room;
	}

	public DungeonFuncExecuteContext setRoom(DungeonAreaRoom room) {
		this.room = room;
		return this;
	}

}
