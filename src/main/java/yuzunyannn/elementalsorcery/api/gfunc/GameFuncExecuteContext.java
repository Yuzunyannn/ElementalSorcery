package yuzunyannn.elementalsorcery.api.gfunc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;
import yuzunyannn.elementalsorcery.api.event.ESEvent;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.target.WorldObjectEntity;

public class GameFuncExecuteContext {

	protected IWorldObject srcObj;
	protected IWorldObject triggerObj;
	protected Event event;

	protected BlockPos pos = null;

	public GameFunc doExecute(GameFunc func) {
		func = ESEvent.post(new GameFuncBeforeExecuteEvent(func, this)).getFunc();
		func.execute(this);
		GameFuncFinOp op = func.afterExecute(this);
		if (op == GameFuncFinOp.ABANDON) return GameFunc.NOTHING;
		return func;
	}

	public void assign(GameFuncExecuteContext config) {
		this.srcObj = config.srcObj;
		this.triggerObj = config.triggerObj;
		this.event = config.event;
	}

	public GameFuncExecuteContext toSubfunc(GameFunc subFunc) {
		return this;
	}

	public Event getEvent() {
		return event;
	}

	public GameFuncExecuteContext setByEvent(Event event) {
		this.event = event;
		return this;
	}

	@Nonnull
	public World getWorld() {
		return srcObj.getWorld();
	}

	@Nonnull
	public BlockPos getBlockPos() {
		return pos == null ? srcObj.getPosition() : pos;
	}

	public void setBlockPos(@Nullable BlockPos pos) {
		this.pos = pos;
	}

	@Nullable
	public Entity getEntity() {
		return srcObj.toEntity();
	}

	@Nonnull
	public IWorldObject getSrcObj() {
		return srcObj;
	}

	@Nullable
	public IWorldObject getTriggerObj() {
		return triggerObj;
	}

	public GameFuncExecuteContext setSrcObj(IWorldObject worldObj) {
		this.srcObj = worldObj;
		return this;
	}

	public GameFuncExecuteContext setSrcObj(World world, BlockPos pos) {
		this.srcObj = IWorldObject.of(world, pos);
		return this;
	}

	public GameFuncExecuteContext setSrcObj(Entity entity) {
		this.srcObj = IWorldObject.of(entity);
		return this;
	}

	public GameFuncExecuteContext setTriggerObj(IWorldObject worldObj) {
		this.triggerObj = worldObj;
		return this;
	}

	public GameFuncExecuteContext setTriggerObj(World world, BlockPos pos) {
		this.triggerObj = IWorldObject.of(world, pos);
		return this;
	}

	public GameFuncExecuteContext setTriggerObj(Entity entity) {
		this.triggerObj = entity == null ? null : new WorldObjectEntity(entity);
		return this;
	}

}
