package yuzunyannn.elementalsorcery.computer.soft;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.IMemory;
import yuzunyannn.elementalsorcery.api.computer.IStorageMonitor;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.IAPPGui;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.var.Variable;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;

public class AppTutorial extends APP {

//	public static final Variable<Byte> INDEX = new Variable<>("si", VariableSet.BYTE);
	public static final Variable<Float> POGRESS = new Variable<>("pg", VariableSet.FLOAT);

	public static float lastProgress;
	public static int selectIndex = 0;
	public static String showTutorialId = null;

	@SideOnly(Side.CLIENT)
	public int getSelectPartIndex() {
		checkCache();
		return selectIndex;
	}

	@SideOnly(Side.CLIENT)
	public void changeSelectIndex(int index) {
		if (isPartLocked(index)) return;
		if (selectIndex == index) return;
		selectIndex = index;
	}

	@SideOnly(Side.CLIENT)
	public String getTutorialId() {
		checkCache();
		return showTutorialId;
	}

	@SideOnly(Side.CLIENT)
	public void changeShowTutorialId(String id) {
		showTutorialId = id;
	}

	@SideOnly(Side.CLIENT)
	protected void checkCache() {
		if (lastProgress > this.progress) {
			lastProgress = this.progress;
			selectIndex = 0;
			showTutorialId = null;
		}
	}

	protected float progress = 0;

	public AppTutorial(IOS os, int pid) {
		super(os, pid);
		sync();
	}

	protected void sync() {
		IMemory memory = this.getOS().getMemory(this);
		progress = Math.max(memory.get(POGRESS), 1);
	}

	public float getProgress() {
		return progress;
	}

	public boolean isPartLocked(int index) {
		if (index == 0) return false;
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMemoryChange() {
		super.onMemoryChange();
		sync();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IAPPGui createGUIRender() {
		return new AppTutorialGui(this);
	}

	@Override
	public void initMemorySync(IStorageMonitor monitor) {
		monitor.add(POGRESS);
	}

}
