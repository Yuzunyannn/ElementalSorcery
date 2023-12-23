package yuzunyannn.elementalsorcery.computer.soft;

import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IMemory;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;

public class AuthorityMemory extends AuthorityStorage implements IMemory {

	public AuthorityMemory(IComputer computer, IMemory storage, String[] paths, APP app) {
		super(computer, storage, paths, app);
	}

}
