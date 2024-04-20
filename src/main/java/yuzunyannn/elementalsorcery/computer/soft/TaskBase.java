package yuzunyannn.elementalsorcery.computer.soft;

import yuzunyannn.elementalsorcery.api.computer.soft.IOS;

public class TaskBase extends AppBase {

	public TaskBase(IOS os, int pid) {
		super(os, pid);
		setTask(true);
	}

}
