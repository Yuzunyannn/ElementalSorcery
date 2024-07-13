package yuzunyannn.elementalsorcery.computer.soft.display;

import java.lang.ref.WeakReference;

import yuzunyannn.elementalsorcery.api.util.IAliveStatusable;

public class DTCReuntime extends DTCBase {

	public final static int ID = 2;

	protected WeakReference<IAliveStatusable> getter;

	public DTCReuntime() {
	}

	public DTCReuntime(IAliveStatusable able) {
		getter = new WeakReference<IAliveStatusable>(able);
	}

	@Override
	public int cid() {
		return DTCReuntime.ID;
	}

	@Override
	public IAliveStatusable getAbstractObject() {
		if (getter != null) return getter.get();
		return null;
	}

	@Override
	public boolean isAlive() {
		IAliveStatusable able = getAbstractObject();
		if (able == null) return false;
		return able.isAlive();
	}

}
