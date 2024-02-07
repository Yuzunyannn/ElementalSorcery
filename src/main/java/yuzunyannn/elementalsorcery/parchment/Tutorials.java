package yuzunyannn.elementalsorcery.parchment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.gson.JsonParseException;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.Json;
import yuzunyannn.elementalsorcery.util.json.Json.ParseExceptionCode;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
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
			this.totalUnlock = Math.max(this.totalUnlock, tutorial.getUnlock());
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

	@Nullable
	public static TutorialLevelInfo tryGetTutorialInfoBiggerOrEqualLevel(int level) {
		for (int i = level; i < level + 5; i++) {
			TutorialLevelInfo info = levelMap.get(level);
			if (info != null) return info;
		}
		return null;
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
		if (json.has("crafts")) {
			List<ItemRecord> records = json.needItems("crafts");
			tutorial.setCrafts(ItemRecord.asItemStackList(records));
		}
		if (json.hasObject("building")) {
			JsonObject buildingJson = json.getObject("building");
			String id = buildingJson.needString("structure", "building");
			Building building = BuildingLib.instance.getBuilding(id);
			if (building == null) throw Json.exception(ParseExceptionCode.NOT_HAVE, "building");
			TutorialBuilding inst = new TutorialBuilding(building);
			tutorial.setBuilding(inst);
			try {
				JsonArray attachs = buildingJson.needArray("custom", "add", "attach");
				loadBuilding(attachs, inst);
			} catch (JsonParseException e) {}
		}
	}

	static protected void loadBuilding(JsonArray attachs, TutorialBuilding inst) {
		for (int i = 0; i < attachs.size(); i++) {
			if (!attachs.hasObject(i)) continue;
			JsonObject json = attachs.getObject(i);

			String type = "";
			if (json.hasString("type")) type = json.getString("type");

			List<ItemRecord> irList = json.needItems("item");
			if (irList.isEmpty()) continue;
			ItemStack stack = irList.get(0).getStack();
			Block block = Block.getBlockFromItem(stack.getItem());
			if (block == null || block == Blocks.AIR) continue;
			IBlockState state = block.getStateFromMeta(stack.getItemDamage());

			switch (type) {
			case "full": {
				BlockPos from = new BlockPos(json.needPos("from").get(0));
				BlockPos to = new BlockPos(json.needPos("to").get(0));
				for (int x = from.getX(); x <= to.getX(); x++) {
					for (int y = from.getY(); y <= to.getY(); y++) {
						for (int z = from.getZ(); z <= to.getZ(); z++) {
							BlockPos pos = new BlockPos(x, y, z);
							inst.addExtraBlockNotOverlap(pos, state);
						}
					}
				}
				break;
			}
			default: {
				List<Vec3d> v3fs = json.needPos("pos");
				if (v3fs.isEmpty()) continue;
				for (Vec3d v3f : v3fs) inst.addExtraBlock(new BlockPos(v3f.x, v3f.y, v3f.z), state);
				break;
			}
			}

		}
	}
}
