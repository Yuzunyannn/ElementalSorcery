package yuzunyannn.elementalsorcery.elf;

import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** 精灵时间，以mc时间进行拆分 */
public class ElfTime {

	protected long time;

	public ElfTime(long time) {
		this.time = time + 1800;
	}

	public ElfTime(World world) {
		this(world.getWorldTime());
	}

	/** 获取秒，即为tick，20秒一分钟 */
	public int getSecond() {
		return (int) ((time / 20) % 20);
	}

	/** 获取分钟，真实时间的秒，十分钟为一小时，一天两小时 */
	public int getMinute() {
		return (int) ((time / 400) % 10);
	}

	/** 获取小时，一天两小时 */
	public int getHour() {
		return (int) ((time / 4000) % 2);
	}

	/** 获取日，十天为一个月 */
	public int getDay() {
		return (int) ((time / 8000) % 10);
	}

	/** 获取月，一年认为有四个月 */
	public int getMonth() {
		return (int) ((time / 80000) % 4);
	}

	/** 获取年 */
	public int getYear() {
		return (int) (time / 320000);
	}

	public long getTime() {
		return time - 1800;
	}

	@SideOnly(Side.CLIENT)
	public String getDate() {
		String year = I18n.format("elf.time.year", this.getYear() + 1);
		String month = I18n.format("elf.time.mon." + this.getMonth());
		String day = I18n.format("elf.time.day", this.getDay() + 1);
		String hour = I18n.format("elf.time.hour." + this.getHour());
		String min = I18n.format("elf.time.min", this.getMinute());
		String sec = I18n.format("elf.time.sec", this.getSecond());
		StringBuilder builder = new StringBuilder();
		builder.append(year).append(month).append(day).append(hour).append(min).append(sec);
		return builder.toString();
	}

}
