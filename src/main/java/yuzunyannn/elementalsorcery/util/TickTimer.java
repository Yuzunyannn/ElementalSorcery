package yuzunyannn.elementalsorcery.util;

import java.util.LinkedList;

public class TickTimer {

	public TickTimer(int tick, int line) {
		lines = new LinkedList[line][tick];
		lineAt = new int[line];
	}

	protected final int[] lineAt;
	protected final LinkedList<TickTask>[][] lines;

	private class TickTask {
		public int tick;
		public Runnable call;

		public TickTask(int tick, Runnable task) {
			this.tick = tick;
			this.call = task;
		}
	}

	public void addTimer(int tick, Runnable task) {
		if (tick <= 0) return;
		addTimer(tick, task, 0);
	}

	private void addTimer(int tick, Runnable task, int add) {
		if (tick < 0) return;
		int n = 1;
		TickTask tt = new TickTask(tick, task);
		for (int i = 0; i < lines.length; i++) {
			int length = lines[i].length;
			tt.tick += n * ((lineAt[i] + add) % length);
			if (tt.tick / n + add <= length) {
				addTimer(i, (lineAt[i] + tick + add) % length, tt);
				return;
			}
			n = n * length;
		}
	}

	public void update() {
		int index = lineAt[0] + 1;
		int length = lines[0].length;
		if (index < length) {
			lineAt[0] = index;
			doTasks(index);
			return;
		}
		dealTasks(1, length);
		lineAt[0] = 0;
		doTasks(0);
	}

	private void addTimer(int i, int j, TickTask task) {
		LinkedList<TickTask> tasks = lines[i][j];
		if (tasks == null) tasks = lines[i][j] = new LinkedList<>();
		tasks.add(task);
	}

	private void doTasks(int index) {
		LinkedList<TickTask> tasks = lines[0][index];
		if (tasks == null) return;
		lines[0][index] = null;
		for (TickTask task : tasks) task.call.run();
	}

	private void downTasks(int i, int index, int n) {
		LinkedList<TickTask> tasks = lines[i][index];
		if (tasks == null) return;
		lines[i][index] = null;
		for (TickTask task : tasks) {
			task.tick -= index * n;
			this.addTimer(task.tick, task.call, 1);
		}
	}

	private void dealTasks(int i, int n) {
		if (i >= lineAt.length) return;
		int index = lineAt[i] + 1;
		int length = lines[i].length;
		if (index < length) {
			lineAt[i] = index;
			downTasks(i, index, n);
			return;
		}
		dealTasks(i + 1, n * length);
		lineAt[i] = 0;
		downTasks(i, 0, n);
	}

}
