package yuzunyannn.elementalsorcery.tile;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTSS;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;

public class TileTaskManager implements INBTSS, Iterable<TileTask> {

	protected Function<Integer, TileTask> factory;
	private TileTask[] tasks;
	private int count;
	private int runtimeIdCounter;
	private int changeMagic = 0;

	public TileTaskManager(int taskCount, Function<Integer, TileTask> factory) {
		this.tasks = new TileTask[taskCount];
		this.factory = factory;
	}

	public boolean isEmpty() {
		return this.count == 0;
	}

	protected void addTask(TileTask task) {
		int tid = task.tid;
		if (tid < 0 || tid >= tasks.length) return;
		if (task.mgr != null) task.mgr.removeTask(task, false);
		if (tasks[tid] == null) tasks[tid] = task;
		else {
			task.next = tasks[tid];
			tasks[tid].prev = task;
			tasks[tid] = task;
		}
		task.mgr = this;
		count++;
		changeMagic++;
	}

	protected TileTask removeTask(TileTask task, boolean exit) {
		if (task == null) return null;
		if (task.mgr != this) return null;
		task.mgr = null;
		if (tasks[task.tid] == task) tasks[task.tid] = task.next;
		else {
			task.prev.next = task.next;
			if (task.next != null) task.next.prev = task.prev;
		}
		count--;
		changeMagic++;
		if (exit) task.onExit();
		return task;
	}

	@Nullable
	public TileTask pushTask(int tid) {
		if (tid < 0 || tid >= tasks.length) return null;
		TileTask task = this.factory.apply(tid);
		if (task == null) return null;
		task.tid = tid;
		task.rid = runtimeIdCounter++;
		addTask(task);
		task.onEnter();
		return task;
	}

	@Nullable
	public TileTask popTask(int tid) {
		if (tid < 0 || tid >= tasks.length) return null;
		if (tasks[tid] == null) return null;
		return removeTask(tasks[tid], true);
	}

	@Nullable
	public TileTask getTask(int tid) {
		if (tid < 0 || tid >= tasks.length) return null;
		return tasks[tid];
	}

	@Nullable
	public TileTask getTask(int tid, int rid) {
		if (tid < 0 || tid >= tasks.length) return null;
		TileTask node = tasks[tid];
		while (node != null) {
			if (node.rid == rid) return node;
			node = node.next;
		}
		return null;
	}

	public Iterator<TileTask> getTasks(int tid) {
		if (tid < 0 || tid >= tasks.length) return JavaHelper.emptyIterator();
		if (tasks[tid] == null) return JavaHelper.emptyIterator();

		return new Iterator<TileTask>() {

			int magic = changeMagic;
			TileTask node = tasks[tid];
			TileTask curr;

			@Override
			public boolean hasNext() {
				return node != null;
			}

			@Override
			public TileTask next() {
				if (magic != changeMagic) throw new ConcurrentModificationException();
				curr = node;
				node = node.next;
				return curr;
			}

			@Override
			public void remove() {
				if (magic != changeMagic) throw new ConcurrentModificationException();
				removeTask(curr, true);
				magic = changeMagic;
			}
		};
	}

	@Override
	public Iterator<TileTask> iterator() {
		if (tasks.length == 0) return JavaHelper.emptyIterator();
		return new Iterator<TileTask>() {

			int magic = changeMagic;
			int index = 0;
			TileTask node;
			TileTask curr;

			@Override
			public boolean hasNext() {
				if (magic != changeMagic) throw new ConcurrentModificationException();
				if (node != null) return true;
				while (index < tasks.length) {
					if ((node = tasks[index++]) != null) return true;
				}
				return false;
			}

			@Override
			public TileTask next() {
				if (magic != changeMagic) throw new ConcurrentModificationException();
				curr = node;
				node = node.next;
				return curr;
			}

			@Override
			public void remove() {
				if (magic != changeMagic) throw new ConcurrentModificationException();
				removeTask(curr, true);
				magic = changeMagic;
			}

		};
	}

	@Nullable
	public TileTask getTaskRT(int rid) {
		for (TileTask task : this) {
			if (task.rid == rid) return task;
		}
		return null;
	}

	@Nullable
	public TileTask removeTaskRT(int rid) {
		TileTask task = getTaskRT(rid);
		if (task != null) return removeTask(task, true);
		return null;
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		NBTTagList list = new NBTTagList();
		for (TileTask task : this) list.appendTag(task.serializeNBT());
		writer.write("tt_ls", list);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		this.tasks = new TileTask[this.tasks.length];
		this.runtimeIdCounter = 0;
		NBTTagList list = reader.listTag("tt_ls", NBTTag.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int tid = tag.getInteger("tid");
			TileTask task = this.factory.apply(tid);
			if (task == null) continue;
			task.deserializeNBT(tag);
			this.addTask(task);
			this.runtimeIdCounter = Math.max(task.rid + 1, this.runtimeIdCounter);
		}
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		writer.write("tt_s", (byte) tasks.length);
		NBTTagList list = new NBTTagList();
		for (TileTask task : this) {
			if (!task.needSyncToClient(0)) continue;
			list.appendTag(createTaskUpdateData(task));
		}
		writer.write("tt_ls", list);
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		this.tasks = new TileTask[reader.nint("tt_s")];
		NBTTagList list = reader.listTag("tt_ls", NBTTag.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) readTaskUpdateData(list.getCompoundTagAt(i), false);
	}

	protected NBTTagCompound createTaskUpdateData(TileTask task) {
		NBTSender sender = new NBTSender();
		task.writeUpdateData(sender);
		return sender.tag();
	}

	public TileTask readTaskUpdateData(NBTTagCompound tag, boolean enter) {
		NBTSender sender = new NBTSender(tag);
		int tid = tag.getInteger("tid");
		TileTask task = this.factory.apply(tid);
		if (task == null) return null;
		if (enter) task.onEnter();
		task.readUpdateData(sender);
		this.addTask(task);
		return null;
	}

	public void update() {
		Iterator<TileTask> iter = this.iterator();
		while (iter.hasNext()) {
			TileTask task = iter.next();
			task.onUpdate();
			if (task.isDead) iter.remove();
		}
	}

}
