package yuzunyannn.elementalsorcery.elf.talk;

import java.util.ArrayList;

public class TalkSceneSay extends TalkScene<TalkSceneSay> {

	protected static class Packet {
		String unlocalizedOrlocalizedString;
		Talker talker = Talker.OPPOSING;

		String getSaying() {
			return unlocalizedOrlocalizedString;
		}
	}

	protected ArrayList<Packet> list = new ArrayList<Packet>();
	protected ITalkAction action = null;

	public TalkSceneSay() {

	}

	public TalkSceneSay(String something) {
		addString(something, Talker.OPPOSING);
	}

	public TalkSceneSay(String something, Talker talker) {
		addString(something, talker);
	}

	@Override
	public TalkType getType() {
		return TalkType.SAY;
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
		return action == null ? false : action.isPoint();
	}

	@Override
	public String getSayings(int talkAt) {
		return list.get(talkAt).getSaying();
	}

	@Override
	public Talker getTalker(int talkAt) {
		return list.get(talkAt).talker;
	}

	@Override
	public ITalkAction getAction(int talkAt) {
		return action;
	}

	public TalkSceneSay addString(String str, Talker talker) {
		Packet p = new Packet();
		p.unlocalizedOrlocalizedString = str;
		p.talker = talker;
		this.list.add(p);
		return this;
	}

	public TalkSceneSay addAction(ITalkAction action) {
		this.action = action;
		return this;
	}

	public TalkSceneSay setEnd() {
		this.addAction(new TalkActionEnd());
		return this;
	}

}
