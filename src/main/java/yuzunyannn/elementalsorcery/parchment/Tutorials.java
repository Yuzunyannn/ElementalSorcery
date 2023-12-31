package yuzunyannn.elementalsorcery.parchment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.gson.JsonParseException;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.util.json.Json;
import yuzunyannn.elementalsorcery.util.json.Json.ParseExceptionCode;
import yuzunyannn.elementalsorcery.util.json.JsonObject;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;

public class Tutorials {

	static public final int HIDE_LEVEL = -1;

	static public class TutorialUnlockInfo {
		public final List<Tutorial> list = new ArrayList<>();
		protected int unlock = 0;

		public int getUnlock() {
			return unlock;
		}
	}

	static public class TutorialLevelInfo {
		public final List<TutorialUnlockInfo> list = new ArrayList<>();
		protected int totalUnlock = 0;
		protected int accTotalUnlock = 0;
		public final int level;

		public TutorialLevelInfo(int level) {
			this.level = level;
		}

		public void add(Tutorial tutorial) {
			this.totalUnlock = totalUnlock + tutorial.getUnlock();
			TutorialUnlockInfo info;
			int i = MathSupporter.binarySearch(list, (s) -> (double) (tutorial.getUnlock() - s.unlock));
			if (i < 0) {
				i = -i - 1;
				list.add(i, info = new TutorialUnlockInfo());
				info.unlock = tutorial.getUnlock();
			} else info = list.get(i);
			info.list.add(tutorial);
		}

		public int getAccTotalUnlock() {
			return accTotalUnlock;
		}

		protected void init() {
			for (int i = 0; i < level; i++) {
				TutorialLevelInfo info = getTutorialInfoByLevel(i);
				if (info == null) continue;
				this.accTotalUnlock = this.accTotalUnlock + info.totalUnlock;
			}
		}
	}

	static final Map<String, Tutorial> tutorials = new HashMap<>();

	static final Map<Integer, TutorialLevelInfo> levelMap = new HashMap<>();

	@Nullable
	public static Tutorial getTutorial(String id) {
		return tutorials.get(id);
	}

	@Nullable
	public static TutorialLevelInfo getTutorialInfoByLevel(int level) {
		return levelMap.get(level);
	}

	static public void reg(String id, Tutorial tutorial) {
		if (tutorials.containsKey(id)) return;
		tutorial.setId(id);
		tutorials.put(id, tutorial);
		TutorialLevelInfo info = levelMap.get(tutorial.getLevel());
		if (info == null) levelMap.put(tutorial.getLevel(), info = new TutorialLevelInfo(tutorial.getLevel()));
		info.add(tutorial);
	}

	static public void init(Side side) {
		tutorials.clear();
		levelMap.clear();

		for (ModContainer mod : Loader.instance().getActiveModList()) loadTutorials(mod);
		for (TutorialLevelInfo info : levelMap.values()) info.init();
	}

	static public void loadTutorials(ModContainer mod) {
		Json.ergodicAssets(mod, "/tutorials", (file, json) -> {
			ResourceLocation typePair = ElementMap.getType(json);
			if (typePair == null) return false;
			if (!ESAPI.MODID.equals(typePair.getNamespace())) return false;
			if (!ElementMap.checkModDemands(json)) return false;

			Tutorial tutorial = read(json);
			if (tutorial == null) return false;

			String id = Json.fileToId(file, null);
			reg(id, tutorial);
			return true;
		});
	}

	static public Tutorial read(JsonObject json) throws JsonParseException {
		if (!json.hasString("type")) throw Json.exception(ParseExceptionCode.NOT_HAVE, "type");
		Tutorial tutorial = new Tutorial();
		readCommont(json, tutorial);
		return tutorial;
	}

	static protected void readCommont(JsonObject json, Tutorial tutorial) {
		if (json.hasNumber("level")) tutorial.setLevel(json.getNumber("level").intValue());
		else tutorial.setLevel(HIDE_LEVEL);
		tutorial.setTitleKey(json.needString("title"));
		if (json.hasNumber("unlock")) tutorial.setUnlock(json.getNumber("unlock").intValue());
		else tutorial.setUnlock(0);
		if (json.hasString("hover")) tutorial.setHoverKey(json.getString("hover"));
		try {
			tutorial.setCoverItem(json.needItem("cover").getStack());
		} catch (JsonParseException e) {
			if (tutorial.getLevel() >= 0) throw e;
		}
		tutorial.setDescribeKey(json.needString("describe"));
	}
}
