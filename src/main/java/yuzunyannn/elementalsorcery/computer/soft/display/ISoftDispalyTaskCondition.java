package yuzunyannn.elementalsorcery.computer.soft.display;

import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.IAliveStatusable;
import yuzunyannn.elementalsorcery.api.util.ICastable;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

interface ISoftDispalyTaskCondition extends IAliveStatusable, ICastable {

	public static ISoftDispalyTaskCondition createCondition(int id) {
		switch (id) {
		case DTCDevice.ID:
			return new DTCDevice();
		case DTCReuntime.ID:
			return new DTCReuntime();
		}
		return null;
	}

	int cid();

	void update(IOS os);

	int extraInt();

	void writeSaveData(INBTWriter writer);

	void readSaveData(INBTReader reader);

}
