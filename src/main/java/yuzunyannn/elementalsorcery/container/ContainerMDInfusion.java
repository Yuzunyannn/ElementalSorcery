package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.tile.md.TileMDInfusion;

public class ContainerMDInfusion extends ContainerMDBase<TileMDInfusion> {

	public ContainerMDInfusion(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileMDInfusion) tileEntity);
		this.addSlotItemHandler(EnumFacing.NORTH, 0, 36, 42);
		this.addSlotItemHandler(EnumFacing.NORTH, 1, 58, 50);
		this.addSlotItemHandler(EnumFacing.NORTH, 2, 80, 58);
		this.addSlotItemHandler(EnumFacing.NORTH, 3, 102, 50);
		this.addSlotItemHandler(EnumFacing.NORTH, 4, 124, 42);
	}

	@Override
	protected void addSlotItemHandler(EnumFacing facing, int index, int x, int y) {
		IItemHandler items = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
		if (items == null) return;
		this.addSlotToContainer(new SlotItemHandler(items, index, x, y) {
			@Override
			public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
				// 成就
				if (thePlayer instanceof EntityPlayerMP)
					ESCriteriaTriggers.INFUSION_TAKE.trigger((EntityPlayerMP) thePlayer, stack);
				return super.onTake(thePlayer, stack);
			}
		});
		this.moreSlots++;
	}
}
