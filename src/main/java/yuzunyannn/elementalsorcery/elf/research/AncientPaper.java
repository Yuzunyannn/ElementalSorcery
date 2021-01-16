package yuzunyannn.elementalsorcery.elf.research;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.api.crafting.IItemCapbiltitySyn;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;

public class AncientPaper implements IItemCapbiltitySyn {

	protected Mantra mantra;
	protected int start, end;
	protected float progress = 1;
	protected KnowledgeType type;

	@Nullable
	public Mantra getMantra() {
		return mantra;
	}

	public AncientPaper setMantra(Mantra mantra) {
		this.mantra = mantra;
		return this;
	}

	public boolean hasMantra() {
		return mantra != null;
	}

	public int getStart() {
		return start;
	}

	public AncientPaper setStart(int start) {
		this.start = start;
		return this;
	}

	public int getEnd() {
		return end;
	}

	public AncientPaper setEnd(int end) {
		this.end = end;
		return this;
	}

	public float getProgress() {
		return progress;
	}

	public AncientPaper setProgress(float progress) {
		this.progress = progress;
		return this;
	}

	@Nullable
	public KnowledgeType getType() {
		return type;
	}

	public AncientPaper setType(KnowledgeType type) {
		this.type = type;
		return this;
	}

	public boolean hasType() {
		return type != null;
	}

	public boolean isLocked() {
		return this.hasType() && this.getProgress() < 1;
	}

	public AncientPaper() {
	}

	public AncientPaper(ItemStack stack) {
		this.loadState(stack);
	}

	@Override
	public boolean hasState(NBTTagCompound nbt) {
		return nbt.hasKey("know");
	}

	@Override
	public void loadState(NBTTagCompound nbt) {
		mantra = Mantra.getFromNBT(nbt);
		type = KnowledgeType.get(nbt.getString("type"));
		start = nbt.getByte("start");
		end = nbt.getByte("end");
		progress = nbt.getFloat("progress");
	}

	@Override
	public void saveState(NBTTagCompound nbt) {
		if (mantra != null) nbt.setString("id", mantra.getRegistryName().toString());
		if (type != null) nbt.setString("type", type.getNameId());
		nbt.setByte("start", (byte) MathHelper.clamp(start, 0, 100));
		nbt.setByte("end", (byte) MathHelper.clamp(end, 0, 100));
		nbt.setFloat("progress", this.progress);
	}

	public static boolean hasMantra(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return false;
		return Mantra.getFromNBT(nbt) != null;
	}

}
