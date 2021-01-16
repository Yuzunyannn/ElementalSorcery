package yuzunyannn.elementalsorcery.elf.research;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.text.TextHelper;

public class KnowledgeType {

	static final public Map<String, KnowledgeType> REGISTRY = new HashMap<>();

	public static KnowledgeType get(String type) {
		return REGISTRY.get(type);
	}

	public static KnowledgeType newKnowledgeType(String id, Object... topics) throws RuntimeException {
		KnowledgeType type = new KnowledgeType().setNameId(id).setUnlocalizedName(TextHelper.castToCamel(id));
		for (int i = 0; i < topics.length; i++) {
			Object obj = topics[i];
			if (obj instanceof Entry) type.addTopic((Entry<String, Integer>) obj);
			else if (obj instanceof String) {
				int count = ((Number) topics[++i]).intValue();
				type.addTopic((String) obj, count);
			} else if (obj instanceof ElementStack) {
				type.setKnowledge((ElementStack) obj);
			}
		}
		return type;
	}

	public static void register(KnowledgeType type) {
		REGISTRY.put(type.getNameId(), type);
	}

	public static void register(String id, Object... topics) throws RuntimeException {
		register(newKnowledgeType(id, topics));
	}

	private static ElementStack knowledge(int count, int power) {
		return new ElementStack(ESInit.ELEMENTS.KNOWLEDGE, count, power);
	}

	public static void registerAll() {
		register("magic_device", knowledge(10, 70), "Engine", 10, "Natural", 2, "Struct", 8);
		register("architecture", knowledge(40, 10), "Engine", 10, "Natural", 10, "Struct", 2, "Ender", 3);
		register("ender_boat", knowledge(5, 100), "Natural", 5, "Ender", 12);
		// 第一个为Mantra可以用作咒文
		register("library", knowledge(20, 50), "Mantra", 5, "Struct", 5, "Engine", 5);
		register("mantra", knowledge(20, 50), "Mantra", 10, "Struct", 5);
		register("element", knowledge(30, 10), "Mantra", 2, "Struct", 12, "Natural", 2);
		register("altar", knowledge(40, 40), "Mantra", 6, "Struct", 6, "Natural", 4, "Engine", 4);
	}

	private String nameId;
	private String unlocalizedName;
	private List<Entry<String, Integer>> topics = new ArrayList<>();
	private ElementStack knowledge = ElementStack.EMPTY;

	public String getNameId() {
		return nameId;
	}

	public KnowledgeType setNameId(String nameId) {
		this.nameId = nameId;
		return this;
	}

	public KnowledgeType setUnlocalizedName(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
		return this;
	}

	@SideOnly(Side.CLIENT)
	public String getUnlocalizedName() {
		return "kType." + unlocalizedName;
	}

	public ElementStack getKnowledge() {
		return knowledge;
	}

	public void setKnowledge(ElementStack knowledge) {
		this.knowledge = knowledge;
	}

	public void setTopics(List<Entry<String, Integer>> topics) {
		this.topics = topics;
	}

	public void addTopic(String type, int count) {
		addTopic(new AbstractMap.SimpleEntry(type, count));
	}

	public void addTopic(Entry<String, Integer> entry) {
		this.topics.add(entry);
	}

	public List<Entry<String, Integer>> getTopics() {
		return topics;
	}

}
