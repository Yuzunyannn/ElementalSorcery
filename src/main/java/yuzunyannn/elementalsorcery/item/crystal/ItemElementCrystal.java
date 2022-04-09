package yuzunyannn.elementalsorcery.item.crystal;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.CapabilityProvider;
import yuzunyannn.elementalsorcery.capability.ElementInventory;

public class ItemElementCrystal extends ItemCrystal {

	public ItemElementCrystal() {
		super("elementCrystal", 69.8f, 0x40c9c0);
		this.setMaxStackSize(1);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new CapabilityProvider.ElementInventoryUseProvider(stack, new ElementInventory() {
			@Override
			public int getMaxSizeInSlot(int slot) {
				return super.getMaxSizeInSlot(slot) / 10;
			}
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		IElementInventory inventory = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		inventory.loadState(stack);
		inventory.addInformation(worldIn, tooltip, flagIn);
	}
}
