package yuzunyannn.elementalsorcery.elf.research;

import java.util.Set;
import java.util.TreeSet;

public class Topics {

	public static final String STRUCT = "Struct";
	public static final String ENGINE = "Engine";
	public static final String NATURAL = "Natural";
	public static final String MANTRA = "Mantra";
	public static final String ENDER = "Ender";

	public static Set<String> getDefaultTopics() {
		Set<String> all = new TreeSet<String>();
		all.add("Mantra");
		all.add("Engine");
		all.add("Natural");
		all.add("Ender");
		all.add("Struct");
		return all;
	}

}
