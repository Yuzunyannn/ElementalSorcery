package yuzunyannn.elementalsorcery.computer.soft;

import java.util.List;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.AppDiskType;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGui;
import yuzunyannn.elementalsorcery.api.util.MatchHelper;
import yuzunyannn.elementalsorcery.api.util.detecter.DDFloat;
import yuzunyannn.elementalsorcery.api.util.var.Variable;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.building.ArcInfo;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingInherent;
import yuzunyannn.elementalsorcery.parchment.Tutorial;
import yuzunyannn.elementalsorcery.parchment.TutorialBuilding;
import yuzunyannn.elementalsorcery.parchment.Tutorials;
import yuzunyannn.elementalsorcery.parchment.Tutorials.TutorialLevelInfo;

public class AppTutorial extends APP {

//	public static final Variable<Byte> INDEX = new Variable<>("si", VariableSet.BYTE);
	public static final Variable<Float> POGRESS = new Variable<>("pg", VariableSet.FLOAT);

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
		if (isPartLocked(selectIndex)) {
			selectIndex = 0;
			showTutorialId = null;
		}
	}

	protected float progress = 0;

	public AppTutorial(IOS os, int pid) {
		super(os, pid);
		detecter.add("1", new DDFloat(i -> progress = i, () -> progress));
	}

	@Override
	public void onStartup() {
		super.onStartup();
		onDiskChange();
	}

	@Override
	public void onDiskChange() {
		super.onDiskChange();
		IOS os = getOS();
		IDeviceStorage disk = os.getDisk(this, AppDiskType.USER_DATA);
		progress = disk.get(POGRESS);
		detecter.markDirty("1");
	}

	public float getProgress() {
		return progress;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setFloat("pg", progress);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		progress = nbt.getFloat("pg");
	}

	public boolean isPartLocked(int index) {
		if (index == 0) return false;
		TutorialLevelInfo info = Tutorials.getTutorialInfoByLevel(index);
		if (info == null) return true;
		return progress <= info.getAccTotalUnlock();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISoftGui createGUIRender() {
		return new AppTutorialGui(this);
	}

	@Override
	public void handleOperation(NBTTagCompound nbt) {
		super.handleOperation(nbt);
		if (nbt.hasKey("ptbd")) {
			String id = nbt.getString("ptbd");
			printBuilding(id);
		}
	}

	protected void printBuilding(String id) {
		Tutorial tutorial = Tutorials.getTutorial(id);
		TutorialBuilding building = tutorial == null ? null : tutorial.getBuilding();
		if (building == null) return;
		IOS os = getOS();
		List<UUID> devices = os.filterLinkedDevice("item-writer");
		if (devices.isEmpty()) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setByte("code", (byte) 1);
			os.message(this, nbt);
			return;
		}
		int pid = os.exec(this, TaskInventoryItemSelect.ID);
		TaskInventoryItemSelect app = (TaskInventoryItemSelect) os.getAppInst(pid);
		app.bindDevice(devices.get(0));

		Building bd = building.getBuilding();
		ItemStack stack = new ItemStack(ESObjects.ITEMS.ARCHITECTURE_CRYSTAL);
		ArcInfo.initArcInfoToItem(stack, bd.getKeyName());
		app.setWriteData(stack.getTagCompound());
		stack.setTagCompound(null);
		MatchHelper.setSampleNoTagCheck(stack);
		app.setEnabledStack(stack);
		if (bd instanceof BuildingInherent) app.setTagTanslateKey(((BuildingInherent) bd).getTanslateName());
		else app.setTagTanslateKey(bd.getName());
	}

}
