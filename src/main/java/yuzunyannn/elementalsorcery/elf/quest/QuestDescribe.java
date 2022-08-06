package yuzunyannn.elementalsorcery.elf.quest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestCondition;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class QuestDescribe implements INBTSerializable<NBTTagCompound> {

	public static class Getter {
		protected String str;
		protected Object[] objs;

		public Getter() {
			str = "";
		}

		public Getter(String str) {
			this.str = str;
		}

		public Getter(String str, String... objs) {
			this.str = str;
			this.objs = objs;
		}

		@SideOnly(Side.CLIENT)
		public String get() {
			if (objs == null) return I18n.format(this.str);
			Object[] strs = new Object[objs.length];
			for (int i = 0; i < objs.length; i++) strs[i] = I18n.format(objs[i].toString());
			return I18n.format(this.str, strs);
		}

		public void writeToNBT(NBTTagCompound nbt) {
			nbt.setString("s", str);
			if (objs != null && objs.length > 0) {
				NBTTagList list = new NBTTagList();
				for (Object obj : objs) list.appendTag(new NBTTagString(obj.toString()));
				nbt.setTag("objs", list);
			}
		}

		public void readFromNBT(NBTTagCompound nbt) {
			str = nbt.getString("s");
			if (nbt.hasKey("objs", NBTTag.TAG_LIST)) {
				NBTTagList list = nbt.getTagList("objs", NBTTag.TAG_STRING);
				objs = new String[list.tagCount()];
				for (int i = 0; i < list.tagCount(); i++) objs[i] = list.getStringTagAt(i);
			}
		}

	}

//	protected String type = null;
	protected String title = "";
	protected List<Getter> strings = new ArrayList<>();

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void addDescribe(String value, String... param) {
		strings.add(new Getter(value, param));
	}

	public boolean isEmpty() {
		return strings.isEmpty();
	}

//	public String getType() {
//		return type == null ? "none" : type;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}

	@SideOnly(Side.CLIENT)
	public String getMainDescribe(Quest quest, @Nullable EntityLivingBase player, boolean dynamic) {
		StringBuilder builder = new StringBuilder();
		for (Getter getter : strings) builder.append(getter.get());
		String describe = builder.toString();
		List<QuestCondition> cons = quest.getType().getConditions();
		return TextHelper.replaceStringWith$(describe, (index) -> {
			if (index < 0 || index >= cons.size()) return "<error>";
			return cons.get(index).getDescribe(quest, player, dynamic);
		});
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (Getter gd : strings) {
			NBTTagCompound nbt = new NBTTagCompound();
			gd.writeToNBT(nbt);
			list.appendTag(nbt);
		}
		tag.setTag("strs", list);
		tag.setString("title", title);
		// if (type != null) tag.setString("type", type);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		NBTTagList list = tag.getTagList("strs", NBTTag.TAG_COMPOUND);
		strings.clear();
		for (NBTBase base : list) {
			NBTTagCompound nbt = (NBTTagCompound) base;
			Getter get = new Getter();
			get.readFromNBT(nbt);
			strings.add(get);
		}
		title = tag.getString("title");
		// if (tag.hasKey("type", NBTTag.TAG_STRING)) type = tag.getString("type");
	}

}
