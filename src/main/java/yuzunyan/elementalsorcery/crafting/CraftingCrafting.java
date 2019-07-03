package yuzunyan.elementalsorcery.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import yuzunyan.elementalsorcery.ElementalSorcery;
import yuzunyan.elementalsorcery.api.crafting.IRecipe;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.api.util.ElementHelper;
import yuzunyan.elementalsorcery.capability.ElementInventory;
import yuzunyan.elementalsorcery.tile.TileStaticMultiBlock;
import yuzunyan.elementalsorcery.util.NBTHelper;
import yuzunyan.elementalsorcery.util.item.ItemStackHandlerInventory;

public class CraftingCrafting implements ICraftingCommit {

	private List<ItemStack> itemList = new ArrayList<ItemStack>();
	// 进行时的仓库
	private ItemStackHandlerInventory working_inventory;
	// 进行时结果列表
	private ItemStackHandlerInventory working_result = new ItemStackHandlerInventory(4);
	// 进行时的剩余需要元素
	private ElementInventory working_einv = null;
	// 进行时的合成表
	private IRecipe working_irecipe = null;
	// 尝试tick
	private int try_tick = 100;

	public CraftingCrafting(IInventory inv) {
		working_inventory = new ItemStackHandlerInventory(inv.getSizeInventory());
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				working_inventory.setStackInSlot(i, stack);
				itemList.add(stack);
			}
		}
	}

	public CraftingCrafting(NBTTagCompound nbt) {
		if (nbt == null) {
			ElementalSorcery.logger.warn("CraftingCrafting恢复的时候，nbt为NULL！");
			nbt = new NBTTagCompound();
		}
		this.deserializeNBT(nbt);
	}

	public ItemStack getResult(World world) {
		IRecipe irecipe = RecipeManagement.instance.findMatchingRecipe(working_inventory, world);
		if (irecipe != null)
			return irecipe.getCraftingResult(working_inventory).copy();
		return ItemStack.EMPTY;
	}

	// 更新一次
	public boolean update(TileStaticMultiBlock tileMul, EntityLivingBase player) {
		// 寻找合成表
		if (working_irecipe == null) {
			working_irecipe = RecipeManagement.instance.findMatchingRecipe(working_inventory, tileMul.getWorld());
			if (working_irecipe == null) {
				return false;
			}
			List<ElementStack> needs = working_irecipe.getNeedElements();
			if (needs != null && !needs.isEmpty()) {
				working_einv = new ElementInventory(needs.size());
				for (int i = 0; i < needs.size(); i++) {
					working_einv.setStackInSlot(i, needs.get(i).copy());
				}
			}
		}
		// 如果有需要的元素
		if (working_einv != null) {
			// 寻找一个所需元素
			ElementStack need = working_einv.getStackInSlot(TileStaticMultiBlock.rand.nextInt(working_einv.getSlots()));
			if (need.isEmpty()) {
				if (!ElementHelper.isEmpty(working_einv)) {
					for (int i = 0; i < working_einv.getSlots(); i++) {
						need = working_einv.getStackInSlot(i);
						if (!need.isEmpty())
							break;
					}
				}
			}
			// 如果还需要元素
			if (!need.isEmpty()) {
				ElementStack old = need;
				need = need.splitStack(1);
				ElementStack get = tileMul.getElementFromSpPlace(need, tileMul.getPos().up());
				if (!get.arePowerfulAndMoreThan(need)) {
					old.grow(need);
					this.try_tick--;
				}
				if (this.try_tick <= 0) {
					return false;
				}
				return true;
			}
		}
		// 产出结果
		ItemStack result = working_irecipe.getCraftingResult(working_inventory);
		if (result.isEmpty()) {
			return false;
		}
		working_irecipe.shrink(working_inventory);
		working_irecipe = null;
		if (tileMul.getWorld().isRemote)
			return true;
		ItemStack out = result.copy();
		if (player instanceof EntityPlayer)
			out.onCrafting(tileMul.getWorld(), (EntityPlayer) player, out.getCount());
		else {
			try {
				out.getItem().onCreated(out, tileMul.getWorld(), null);
			} catch (NullPointerException e) {
				ElementalSorcery.logger.warn("合祭坛合成时，onCreated出现空指针异常，这是一个非常不期望的结果", e);
			}
		}
		if (this.addResult(out)) {
			tileMul.markDirty();
		} else
			return false;
		return true;
	}

	// 添加一个stack到结果列表
	private boolean addResult(ItemStack stack) {
		for (int i = 0; i < working_result.getSlots() - 1; i++) {
			stack = working_result.insertItem(i, stack, false);
			if (stack.isEmpty())
				return true;
		}
		working_result.insertItem(working_result.getSlots() - 1, stack, false);
		return false;
	}

	// 结束
	public void end() {
		itemList.clear();
		// 重置剩余的物品
		for (int i = 0; i < working_inventory.getSlots(); i++) {
			ItemStack stack = working_inventory.getStackInSlot(i);
			if (!stack.isEmpty())
				itemList.add(stack);
		}
		// 添加新产生的物品
		for (int i = 0; i < working_result.getSlots(); i++) {
			ItemStack stack = working_result.getStackInSlot(i);
			if (!stack.isEmpty())
				itemList.add(stack);
		}
	}

	// 是否有结果
	public boolean haveResult() {
		return !working_result.isEmpty();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		if (working_inventory != null)
			nbt.setTag("work_inv", working_inventory.serializeNBT());
		if (working_result != null)
			nbt.setTag("work_res", working_result.serializeNBT());
		if (working_einv != null)
			nbt.setTag("work_einv", working_einv.serializeNBT());
		NBTHelper.setItemList(nbt, "itemList", itemList);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("work_inv"))
			working_inventory = new ItemStackHandlerInventory(nbt.getCompoundTag("work_inv"));
		else
			working_inventory = new ItemStackHandlerInventory(9);
		if (nbt.hasKey("work_res"))
			working_result = new ItemStackHandlerInventory(nbt.getCompoundTag("work_res"));
		if (nbt.hasKey("work_einv"))
			working_einv = new ElementInventory(nbt.getCompoundTag("work_einv"));
		if (nbt.hasKey("itemList"))
			itemList = NBTHelper.getItemList(nbt, "itemList");
	}

	@Override
	public List<ItemStack> getItems() {
		return itemList;
	}

}
