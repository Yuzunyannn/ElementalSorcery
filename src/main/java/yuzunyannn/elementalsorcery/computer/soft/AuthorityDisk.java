package yuzunyannn.elementalsorcery.computer.soft;

import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.util.var.IVariableSet;

public class AuthorityDisk extends AuthorityStorage implements IDisk {

	public AuthorityDisk(IComputer computer, IDisk storage, IVariableSet variableSet, APP app) {
		super(computer, storage, variableSet, app);
	}

}
