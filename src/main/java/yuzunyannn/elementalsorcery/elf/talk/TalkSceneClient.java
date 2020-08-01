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
			this.addString(I18n.format(p.getString("str")), Talker.values()[p.getByte("talker")]);
		}
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
