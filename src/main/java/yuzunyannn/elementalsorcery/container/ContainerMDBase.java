package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;

public class ContainerMDBase<T extends TileMDBase> extends ContainerNormal<T> {

	/** 容器更新检测使用 */
	private int[] fieldDatas;

	public ContainerMDBase(EntityPlayer player, T tileEntity) {
		super(player, tileEntity);
		fieldDatas = new int[tileEntity.getFieldCount()];
		for (int i = 0; i < fieldDatas.length; i++) fieldDatas[i] = -1;
	}

	protected void addSlotItemHandler(EnumFacing facing, int index, int x, int y) {
		IItemHandler items = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
		if (items == null) return;
		this.addSlotToContainer(new SlotItemHandler(items, index, x, y));
		this.moreSlots++;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (fieldDatas.length < tileEntity.getFieldCount()) fieldDatas = new int[tileEntity.getFieldCount()];

		for (int i = 0; i < tileEntity.getFieldCount(); i++) {
			if (tileEntity.getField(i) != fieldDatas[i]) {
				fieldDatas[i] = tileEntity.getField(i);
				for (int j = 0; j < listeners.size(); ++j) {
					listeners.get(j).sendWindowProperty(this, i, tileEntity.getField(i));
				}
			}
		}

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		this.tileEntity.setField(id, data);
	}

}
