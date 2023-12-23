package yuzunyannn.elementalsorcery.computer.soft;

import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;

public class AuthorityDisk extends AuthorityStorage implements IDisk {

	public AuthorityDisk(IComputer computer, IDisk storage, String[] paths, APP app) {
		super(computer, storage, paths, app);
	}

}
