package yuzunyannn.elementalsorcery.crafting.altar;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.crafting.IRecipe;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyannn.elementalsorcery.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.render.entity.AnimeRenderCrafting;
import yuzunyannn.elementalsorcery.tile.altar.TileStaticMultiBlock;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerInventory;

public class CraftingCrafting implements ICraftingAltar {

	private List<ItemStack> itemList = new ArrayList<ItemStack>();
	// 进行时的仓库
	private ItemStackHandlerInventory workingInventory;
	// 进行时结果列表
	private ItemStackHandlerInventory workingResult = new ItemStackHandlerInventory(4);
	// 进行时的剩余需要元素
	private ElementInventory workingEInventory = null;
	// 进行时的合成表
	private IRecipe workingIrecipe = null;
	// 尝试tick
	private int tryTick = 50;
	// 玩家
	private EntityLivingBase player = null;
	// 是否ok
	private boolean isOk = true;
	// 周期性刷新
	private int tick = 0;

	public CraftingCrafting(IInventory inv) {
		workingInventory = new ItemStackHandlerInventory(inv.getSizeInventory());
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				workingInventory.setStackInSlot(i, stack);
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

	@Override
	public void setWorldInfo(World world, BlockPos pos, EntityLivingBase player) {
		this.player = player;
	}

	public ItemStack getResult(World world) {
		IRecipe irecipe = RecipeManagement.instance.findMatchingRecipe(workingInventory, world);
		if (irecipe != null) return irecipe.getCraftingResult(workingInventory).copy();
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canContinue(TileStaticMultiBlock tileMul) {
		return tileMul.isIntact() && this.isOk;
	}

	// 更新一次
	@Override
	public void update(TileStaticMultiBlock tileMul) {
		if (!this.isOk) return;
		this.tick++;
		if (this.tick % 3 != 0) return;
		// 寻找合成表
		if (workingIrecipe == null) {
			workingIrecipe = RecipeManagement.instance.findMatchingRecipe(workingInventory, tileMul.getWorld());
			if (workingIrecipe == null) {
				this.isOk = false;
				return;
			}
			List<ElementStack> needs = workingIrecipe.getNeedElements();
			if (needs != null && !needs.isEmpty()) {
				workingEInventory = new ElementInventory(needs.size());
				for (int i = 0; i < needs.size(); i++) {
					workingEInventory.setStackInSlot(i, needs.get(i).copy());
				}
			}
		}
		// 如果有需要的元素
		if (workingEInventory != null) {
			// 寻找一个所需元素
			ElementStack need = workingEInventory.getStackInSlot(tileMul.rand.nextInt(workingEInventory.getSlots()));
			if (need.isEmpty()) {
				if (!ElementHelper.isEmpty(workingEInventory)) {
					for (int i = 0; i < workingEInventory.getSlots(); i++) {
						need = workingEInventory.getStackInSlot(i);
						if (!need.isEmpty()) break;
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
					this.tryTick--;
				}
				if (this.tryTick <= 0) {
					this.isOk = false;
					return;
				}
				return;
			}
		}
		// 产出结果
		ItemStack result = workingIrecipe.getCraftingResult(workingInventory);
		if (result.isEmpty()) {
			this.isOk = false;
			return;
		}
		IRecipe irecipe = workingIrecipe;
		workingIrecipe.shrink(workingInventory);
		workingIrecipe = null;
		if (tileMul.getWorld().isRemote) return;
		ItemStack out = result.copy();
		if (player instanceof EntityPlayer) out.onCrafting(tileMul.getWorld(), (EntityPlayer) player, out.getCount());
		else {
			try {
				out.getItem().onCreated(out, tileMul.getWorld(), null);
			} catch (NullPointerException e) {
				ElementalSorcery.logger.warn("合祭坛合成时，onCreated出现空指针异常，这是一个非常不期望的结果", e);
			}
		}
		if (player instanceof EntityPlayerMP)
			ESCriteriaTriggers.ELEMENT_CRAFT.trigger((EntityPlayerMP) player, irecipe);
		if (this.addResult(out)) tileMul.markDirty();
		else {
			this.isOk = false;
			return;
		}
		return;
	}

	// 添加一个stack到结果列表
	private boolean addResult(ItemStack stack) {
		for (int i = 0; i < workingResult.getSlots() - 1; i++) {
			stack = workingResult.insertItem(i, stack, false);
			if (stack.isEmpty()) return true;
		}
		workingResult.insertItem(workingResult.getSlots() - 1, stack, false);
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ICraftingLaunchAnime getAnime() {
		return new AnimeRenderCrafting();
	}

	public ItemStackHandlerInventory getWorkingInventory() {
		return workingInventory;
	}

	@Override
	public boolean end(TileStaticMultiBlock tileMul) {
		if (workingResult.isEmpty()) return false;
		if (!tileMul.isIntact()) return false;
		itemList.clear();
		// 重置剩余的物品
		for (int i = 0; i < workingInventory.getSlots(); i++) {
			ItemStack stack = workingInventory.getStackInSlot(i);
			if (!stack.isEmpty()) itemList.add(stack);
		}
		// 添加新产生的物品
		for (int i = 0; i < workingResult.getSlots(); i++) {
			ItemStack stack = workingResult.getStackInSlot(i);
			if (!stack.isEmpty()) itemList.add(stack);
		}
		return true;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		if (workingInventory != null) nbt.setTag("work_inv", workingInventory.serializeNBT());
		if (workingResult != null) nbt.setTag("work_res", workingResult.serializeNBT());
		if (workingEInventory != null) nbt.setTag("work_einv", workingEInventory.serializeNBT());
		NBTHelper.setItemList(nbt, "itemList", itemList);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("work_inv")) workingInventory = new ItemStackHandlerInventory(nbt.getCompoundTag("work_inv"));
		else workingInventory = new ItemStackHandlerInventory(9);
		if (nbt.hasKey("work_res")) workingResult = new ItemStackHandlerInventory(nbt.getCompoundTag("work_res"));
		if (nbt.hasKey("work_einv")) workingEInventory = new ElementInventory(nbt.getCompoundTag("work_einv"));
		if (nbt.hasKey("itemList")) itemList = NBTHelper.getItemList(nbt, "itemList");
	}

	@Override
	public List<ItemStack> getItems() {
		return itemList;
	}

}
