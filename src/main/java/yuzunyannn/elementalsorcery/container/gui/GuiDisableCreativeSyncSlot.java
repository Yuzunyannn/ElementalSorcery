package yuzunyannn.elementalsorcery.container.gui;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Deprecated
@SideOnly(Side.CLIENT)
public class GuiDisableCreativeSyncSlot extends Slot {

	public final Slot other;

	public GuiDisableCreativeSyncSlot(Slot other) {
		super(other.inventory, other.getSlotIndex(), other.xPos, other.yPos);
		this.other = other;
		this.slotNumber = other.slotNumber;
	}

	public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {
		other.onSlotChange(p_75220_1_, p_75220_2_);
	}

	public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
		return other.onTake(thePlayer, stack);
	}

	public boolean isItemValid(ItemStack stack) {
		return other.isItemValid(stack);
	}

	public ItemStack getStack() {
		return other.getStack();
	}

	public boolean getHasStack() {
		return other.getHasStack();
	}

	public void putStack(ItemStack stack) {
		other.putStack(stack);
	}

	public void onSlotChanged() {
		other.onSlotChanged();
	}

	public int getSlotStackLimit() {
		return other.getSlotStackLimit();
	}

	public int getItemStackLimit(ItemStack stack) {
		return other.getItemStackLimit(stack);
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	public String getSlotTexture() {
		return other.getSlotTexture();
	}

	public ItemStack decrStackSize(int amount) {
		return other.decrStackSize(amount);
	}

	public boolean isHere(IInventory inv, int slotIn) {
		return other.isHere(inv, slotIn);
	}

	public boolean canTakeStack(EntityPlayer playerIn) {
		return other.canTakeStack(playerIn);
	}

	@SideOnly(Side.CLIENT)
	public boolean isEnabled() {
		return other.isEnabled();
	}

	@SideOnly(Side.CLIENT)
	public net.minecraft.util.ResourceLocation getBackgroundLocation() {
		return other.getBackgroundLocation();
	}

	@SideOnly(Side.CLIENT)
	public void setBackgroundLocation(net.minecraft.util.ResourceLocation texture) {
		other.setBackgroundLocation(texture);
	}

	public void setBackgroundName(@Nullable String name) {
		other.setBackgroundName(name);
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	public net.minecraft.client.renderer.texture.TextureAtlasSprite getBackgroundSprite() {
		return other.getBackgroundSprite();
	}

	public int getSlotIndex() {
		return other.getSlotIndex();
	}

	public boolean isSameInventory(Slot other) {
		return this.other.isSameInventory(other);
	}
}
