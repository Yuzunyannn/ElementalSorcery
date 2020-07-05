package yuzunyannn.elementalsorcery.elf.talk;

import java.util.ArrayList;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;

/** 一个对话场景，可以描述一句话，或者几个选项 */
public class TalkScene {

	protected static class Packet {
		String unlocalizedOrlocalizedString;
		Talker talker = Talker.OPPOSING;

		String getSaying() {
			return I18n.format(unlocalizedOrlocalizedString);
		}
	}

	protected ArrayList<Packet> list = new ArrayList<TalkScene.Packet>();
	protected ArrayList<ITalkAction> actions = null;
	protected TalkType type = TalkType.SAY;
	protected String label = null;

	public TalkScene() {
	}

	public TalkScene(TalkType type) {
		this.type = type;
	}

	public TalkScene setLabel(String label) {
		this.label = label;
		return this;
	}

	public void addString(String str, Talker talker) {
		if (this.getType() == TalkType.SELECT && this.actions != null) {
			ElementalSorcery.logger.warn("【文字-行为】添加一旦使用之后，不可再使用别的添加");
			return;
		}
		Packet p = new Packet();
		p.unlocalizedOrlocalizedString = str;
		p.talker = talker;
		this.list.add(p);
	}

	public void addAction(ITalkAction action) {
		if (this.getType() != TalkType.SAY) {
			ElementalSorcery.logger.warn("单独添加行为，只能用于普通对话");
			return;
		}
		if (actions == null) actions = new ArrayList<ITalkAction>();
		actions.add(action);
	}

	public void addString(String str, ITalkAction action) {
		if (this.getType() != TalkType.SELECT) {
			ElementalSorcery.logger.warn("【文字-行为】添加模式只限于选择项目");
			return;
		}
		if (this.actions == null) this.actions = new ArrayList<ITalkAction>();
		if (this.actions.size() != this.list.size()) {
			ElementalSorcery.logger.warn("【文字-行为】一旦添加之后，不可再使用别的添加");
			return;
		}
		Packet p = new Packet();
		p.unlocalizedOrlocalizedString = str;
		p.talker = Talker.PLAYER;
		this.list.add(p);
		this.actions.add(action);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	/** 是否是位点 */
	public boolean isPoint() {
		if (actions == null) return false;
		for (ITalkAction action : actions) if (action.isPoint()) return true;
		return false;
	}

	/** 获取当前的对话类型 */
	public TalkType getType() {
		return type;
	}

	public int size() {
		return list.size();
	}

	/** 获取对话 */
	public String getSayings(int talkAt) {
		return list.get(talkAt).getSaying();
	}

	// 高速获取缓存
	String[] allSaying;

	/** 获取全部对话 */
	public String[] getAllSaying() {
		if (allSaying != null) return allSaying;
		allSaying = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			allSaying[i] = list.get(i).getSaying();
		}
		return allSaying;
	}

	/** 获取说话人 */
	public Talker getTalker(int talkAt) {
		Packet p = list.get(talkAt);
		return p.talker == null ? Talker.OPPOSING : p.talker;
	}

	/**
	 * 获取当前对话的行为
	 * 
	 * @param talkAt 如果是{@link TalkType#SAY}仅在getSayings中全部播放结束后传入0；如果是{@link TalkType#SELECT}传入选择的选项
	 */
	ITalkAction getAction(int talkAt) {
		return actions == null ? null : actions.get(talkAt);
	}

	public NBTTagCompound serializeNBTToSend() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("tType", this.type.ordinal());
		if (this.isPoint()) nbt.setBoolean("isPonit", true);
		NBTTagList list = new NBTTagList();
		for (Packet packet : this.list) {
			NBTTagCompound p = new NBTTagCompound();
			p.setString("str", packet.unlocalizedOrlocalizedString);
			p.setByte("talker", (byte) packet.talker.ordinal());
			list.appendTag(p);
		}
		nbt.setTag("list", list);
		return nbt;
	}

	@SideOnly(Side.CLIENT)
	public TalkScene deserializeNBTFromSend(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("list", 10);
		this.list.clear();
		for (NBTBase base : list) {
			NBTTagCompound p = (NBTTagCompound) base;
			this.addString(p.getString("str"), Talker.values()[p.getByte("talker")]);
		}
		this.type = TalkType.values()[nbt.getInteger("tType")];
		if (nbt.getBoolean("isPonit")) {
			this.actions = new ArrayList<ITalkAction>();
			this.actions.add(new TalkActionJustPoint());
		}
		return this;
	}

}
