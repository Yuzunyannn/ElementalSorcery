package yuzunyannn.elementalsorcery.nodegui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GAction {

	protected boolean isStart = false;

	public boolean isStart() {
		return isStart;
	}

	public void onStart(GNode node) {
		isStart = true;
	}

	public void reset(GNode node) {
		isStart = false;
	}

	public boolean isOver() {
		return true;
	}

	public void update(GNode node) {

	}

}
