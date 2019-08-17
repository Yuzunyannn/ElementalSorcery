package yuzunyannn.elementalsorcery.util.world;

import net.minecraft.world.World;

/** ES时间类 */
public class WorldTime {
	public int time;

	public WorldTime(World world) {
		this.time = (int) ((world.getWorldTime() + 1800) % 24000);
	}

	public boolean at(Period period) {
		if (period.start <= time && period.end > time)
			return true;
		return false;
	}

	public static enum Period {
		DAWN(0, 1800), MORNING(1800, 6900), AFTERNOON(6900, 12000), DUSK(12000, 13800), NIGHT(13800,
				24000), MIDNIGHT(17700, 20100), DAY(1800, 12000);
		public int start, end;

		Period(int start, int end) {
			this.start = start;
			this.end = end;
		}
	};
};