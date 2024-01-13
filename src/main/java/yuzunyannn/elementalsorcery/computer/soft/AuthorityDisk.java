package yuzunyannn.elementalsorcery.computer.soft;

import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDisk;

public class AuthorityDisk extends AuthorityStorage implements IDisk {

	public AuthorityDisk(IComputer computer, IDisk storage, String[] paths) {
		super(computer, storage, paths);
	}

}
