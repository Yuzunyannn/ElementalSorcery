package yuzunyannn.elementalsorcery.elf.talk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/** 一个对话场景，可以描述一句话，或者几个选项 */
public abstract class TalkScene<T extends TalkScene> {

	protected String label = null;

	public T setLabel(String label) {
		this.label = label;
		return (T) this;
	}

	/** 是否是位点 */
	abstract public boolean isPoint();

	/** 获取当前的对话类型 */
	abstract public TalkType getType();

	/** 数量 */
	abstract public int size();

	public boolean isEmpty() {
		return this.size() <= 0;
	}

	/** 获取对话 */
	abstract public String getSayings(int index);

	// 高速获取缓存
	String[] allSaying;

	/** 获取全部对话 */
	public String[] getAllSaying() {
		if (allSaying != null) return allSaying;
		allSaying = new String[this.size()];
		for (int i = 0; i < this.size(); i++) {
			allSaying[i] = this.getSayings(i);
		}
		return allSaying;
	}

	/** 获取说话人 */
	@Nonnull
	abstract public Talker getTalker(int index);

	/**
	 * 获取当前对话的行为
	 * 
	 * @param index 如果是{@link TalkType#SAY}仅在getSayings中全部播放结束后传入0；如果是{@link TalkType#SELECT}传入选择的选项
	 */
	@Nullable
	abstract public ITalkAction getAction(int index);

	public NBTTagCompound serializeNBTToSend() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("tType", this.getType().ordinal());
		if (this.isPoint()) nbt.setBoolean("isPonit", true);

		NBTTagList list = new NBTTagList();
		for (int i = 0; i < this.size(); i++) {
			NBTTagCompound p = new NBTTagCompound();
			p.setString("str", this.getSayings(i));
			p.setByte("talker", (byte) this.getTalker(i).ordinal());
			list.appendTag(p);
		}
		nbt.setTag("list", list);

		return nbt;
	}

}
