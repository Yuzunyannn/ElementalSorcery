package yuzunyannn.elementalsorcery.elf.talk;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import yuzunyannn.elementalsorcery.container.ContainerElf;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter.Iter;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;

public class TalkActionToGui implements ITalkAction {

	protected int toGUI;

	public TalkActionToGui(int gui) {
		this.toGUI = gui;
	}

	@Override
	public Object invoke(EntityPlayer player, EntityElfBase elf, TalkChapter chapter, Iter iter, TalkScene scene,
			int talkAt) {
		Container open = player.openContainer;
		if (open instanceof ContainerElf) {
			((ContainerElf) open).changeUI(toGUI);
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
