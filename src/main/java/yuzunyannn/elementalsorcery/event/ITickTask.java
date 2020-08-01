package yuzunyannn.elementalsorcery.event;

public interface ITickTask {

	public static final int SUCCESS = 1;
	public static final int END = 0;

	int onTick();

	// 快捷操作
	public interface ITickTaskOnce extends ITickTask {
		@Override
		default int onTick() {
			onTickOnce();
			return ITickTask.END;
		}

		void onTickOnce();
	}
}
