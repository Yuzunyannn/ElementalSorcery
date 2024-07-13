package yuzunyannn.elementalsorcery.computer.soft.display;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.ICastable;
import yuzunyannn.elementalsorcery.api.util.render.GameDisplayCast;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.IDisplayNode;
import yuzunyannn.elementalsorcery.nodegui.IDisplaySustainable;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;

public abstract class SoftDisplayTask<T, U extends NBTBase> implements IDisplayNode, IDisplaySustainable<T, U> {

	private final String id;
	private ISoftDispalyTaskCondition condition;
	protected boolean isDead = false;
	protected String digest;

	public SoftDisplayTask(String id) {
		this.id = id;
	}

	@Override
	public String digest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public void setCondition(ISoftDispalyTaskCondition condition) {
		this.condition = condition;
	}

	public ISoftDispalyTaskCondition getCondition() {
		return condition;
	}

	public <T> T conditionCast(Class<T> clazz) {
		return condition == null ? null : condition.cast(clazz);
	}

	@Override
	public String getId() {
		return this.id;
	}

	@SideOnly(Side.CLIENT)
	protected GNode currNode;

	@Override
	@SideOnly(Side.CLIENT)
	public GNode getGNode() {
		if (currNode == null) {
			initUI();
			doUpdateNodeDead();
		}
		return currNode;
	}

	@SideOnly(Side.CLIENT)
	private void doUpdateNodeDead() {
		if (isDead && currNode != null) updateWhenDead();
	}

	@SideOnly(Side.CLIENT)
	abstract protected void initUI();

	@SideOnly(Side.CLIENT)
	protected void updateWhenDead() {

	}

	@Override
	public boolean isAlive() {
		if (isDead) return false;
		if (this.condition == null) {
			isDead = true;
			return false;
		}
		if (this.condition.isAlive()) return true;
		return !(isDead = true);
	}

	protected void setDead() {
		this.condition = null;
		if (this.isDead) return;
		this.isDead = true;
	}

	@Override
	public void setDead(Side side) {
		setDead();
		if (side == Side.CLIENT) doUpdateNodeDead();
	}

	@Override
	public void abandon() {
		setDead();
	}

	@Override
	public void updateServer(ICastable env) {
		IOS os = env.cast(IOS.class);
		if (os == null) return;
		if (this.condition == null) return;
		this.condition.update(os);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		readSaveData(new NBTSender(nbt));
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTSender saver = new NBTSender();
		writeSaveData(saver);
		return saver.tag();
	}

	public void writeSaveData(INBTWriter writer) {
		if (!this.isDead) writer.write("nd", true);
		if (this.condition != null) {
			writer.write("cid", (byte) this.condition.cid());
			this.condition.writeSaveData(writer);
		}
		if (this.digest != null) writer.write("dit", digest);
	}

	public void readSaveData(INBTReader reader) {
		this.isDead = !reader.nboolean("nd");
		this.condition = null;
		if (reader.has("cid")) {
			int id = reader.nbyte("cid");
			this.condition = ISoftDispalyTaskCondition.createCondition(id);
			if (this.condition != null) this.condition.readSaveData(reader);
		}
		this.digest = reader.has("dit") ? reader.string("dit") : null;
	}

	public static void registerAll() {
		GameDisplayCast.C_MAP.put(DeviceScanDisplay.ID, () -> new DeviceScanDisplay());
		GameDisplayCast.C_MAP.put(DeviceAskerDisplay.ID, () -> new DeviceAskerDisplay());
	}

}
