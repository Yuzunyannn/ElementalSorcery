package yuzunyannn.elementalsorcery.crafting.element;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.element.ElementStack;

//默认水桶到元素
public class DefaultBucketToElement implements IToElement {

	public Map<Fluid, IToElementInfo> fluidToElementMap = new HashMap<>();

	@Override
	public IToElementInfo toElement(ItemStack stack) {
		IFluidHandlerItem fhi = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		if (fhi == null) return null;
		FluidStack fstack = fhi.drain(1000, false);
		if (fstack == null || fstack.amount < 1000) return null;
		Fluid fluid = fstack.getFluid();
		if (fluid == null) return null;
		IToElementInfo info = fluidToElementMap.get(fluid);
		if (info == null) return null;
		ItemStack container = fhi.getContainer().copy();
		fhi = container.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		fhi.drain(1000, true);
		return ToElementInfoStatic.create(info.complex(), fhi.getContainer(), info.element());
	}

	public void add(Fluid fluid, int complex, ElementStack... estacks) {
		fluidToElementMap.put(fluid, ToElementInfoStatic.create(complex, estacks));
	}

}
