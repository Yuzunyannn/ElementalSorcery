package yuzunyannn.elementalsorcery.computer.soft;

import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IMemory;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.util.var.IVariableSet;

public class AuthorityMemory extends AuthorityStorage implements IMemory {

	public AuthorityMemory(IComputer computer, IMemory storage, IVariableSet variableSet, APP app) {
		super(computer, storage, variableSet, app);
	}

}
