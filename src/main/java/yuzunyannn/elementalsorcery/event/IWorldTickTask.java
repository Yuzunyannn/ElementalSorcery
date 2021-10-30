package yuzunyannn.elementalsorcery.event;

import net.minecraft.world.World;

public interface IWorldTickTask {

	int onTick(World world);

	// 快捷操作
	public interface IWorldTickTaskOnce extends IWorldTickTask {
		@Override
		default int onTick(World world) {
			onTickOnce(world);
			return ITickTask.END;
		}

		void onTickOnce(World world);
	}
}
