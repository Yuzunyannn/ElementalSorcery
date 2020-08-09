package yuzunyannn.elementalsorcery.elf.talk;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import yuzunyannn.elementalsorcery.container.ContainerElf;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter.Iter;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;

public class TalkActionToTrade implements ITalkAction {

	@Override
	public Object invoke(EntityPlayer player, EntityElfBase elf, TalkChapter chapter, Iter iter, TalkScene scene,
			int talkAt) {
		Container open = player.openContainer;
		if (open instanceof ContainerElf) {
			((ContainerElf) open).changeUI(ESGuiHandler.GUI_ELF_TRADE);
			iter.setEnd();
			return true;
		}
		return false;
	}

	@Override
	public boolean isPoint() {
		return true;
	}

}
