package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.tile.device.TileComputer;

public class ContainerComputerEditor extends ContainerNormal<TileComputer> {

	public static final int SLOT_COLVER_FRONT = 0;

	protected IComputer computer;
	public int slotType;

	public ContainerComputerEditor(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileComputer) tileEntity, true, 48, 84);
		this.computer = Computer.from(tileEntity);
		this.slotType = SLOT_COLVER_FRONT;
		IItemHandlerModifiable hander = this.tileEntity.getEditorItemHandler();

		switch (this.slotType) {
		case SLOT_COLVER_FRONT:
			this.addSlotToContainer(new SlotItemHandler(hander, 0, 28, 33));
			this.moreSlots = 1;
			break;
		default:
			break;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (this.computer == null) return false;
		return super.canInteractWith(playerIn);
	}

	public IComputer getComputer() {
		return computer;
	}

}
