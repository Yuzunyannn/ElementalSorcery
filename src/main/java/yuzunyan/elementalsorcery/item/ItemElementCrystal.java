package yuzunyan.elementalsorcery.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import yuzunyan.elementalsorcery.api.ability.IElementInventory;
import yuzunyan.elementalsorcery.api.util.ElementHelper;
import yuzunyan.elementalsorcery.capability.CapabilityProvider;
import yuzunyan.elementalsorcery.capability.ElementInventory;

public class ItemElementCrystal extends Item {
	public ItemElementCrystal() {
		this.setUnlocalizedName("elementCrystal");
		this.setMaxStackSize(1);
	}

	@Override
	@Nullable
	public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack,
			@Nullable NBTTagCompound nbt) {
		return new CapabilityProvider.ElementInventoryUseProvider(stack);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		IElementInventory inventory = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		inventory.loadState(stack);
		ElementHelper.addElementInformation(inventory, worldIn, tooltip, flagIn);
	}
}
