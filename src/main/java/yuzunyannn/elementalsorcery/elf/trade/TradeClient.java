package yuzunyannn.elementalsorcery.elf.trade;

import java.util.Set;
import java.util.TreeSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TradeClient extends Trade {

	Set<Integer> soldOut = new TreeSet<>();

	public TradeClient(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	@Override
	public void sell(int index, int count) {

	}

	@Override
	public void reclaim(int index, int count) {

	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		if (nbt.hasKey("counts")) {
			int[] cs = nbt.getIntArray("counts");
			for (int i = 0; i < cs.length; i++) {
				if (cs[i] <= 0) this.setSoldOut(i);
			}
		}
	}

	public void setSoldOut(int index) {
		soldOut.add(index);
	}

	public void setFilling(int index) {
		soldOut.remove(index);
	}

	@Override
	public int stock(int index) {
		return soldOut.contains(index) ? 0 : 1;
	}

}
