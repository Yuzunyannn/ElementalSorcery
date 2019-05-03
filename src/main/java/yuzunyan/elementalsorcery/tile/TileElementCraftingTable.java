package yuzunyan.elementalsorcery.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import yuzunyan.elementalsorcery.ElementalSorcery;
import yuzunyan.elementalsorcery.api.crafting.IRecipe;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.api.util.ElementHelper;
import yuzunyan.elementalsorcery.building.Buildings;
import yuzunyan.elementalsorcery.building.MultiBlock;
import yuzunyan.elementalsorcery.capability.ElementInventory;
import yuzunyan.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyan.elementalsorcery.crafting.RecipeManagement;
import yuzunyan.elementalsorcery.util.item.ItemStackHandlerInventory;

public class TileElementCraftingTable extends TileStaticMultiBlockWithInventory implements ICraftingLaunch {

	public TileElementCraftingTable() {
		super(9);
	}

	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.ELEMENT_CRAFTING_ALTAR, this, new BlockPos(0, -2, 0));
		structure.addSpecialBlock(new BlockPos(0, 1, 3));
		structure.addSpecialBlock(new BlockPos(0, 1, -3));
		structure.addSpecialBlock(new BlockPos(3, 1, 0));
		structure.addSpecialBlock(new BlockPos(-3, 1, 0));
	}

	// 进行时的仓库
	ItemStackHandlerInventory working_inventory = null;
	// 进行时结果列表
	ItemStackHandlerInventory working_result = null;
	// 进行时的剩余需要元素
	ElementInventory working_einv = null;
	// 进行时的合成表
	IRecipe working_irecipe = null;
	// 尝试tick
	int try_tick = 0;
	// 玩家
	EntityPlayer player = null;
	// 开始前等待时间
	int startTime = 0;
	// 结束前等待时间
	int endTime = 0;
	// 正在进行
	protected boolean working = false;

	@Override
	public boolean craftingBegin(CraftingType type, EntityPlayer player) {
		if (this.isEmpty())
			return false;
		if (!this.isIntact())
			return false;
		this.onCraftMatrixChanged();
		if (this.getOutput().isEmpty())
			return false;
		this.craftingRecovery(type, player);
		this.markDirty();
		return working;
	}

	@Override
	public void craftingRecovery(CraftingType type, EntityPlayer player) {
		this.player = player;
		this.working = true;
		if (working_inventory == null)
			working_inventory = new ItemStackHandlerInventory(this.getSizeInventory());
		else {
			IRecipe irecipe = RecipeManagement.instance.findMatchingRecipe(working_inventory, world);
			if (irecipe != null)
				result = irecipe.getCraftingResult(working_inventory).copy();
		}
		if (working_result == null)
			working_result = new ItemStackHandlerInventory(4);
		working_irecipe = null;
		try_tick = 100;
		startTime = 80;
		endTime = 80;
	}

	@Override
	public boolean isWorking() {
		return working;
	}

	@Override
	public void craftingUpdate() {
		if (result.isEmpty())
			return;
		// 寻找合成表
		if (working_irecipe == null) {
			working_irecipe = RecipeManagement.instance.findMatchingRecipe(working_inventory, world);
			if (working_irecipe == null) {
				result = ItemStack.EMPTY;
				return;
			}
			List<ElementStack> needs = working_irecipe.getNeedElements();
			if (needs != null && !needs.isEmpty()) {
				working_einv = new ElementInventory(needs.size());
				for (int i = 0; i < needs.size(); i++) {
					working_einv.setStackInSlot(i, needs.get(i).copy());
				}
			}
		}
		if (startTime >= 0) {
			startTime--;
			return;
		}
		if (Math.random() < 0.25)
			return;
		// 如果有需要的元素
		if (working_einv != null) {
			// 寻找一个所需元素
			ElementStack need = working_einv.getStackInSlot(rand.nextInt(working_einv.getSlots()));
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
				ElementStack get = this.getElementFromSpPlace(need, this.pos.up());
				if (!get.arePowerfulAndMoreThan(need)) {
					old.grow(need);
					this.try_tick--;
				}
				if (this.try_tick <= 0) {
					result = ItemStack.EMPTY;
				}
				return;
			}
		}
		// 产出结果
		result = working_irecipe.getCraftingResult(working_inventory);
		if (result.isEmpty()) {
			return;
		}
		working_irecipe.shrink(working_inventory);
		working_irecipe = null;
		if (world.isRemote)
			return;
		ItemStack out = result.copy();
		if (this.player != null)
			out.onCrafting(world, this.player, out.getCount());
		else {
			try {
				out.getItem().onCreated(out, this.world, null);
			} catch (NullPointerException e) {
				ElementalSorcery.logger.warn("合祭坛合成时，onCreated出现空指针异常，这是一个非常不期望的结果", e);
			}
		}
		if (this.addResult(out)) {
			this.markDirty();
		} else
			result = ItemStack.EMPTY;
	}

	@Override
	public void craftingUpdateClient() {
		this.craftingUpdate();
	}

	@Override
	public int craftingEnd(List<ItemStack> list) {
		working = false;
		// 如果祭坛不完整，或者没有生成任何物品
		if (!this.isIntact() || working_result.isEmpty()) {
			working_inventory = null;
			working_result = null;
			player = null;
			return ICraftingLaunch.FAIL;
		}
		list.clear();
		// 重置剩余的物品
		for (int i = 0; i < working_inventory.getSlots(); i++) {
			ItemStack stack = working_inventory.getStackInSlot(i);
			if (!stack.isEmpty())
				list.add(stack);
		}
		// 添加新产生的物品
		for (int i = 0; i < working_result.getSlots(); i++) {
			ItemStack stack = working_result.getStackInSlot(i);
			if (!stack.isEmpty())
				list.add(stack);
		}
		working_inventory = null;
		working_result = null;
		player = null;
		this.markDirty();
		return ICraftingLaunch.SUCCESS;
	}

	@Override
	public List<ItemStack> commitItems() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < this.getSizeInventory(); i++) {
			ItemStack stack = this.getStackInSlot(i);
			if (!stack.isEmpty()) {
				working_inventory.setStackInSlot(i, stack);
				list.add(stack);
			}
		}
		this.clear();
		return list;
	}

	@Override
	public boolean canContinue() {
		if (result.isEmpty()) {
			if (endTime >= 0) {
				endTime--;
				return this.isIntact();
			} else
				return false;
		}
		return this.isIntact();
	}

	@Override
	public boolean checkType(CraftingType type) {
		return type == CraftingType.ELEMENT_CRAFTING;
	}

	// 产出结果，该引用的对象不应该被修改，该变量同时起到标定作用
	private ItemStack result = ItemStack.EMPTY;

	public ItemStack getOutput() {
		return result;
	}

	public void onCraftMatrixChanged() {
		IRecipe irecipe = RecipeManagement.instance.findMatchingRecipe(this, world);
		if (irecipe == null) {
			result = ItemStack.EMPTY;
			return;
		}
		result = irecipe.getCraftingResult(this).copy();
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

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("work_inv")) {
			working_inventory = new ItemStackHandlerInventory();
			working_inventory.deserializeNBT(compound.getCompoundTag("work_inv"));
		}
		if (compound.hasKey("work_res")) {
			working_result = new ItemStackHandlerInventory();
			working_result.deserializeNBT(compound.getCompoundTag("work_res"));
		}
		if (compound.hasKey("work_einv")) {
			working_einv = new ElementInventory();
			working_einv.deserializeNBT(compound.getCompoundTag("work_einv"));
		}
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (working_inventory != null)
			compound.setTag("work_inv", working_inventory.serializeNBT());
		if (working_result != null)
			compound.setTag("work_res", working_result.serializeNBT());
		if (working_einv != null)
			compound.setTag("work_einv", working_einv.serializeNBT());
		return super.writeToNBT(compound);
	}

}
