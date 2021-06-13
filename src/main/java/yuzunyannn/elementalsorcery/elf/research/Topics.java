package yuzunyannn.elementalsorcery.elf.research;

import java.util.Set;
import java.util.TreeSet;

import yuzunyannn.elementalsorcery.ElementalSorcery;

public class Topics {

	public static final String STRUCT = "Struct";
	public static final String ENGINE = "Engine";
	public static final String NATURAL = "Natural";
	public static final String MANTRA = "Mantra";
	public static final String ENDER = "Ender";
	public static final String BIOLOGY = "Biology";

	public static Set<String> getDefaultTopics() {
		Set<String> all = new TreeSet<String>();
		all.add("Mantra");
		all.add("Engine");
		all.add("Natural");
		all.add("Ender");
		all.add("Struct");
		if (ElementalSorcery.isDevelop) all.add("Biology");
		return all;
	}

}
