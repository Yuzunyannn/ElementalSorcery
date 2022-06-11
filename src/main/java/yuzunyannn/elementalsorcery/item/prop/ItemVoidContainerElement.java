package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.CapabilityProvider;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.entity.EntityThrow;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class ItemVoidContainerElement extends Item implements EntityThrow.IItemThrowAction {

	public ItemVoidContainerElement() {
		this.setTranslationKey("voidContainerElement");
		this.setMaxStackSize(1);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new CapabilityProvider.ElementInventoryUseProvider(stack, new ElementInventory() {
			@Override
			public int getMaxSizeInSlot(int slot) {
				return -1;
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

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.world.isRemote) return false;

		if (entityItem.ticksExisted % 5 == 0) {
			if (entityItem.onGround) {
				IElementInventory eInv = ElementHelper.getElementInventory(entityItem.getItem());
				if (!ElementHelper.isEmpty(eInv)) {
					ElementHelper.onElementFreeFromVoid(entityItem.world, entityItem.getPosition(), eInv, null);
					entityItem.setDead();
				}
			}
		}

		return super.onEntityItemUpdate(entityItem);
	}

	@Override
	public void onImpact(EntityThrow entity, RayTraceResult ray) {
		if (entity.world.isRemote) return;
		IElementInventory eInv = ElementHelper.getElementInventory(entity.getItemStack());
		if (!ElementHelper.isEmpty(eInv)) ElementHelper.onElementFreeFromVoid(entity.world, entity.getPosition(), eInv, null);
		else Block.spawnAsEntity(entity.world, new BlockPos(ray.hitVec), entity.getItemStack());
	}

}
