package yuzunyannn.elementalsorcery.elf.talk;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TalkSceneClient extends TalkSceneSay {

	final TalkType type;
	final boolean isPoint;

	public TalkSceneClient(NBTTagCompound nbt) {
		type = TalkType.values()[nbt.getInteger("tType")];
		isPoint = nbt.getBoolean("isPonit");

		NBTTagList list = nbt.getTagList("list", 10);
		for (NBTBase base : list) {
			NBTTagCompound p = (NBTTagCompound) base;
			String str = dealSpecialString(p.getString("str"));
			this.addString(str, Talker.values()[p.getByte("talker")]);
		}
	}

	public static String dealSpecialString(String str) {
		if (str.charAt(0) == '#') {
			int n = str.indexOf('?');
			if (n == -1) return str.substring(1);
			String first = str.substring(1, n);
			String pair = str.substring(n + 1);
			String[] pairs = pair.split("&");
			for (int i = 0; i < pairs.length; i++) pairs[i] = I18n.format(pairs[i]);
			return I18n.format(first, (Object[]) pairs);
		}
		return I18n.format(str);
	}

	@Override
	public TalkType getType() {
		return type;
	}

	@Override
	public boolean isPoint() {
		return isPoint;
	}

}
