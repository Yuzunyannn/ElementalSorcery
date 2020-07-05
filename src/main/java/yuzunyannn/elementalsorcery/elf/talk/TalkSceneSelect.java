package yuzunyannn.elementalsorcery.elf.talk;

import java.util.ArrayList;

public class TalkSceneSelect extends TalkScene<TalkSceneSelect> {

	protected ArrayList<String> list = new ArrayList<>();
	protected ArrayList<ITalkAction> actions = new ArrayList<>();

	@Override
	public TalkType getType() {
		return TalkType.SELECT;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean isPoint() {
		for (ITalkAction ac : actions) if (ac != null) return true;
		return false;
	}

	@Override
	public String getSayings(int talkAt) {
		return list.get(talkAt);
	}

	@Override
	public Talker getTalker(int talkAt) {
		return Talker.PLAYER;
	}

	@Override
	public ITalkAction getAction(int index) {
		return actions == null ? null : actions.get(index);
	}

	public void addString(String str, ITalkAction action) {
		this.list.add(str);
		this.actions.add(action);
	}

}
