package yuzunyannn.elementalsorcery.elf.research;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** 客户端的绘图类 */
@SideOnly(Side.CLIENT)
public class Topic {

	protected final static Map<String, Class<? extends Topic>> TOPICS = new HashMap<>();

	public static void register(String type, Class<? extends Topic> handle) {
		TOPICS.put(type, handle);
	}

	public static Topic create(String type) {
		// 构建对象，空指针或者无法实例化，会自动创建默认的
		try {
			Class<? extends Topic> cls = TOPICS.get(type);
			Constructor<Topic> c = (Constructor<Topic>) cls.getConstructor(String.class);
			return c.newInstance(type);
		} catch (Exception e) {
			return new TopicDefault(type);
		}
	}

	public final String type;

	/** 字符串构造函数，必须 */
	public Topic(String type) {
		this.type = type == null ? "" : type;
	}

	public String getTypeName() {
		return type;
	}

	public int getColor() {
		return 0;
	}

	public String getUnlocalizedName() {
		return "topic." + this.type;
	}

	public void update(boolean isSelected) {

	}

	public void render(Minecraft mc, float size, float alpha, float partialTicks) {

	}

	public static void registerAll() {
		register(Topics.ENGINE, TopicEngine.class);
		register(Topics.STRUCT, TopicStruct.class);
		register(Topics.ENDER, TopicEnder.class);
		register(Topics.NATURAL, TopicNatural.class);
		register(Topics.MANTRA, TopicMantra.class);
		register(Topics.BIOLOGY, TopicBiology.class);
	}

}
