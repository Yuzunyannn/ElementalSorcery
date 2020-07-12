package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.tile.TileDeconstructBox;

@Deprecated
public class ContainerDeconstructBox extends ContainerNormal<TileDeconstructBox> {

	public ContainerDeconstructBox(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileDeconstructBox) tileEntity);
		IItemHandler items;
		// 接受工具
		items = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		this.addSlotToContainer(new SlotItemHandler(items, 0, 107, 37));
		// 任意物品
		items = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		this.addSlotToContainer(new SlotItemHandler(items, 0, 55, 37));

		this.moreSlots = 2;
	}

	int last_power = -1;

	// 发消息
	@Override
	public void detectAndSendChanges() {
		// 全局同步
		super.detectAndSendChanges();
		// 燃烧
		int power = tileEntity.getField(TileDeconstructBox.FIELD_POWER);
		if (power != last_power) {
			last_power = power;
			for (int j = 0; j < this.listeners.size(); ++j) {
				this.listeners.get(j).sendWindowProperty(this, TileDeconstructBox.FIELD_POWER,
						tileEntity.getField(TileDeconstructBox.FIELD_POWER));
			}
		}
	}

	// 接受消息
	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		tileEntity.setField(id, data);
	}

}
