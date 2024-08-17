package yuzunyannn.elementalsorcery.dungeon;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.function.BiConsumer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.util.var.Variable;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;

public final class DungeonCategory implements INBTSerializable<NBTTagCompound> {

	public static final Variable<VariableSet> LABEL_MAPPING = new Variable<>("L->P", VariableSet.VAR_SET);

	final VariableSet extra;
	World world;

	public DungeonCategory(VariableSet extra) {
		this.extra = extra;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return extra.serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		extra.deserializeNBT(nbt);
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void setLabelPos(String label, BlockPos pos) {
		VariableSet map = this.extra.get(LABEL_MAPPING);
		map.set(label, pos, VariableSet.BLOCK_POS);
	}

	public BlockPos getPosByLabel(String label) {
		VariableSet map = this.extra.get(LABEL_MAPPING);
		if (!map.has(label)) return null;
		return map.get(label, VariableSet.BLOCK_POS);
	}

	@FunctionalInterface
	public interface SerializableBiConsumer<T, U> extends BiConsumer<T, U>, Serializable {
	}

	public void addOnCompleteTask(SerializableBiConsumer<DungeonCategory, VariableSet> task, VariableSet params) {
		LinkedList<VariableSet> callbacks = this.extra.get("$Complete", VariableSet.VAR_SET_LINKED_LIST);
		VariableSet set = new VariableSet();
		set.set("callback", task, VariableSet.JOBJ);
		set.set("params", params, VariableSet.VAR_SET);
		callbacks.add(set);
	}

	public Runnable popCompleteTask() {
		LinkedList<VariableSet> callbacks = this.extra.get("$Complete", VariableSet.VAR_SET_LINKED_LIST);
		if (callbacks.isEmpty()) return null;
		VariableSet task = callbacks.pop();
		try {
			final VariableSet params = task.get("params", VariableSet.VAR_SET);
			final BiConsumer<DungeonCategory, VariableSet> callback = (BiConsumer<DungeonCategory, VariableSet>) task.get("callback", VariableSet.JOBJ);
			if (callback == null) throw new NullPointerException();
			return (Runnable) () -> callback.accept(this, params);
		} catch (Exception e2) {
			ESAPI.logger.warn("反序列化异常", e2);
			return (Runnable) () -> {};
		}
	}

}
