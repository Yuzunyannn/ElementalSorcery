package yuzunyannn.elementalsorcery.elf.talk;

import net.minecraft.entity.player.EntityPlayer;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.item.ItemElfPurse;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class TalkActionCoin implements ITalkAction {

	protected boolean give;
	protected int count;

	public TalkActionCoin(boolean isGive, int count) {
		this.give = isGive;
		this.count = count;
	}

	public TalkActionCoin(JsonObject json) {

	}

	@Override
	public Object invoke(EntityPlayer player, EntityElfBase elf, TalkChapter chapter, TalkChapter.Iter iter,
			TalkScene originScene, int talkAt) {
		if (give) {
			ItemElfPurse.insert(player, count);
			return true;
		} else {
			int rest = ItemElfPurse.extract(player.inventory, count, true);
			if (rest > 0) return false;
			ItemElfPurse.extract(player.inventory, count, false);
			return true;
		}
	}

	@Override
	public boolean isPoint() {
		return true;
	}

}
