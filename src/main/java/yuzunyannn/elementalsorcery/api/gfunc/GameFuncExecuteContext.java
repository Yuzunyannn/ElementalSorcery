package yuzunyannn.elementalsorcery.api.gfunc;

import java.util.Random;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.WorldObjectBlock;
import yuzunyannn.elementalsorcery.api.util.WorldObjectEntity;

public class GameFuncExecuteContext {

	protected Consumer<GameFunc> exchanger;
	protected Random rand = new Random();
	protected GameFunc func;
	protected IWorldObject srcObj;
	protected IWorldObject triggerObj;
	protected Event event;

	public GameFuncExecuteContext(GameFunc currFunc, Consumer<GameFunc> exchanger) {
		this.exchanger = exchanger;
		this.func = currFunc;
	}

	public void doExecute() {
		this.func.execute(this);
		GameFuncFinOp op = this.func.afterExecute(this);
		if (op == GameFuncFinOp.ABANDON) this.exchanger.accept(GameFunc.NOTHING);
	}

	public Random getRand() {
		return rand;
	}

	public GameFuncExecuteContext setRand(Random rand) {
		this.rand = rand;
		return this;
	}

	public GameFunc getCurrFunc() {
		return func;
	}

	public Event getEvent() {
		return event;
	}

	public GameFuncExecuteContext setByEvent(Event event) {
		this.event = event;
		return this;
	}

	@Nonnull
	public IWorldObject getSrcObj() {
		return srcObj;
	}

	@Nonnull
	public World getWorld() {
		return srcObj.getWorld();
	}

	@Nonnull
	public BlockPos getBlockPos() {
		return srcObj.getPosition();
	}

	@Nullable
	public Entity getEntity() {
		return srcObj.asEntity();
	}

	@Nullable
	public IWorldObject getTriggerObj() {
		return triggerObj;
	}

	public GameFuncExecuteContext setSrcMan(IWorldObject worldObj) {
		this.srcObj = worldObj;
		return this;
	}

	public GameFuncExecuteContext setSrcMan(World world, BlockPos pos) {
		this.srcObj = new WorldObjectBlock(world, pos);
		return this;
	}

	public GameFuncExecuteContext setSrcObj(Entity entity) {
		this.srcObj = new WorldObjectEntity(entity);
		return this;
	}

	public GameFuncExecuteContext setTriggerMan(IWorldObject worldObj) {
		this.triggerObj = worldObj;
		return this;
	}

	public GameFuncExecuteContext setTriggerObj(World world, BlockPos pos) {
		this.triggerObj = new WorldObjectBlock(world, pos);
		return this;
	}

	public GameFuncExecuteContext setTriggerMan(Entity entity) {
		this.triggerObj = new WorldObjectEntity(entity);
		return this;
	}

}
