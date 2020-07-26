package yuzunyannn.elementalsorcery.container;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.stats.RecipeBook;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyannn.elementalsorcery.tile.altar.TileSupremeTable;

public class ContainerSupremeTable extends ContainerNormal<TileSupremeTable> {
	public final static int[] craftingRelative = new int[] { 0, 0, 18, 0, 36, 0, 0, 18, 18, 18, 36, 18, 0, 36, 18, 36,
			36, 36, -36, -36, -18, -36, 54, -36, 72, -36, -36, -18, -18, -18, 54, -18, 72, -18, -36, 54, -18, 54, 54,
			54, 72, 54, -36, 72, -18, 72, 54, 72, 72, 72 };

	static final public byte MODE_NONE = 0;
	static final public byte MODE_NATIVE_CRAFTING = 1;
	static final public byte MODE_ELEMENT_CRAFTING = 2;
	static final public byte MODE_PLATFORM_NONE = 10;
	static final public byte MODE_DECONSTRUCT = 11;
	static final public byte MODE_CONSTRUCT = 12;
	public byte showMode = 0;
	int resultSlotId;
	public InventoryCraftResult result = new InventoryCraftResult();
	/*** mc原始合成 */
	private boolean nativeCrafting = false;
	public InventoryCrafting craftMatrix;

	public ContainerSupremeTable(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileSupremeTable) tileEntity, 36, 160);
		IItemHandler items = this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		// 添加slot
		for (int i = 0; i < items.getSlots(); i++) {
			int x = ContainerSupremeTable.craftingRelative[i * 2];
			int y = ContainerSupremeTable.craftingRelative[i * 2 + 1];
			if (i == 3 || i == 5) this.addSlotToContainer(new SlotItemHandler(items, i, 89 + x, 58 + y) {
				@Override
				public void onSlotChanged() {
					ContainerSupremeTable.this
							.onCraftMatrixChanged(ContainerSupremeTable.this.tileEntity);
					super.onSlotChanged();
				}

				@Override
				@SideOnly(Side.CLIENT)
				public boolean isEnabled() {
					return showMode != MODE_DECONSTRUCT;
				}
			});
			else this.addSlotToContainer(new SlotItemHandler(items, i, 89 + x, 58 + y) {
				@Override
				public void onSlotChanged() {
					ContainerSupremeTable.this
							.onCraftMatrixChanged(ContainerSupremeTable.this.tileEntity);
					super.onSlotChanged();
				}
			});
		}
		// 添加result的slot
		craftMatrix = this.tileEntity.toInventoryCrafting(this);
		this.resultSlotId = this.addSlotToContainer(new SlotCrafting(player, craftMatrix, result, 0, 107, 130) {

			@Override
			public boolean canTakeStack(EntityPlayer playerIn) {
				return nativeCrafting;
			}

		}).slotNumber;
		this.onCraftMatrixChanged(this.tileEntity);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return super.canInteractWith(playerIn) && tileEntity.isIntact();
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		World world = this.player.getEntityWorld();
		nativeCrafting = false;
		showMode = MODE_NONE;
		// 先寻找es合成表
		String type = this.tileEntity.onCraftMatrixChanged();
		if (type == null) type = "";
		ItemStack platfromItem;
		switch (type) {
		case ICraftingLaunch.TYPE_ELEMENT_CRAFTING:
			ItemStack output = this.tileEntity.getOutput();
			this.result.setInventorySlotContents(0, output);
			showMode = MODE_ELEMENT_CRAFTING;
			return;
		case ICraftingLaunch.TYPE_ELEMENT_DECONSTRUCT:
			platfromItem = this.tileEntity.getPlatformItem();
			this.result.setInventorySlotContents(0, platfromItem);
			showMode = MODE_DECONSTRUCT;
			return;
		case ICraftingLaunch.TYPE_ELEMENT_CONSTRUCT:
			platfromItem = this.tileEntity.getPlatformItem();
			this.result.setInventorySlotContents(0, platfromItem);
			showMode = MODE_CONSTRUCT;
			return;
		default:
			platfromItem = this.tileEntity.getPlatformItem();
			if (!platfromItem.isEmpty()) {
				showMode = MODE_PLATFORM_NONE;
				this.result.setInventorySlotContents(0, platfromItem);
			} else {
				// 寻找mc原版合成表
				this.slotChangedCraftingGrid(world, this.player, this.craftMatrix, this.result);
				if (!this.result.isEmpty()) {
					nativeCrafting = true;
					showMode = MODE_NATIVE_CRAFTING;
				}
			}
			break;
		}
	}

	@Override
	protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting craftMatrix,
			InventoryCraftResult craftResult) {
		ItemStack itemstack = ItemStack.EMPTY;
		IRecipe irecipe = CraftingManager.findMatchingRecipe(craftMatrix, world);

		RecipeBook book = world.isRemote ? ((EntityPlayerSP) player).getRecipeBook()
				: ((EntityPlayerMP) player).getRecipeBook();
		if (irecipe != null && (!world.getGameRules().getBoolean("doLimitedCrafting") || book.isUnlocked(irecipe))) {
			craftResult.setRecipeUsed(irecipe);
			itemstack = irecipe.getCraftingResult(craftMatrix);
		}
		craftResult.setInventorySlotContents(0, itemstack);
	}

	private byte lastShowMode = 0;

	@Override
	public void detectAndSendChanges() {
		this.detectAndSendChangesResult();
		super.detectAndSendChanges();
		if (lastShowMode == showMode) return;
		lastShowMode = showMode;
		for (int j = 0; j < this.listeners.size(); ++j) {
			((IContainerListener) this.listeners.get(j)).sendWindowProperty(this, 0, this.showMode);
		}
	}

	// 发送产出结果的数据
	public void detectAndSendChangesResult() {
		ItemStack itemstack = this.inventorySlots.get(resultSlotId).getStack();
		ItemStack itemstack1 = this.inventoryItemStacks.get(resultSlotId);
		if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
			itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
			this.inventoryItemStacks.set(resultSlotId, itemstack1);
			for (int j = 0; j < this.listeners.size(); ++j) {
				IContainerListener listener = listeners.get(j);
				if (listener instanceof EntityPlayerMP) ((EntityPlayerMP) listener).connection
						.sendPacket(new SPacketSetSlot(this.windowId, resultSlotId, itemstack));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		switch (id) {
		case 0:
			this.showMode = (byte) data;
			break;

		default:
			break;
		}
	}

}
