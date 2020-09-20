package yuzunyannn.elementalsorcery.elf.talk;

import java.util.ArrayList;

import com.google.gson.JsonObject;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;

/** 对话的章节，包含多个对话场景 */
public class TalkChapter {

	public static final String NOTHING_TO_SAY = "...";

	protected ArrayList<TalkScene> scenes = new ArrayList<>();

	public TalkChapter() {

	}

	public TalkChapter(JsonObject json) throws Exception {
		this.fromJson(json);
	}

	public ArrayList<TalkScene> getScenes() {
		return scenes;
	}

	public class Iter {
		protected int iter = 0;
		protected int sceneIter = 0;

		public void reset() {
			sceneIter = iter = 0;
		}

		/** 特殊函数处理 */
		@SideOnly(Side.CLIENT)
		private String specialString(String str, EntityPlayer player, EntityElfBase elf) {
			if (str.charAt(0) != '?') return str;
			String id = str.substring(1);
			ITalkSpecial ts = ITalkSpecial.REGISTRY.get(id);
			if (ts == null) return str;
			return ts.deal(player, elf, TalkChapter.this, this, str);
		}

		public Object getSaying(EntityPlayer player, EntityElfBase elf) {
			if (scenes.isEmpty()) return NOTHING_TO_SAY;
			TalkScene scene = scenes.get(iter);
			if (scene.isEmpty()) return NOTHING_TO_SAY;
			if (scene.getType() == TalkType.SAY) {
				if (player.world.isRemote) return specialString(scene.getSayings(sceneIter), player, elf);
				return scene.getSayings(sceneIter);
			}
			return scene.getAllSaying();
		}

		public Talker getTalker() {
			if (scenes.isEmpty()) return Talker.OPPOSING;
			TalkScene scene = scenes.get(iter);
			if (scene.isEmpty()) return Talker.OPPOSING;
			return scene.getTalker(sceneIter);
		}

		public TalkType getType() {
			if (scenes.isEmpty()) return TalkType.SAY;
			TalkScene scene = scenes.get(iter);
			return scene.getType();
		}

		public boolean hasNext() {
			if (scenes.isEmpty()) return false;
			TalkScene scene = scenes.get(iter);
			if (scene.getType() == TalkType.SAY) {
				if (sceneIter + 1 < scene.size()) return true;
				if (iter + 1 < scenes.size()) return true;
				return false;
			}
			return iter + 1 < scenes.size();
		}

		public void next() {
			if (this.hasNext()) {
				TalkScene scene = scenes.get(iter);
				if (scene.getType() == TalkType.SAY && sceneIter + 1 < scene.size()) sceneIter++;
				else {
					iter++;
					sceneIter = 0;
				}
			} else this.reset();
		}

		public boolean isPoint() {
			if (scenes.isEmpty()) return false;
			TalkScene scene = scenes.get(iter);
			if (scene.isEmpty()) return false;
			if (scene.getType() == TalkType.SAY) return sceneIter + 1 == scene.size() && scene.isPoint();
			return scene.isPoint();
		}

		public boolean hasNextScene() {
			if (scenes.isEmpty()) return false;
			return iter + 1 < scenes.size();
		}

		public void nextScene() {
			this.sceneIter = 0;
			iter++;
			if (iter >= scenes.size()) this.reset();
		}

		public boolean isPointScene() {
			if (scenes.isEmpty()) return false;
			TalkScene scene = scenes.get(iter);
			return scene.isPoint();
		}

		public boolean isEnd() {
			return iter >= scenes.size();
		}

		public void setEnd() {
			iter = scenes.size();
		}

		/**
		 * @return 返回一个新的，要切换的章节
		 */
		public TalkChapter dealAction(int talkAt, EntityPlayer player, EntityElfBase elf) {
			if (scenes.isEmpty()) return null;
			TalkScene scene = scenes.get(iter);
			ITalkAction action = scene.getAction(scene.getType() == TalkType.SAY ? 0 : talkAt);
			if (action == null) return null;
			Object ret = action.invoke(player, elf, TalkChapter.this, this, scene, talkAt);
			if (ret instanceof TalkChapter) return (TalkChapter) ret;
			return null;
		}

		public int getIndex() {
			return this.iter;
		}

		public void setIndex(int iter) {
			this.iter = Math.max(0, Math.min(scenes.size(), iter));
			this.sceneIter = 0;
		}
	}

	public Iter createIter() {
		return new Iter();
	}

	public int size() {
		return scenes.size();
	}

	public void addScene(TalkScene scene) {
		scenes.add(scene);
	}

	public NBTTagCompound serializeNBTToSend() {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		nbt.setTag("scenes", list);
		for (TalkScene scene : scenes) list.appendTag(scene.serializeNBTToSend());
		return nbt;
	}

	@SideOnly(Side.CLIENT)
	public TalkChapter deserializeNBTFromSend(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("scenes", 10);
		scenes.clear();
		for (NBTBase base : list) scenes.add(new TalkSceneClient((NBTTagCompound) base));
		return this;
	}

	void fromJson(JsonObject json) throws Exception {

	}

}
