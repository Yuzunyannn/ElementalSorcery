package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.tile.TileHearth;
import yuzunyannn.elementalsorcery.util.IField;

public class ContainerHearth extends ContainerNormal<TileHearth> implements IField {

	public ContainerHearth(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileHearth) tileEntity);
		IItemHandler items = this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
				EnumFacing.NORTH);
		// 添加放置物品的窗口
		for (int i = 0; i < 4; i++) {
			this.addSlotToContainer(new SlotItemHandler(items, i, 33 + 31 * i, 44));
		}
		this.moreSlots = 4;
	}

	// 服务器发消息
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int j = 0; j < this.listeners.size(); ++j) {
			this.listeners.get(j).sendWindowProperty(this, TileHearth.FIELD_BURN_TIME,
					tileEntity.getField(TileHearth.FIELD_BURN_TIME));
			this.listeners.get(j).sendWindowProperty(this, TileHearth.FIELD_TOTAL_BURN_TIME,
					tileEntity.getField(TileHearth.FIELD_TOTAL_BURN_TIME));
		}
	}

	// 客户端接消息
	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		tileEntity.setField(id, data);
	}

	@Override
	public int getField(int id) {
		return tileEntity.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		tileEntity.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return tileEntity.getFieldCount();
	}

	// 获取名字
	public String getUnlocalizedName() {
		return tileEntity.getBlockUnlocalizedName();
	}

}
