package yuzunyannn.elementalsorcery.elf.talk;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TalkActionJustPoint implements ITalkAction {

	@Override
	public Object invoke(TalkChapter chapter, TalkChapter.Iter iter, TalkScene scene, int talkAt) {
		return null;
	}

	@Override
	public boolean isPoint() {
		return true;
	}

}
