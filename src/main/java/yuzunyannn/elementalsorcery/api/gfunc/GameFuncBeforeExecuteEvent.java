package yuzunyannn.elementalsorcery.api.gfunc;

import yuzunyannn.elementalsorcery.api.event.ESEvent;

public class GameFuncBeforeExecuteEvent extends ESEvent {

	protected GameFunc func;
	protected GameFuncExecuteContext context;

	public GameFuncBeforeExecuteEvent(GameFunc func, GameFuncExecuteContext context) {
		this.func = func;
		this.context = context;
	}

	public GameFunc getFunc() {
		return func;
	}

	public GameFuncExecuteContext getContext() {
		return context;
	}

	public void setFunc(GameFunc func) {
		this.func = func == null ? GameFunc.NOTHING : func;
	}

}
