package yuzunyannn.elementalsorcery.tile;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemKynaiteTools;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.item.ItemScroll;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.util.RandomHelper;

public class TileStela extends TileEntityNetwork {

	private EnumFacing face = EnumFacing.NORTH;

	public void setFace(EnumFacing face) {
		this.face = face;
	}

	public EnumFacing getFace() {
		return face;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		face = EnumFacing.values()[compound.getByte("face")];
		this.inv_item.deserializeNBT(compound.getCompoundTag("inv_item"));
		this.inv_paper.deserializeNBT(compound.getCompoundTag("inv_paper"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setByte("face", (byte) face.ordinal());
		compound.setTag("inv_item", this.inv_item.serializeNBT());
		compound.setTag("inv_paper", this.inv_paper.serializeNBT());
		return super.writeToNBT(compound);
	}

	protected ItemStackHandler inv_item = new ItemStackHandler(1);
	protected ItemStackHandler inv_paper = new ItemStackHandler(1) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			// 如果物品不是羊皮纸
			if (stack.getItem() != ESInitInstance.ITEMS.PARCHMENT)
				return stack;
			// 有内容的东西不能放入
			if (Pages.getPageId(stack) != 0)
				return stack;
			return super.insertItem(slot, stack, simulate);
		}
	};

	// 拥有能力
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	// 获取能力
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			if (facing == face.getOpposite())
				return (T) inv_item;
			return (T) inv_paper;
		}
		return super.getCapability(capability, facing);
	}

	// 是否正在工作
	public boolean isRunning() {
		ItemStack paper = inv_paper.extractItem(0, 1, true);
		if (paper.isEmpty())
			return false;
		ItemStack item = inv_item.extractItem(0, 1, true);
		if (item.isEmpty())
			return false;
		return canRunning();
	}

	// 检测周围的运行条件
	public boolean canRunning() {
		EnumFacing face = this.face.rotateY();
		IBlockState state = this.world.getBlockState(pos.offset(face));
		boolean haveFlower = state.getBlock() instanceof BlockFlower;
		state = this.world.getBlockState(pos.offset(face.getOpposite()));
		haveFlower &= state.getBlock() instanceof BlockFlower;
		return haveFlower;
	}

	// 工作一次
	public void doOnce() {
		if (!this.isRunning())
			return;
		ItemStack item = inv_item.extractItem(0, 64, true);
		int[] idArray = TileStela.pageAwareFromItem(item);
		if (idArray.length == 0) {
			return;
		}
		ItemStack paper = inv_paper.extractItem(0, idArray.length, false);
		if (idArray.length == 1) {
			this.produce(ItemParchment.getParchment(idArray[0]));
			return;
		}
		int size = Math.min(paper.getCount(), idArray.length);
		int[] ids = new int[size];
		for (int i = 0; i < size; i++)
			ids[i] = idArray[i];
		this.produce(ItemScroll.getScroll(ids));
	}

	// 产生物品
	public void produce(ItemStack stack) {
		BlockPos pos = this.pos.offset(this.face.getOpposite());
		TileEntity tile = this.world.getTileEntity(pos);
		if (tile != null) {
			IItemHandler heandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.face);
			if (heandler != null) {
				// 插入
				for (int i = 0; i < heandler.getSlots(); i++) {
					stack = heandler.insertItem(i, stack, false);
					if (stack.isEmpty()) {
						return;
					}
				}
			}
		}
		Block.spawnAsEntity(this.world, this.pos, stack);
	}

	public static Map<ResourceLocation, int[]> itemToIds = new HashMap<ResourceLocation, int[]>();

	/** 从物品中获得page的id */
	static public int[] pageAwareFromItem(ItemStack stack) {
		int[] ids = null;
		ids = TileStela.pageAware0(stack);
		if (ids != null)
			return ids;
		ResourceLocation rname = stack.getItem().getRegistryName();
		if (itemToIds.containsKey(rname))
			return RandomHelper.randomSelect(itemToIds.get(rname));
		ids = TileStela.pageAware1(stack);
		if (ids != null)
			return ids;
		ids = TileStela.pageAware2(stack);
		if (ids != null)
			return ids;
		return new int[0];
	}

	/** 初始化 */
	static public void init() {
		// 蓝晶石矿
		addToMap(ESInitInstance.BLOCKS.KYNAITE_ORE, Pages.ABOUT_KYNAITE);
		// 蓝晶石方块
		addToMap(ESInitInstance.BLOCKS.KYNAITE_BLOCK, Pages.ABOUT_KYNAITE);
		// 烧炼箱
		addToMap(ESInitInstance.BLOCKS.SMELT_BOX, Pages.ABOUT_SMELT_BOX, Pages.ABOUT_HEARTH);
		addToMap(ESInitInstance.BLOCKS.SMELT_BOX_IRON, Pages.ABOUT_SMELT_BOX, Pages.ABOUT_HEARTH,
				Pages.ABOUT_MAGICAL_PIECE);
		addToMap(ESInitInstance.BLOCKS.SMELT_BOX_KYNAITE, Pages.ABOUT_SMELT_BOX, Pages.ABOUT_HEARTH,
				Pages.ABOUT_MAGICAL_PIECE);
		// 蓝晶石
		addToMap(ESInitInstance.ITEMS.KYNAITE, Pages.ABOUT_KYNAITE, Pages.ABOUT_KYNATIE_TOOLS, Pages.ABOUT_INFUSION);
		// 魔力碎片
		addToMap(ESInitInstance.ITEMS.MAGICAL_PIECE, Pages.ABOUT_MAGICAL_ENDEREYE, Pages.ABOUT_MAGICAL_PIECE,
				Pages.ABOUT_INFUSION);
		// 吸收箱
		addToMap(ESInitInstance.BLOCKS.ABSORB_BOX, Pages.ABOUT_ABSORB_BOX, Pages.ABOUT_MAGIC_PL);
		// 附魔台
		addToMap(Blocks.ENCHANTING_TABLE, Pages.ABOUT_ENCHANTINGBOOK, Pages.ABOUT_KYNATIE_TOOLS);
		// 末影之眼
		addToMap(Items.ENDER_EYE, Pages.ABOUT_MAGICAL_ENDEREYE);
		// 魔力水晶
		addToMap(ESInitInstance.ITEMS.MAGIC_CRYSTAL, Pages.ABOUT_MAGIC_CRY, Pages.ABOUT_INFUSION,
				Pages.ABOUT_MAGIC_ESTONE);
		// 咒术水晶
		addToMap(ESInitInstance.ITEMS.SPELL_CRYSTAL, Pages.ABOUT_SPELL_CRY, Pages.ABOUT_INFUSION,
				Pages.ABOUT_MAGIC_PAPER);
		// 元素水晶
		addToMap(ESInitInstance.ITEMS.ELEMENT_CRYSTAL, Pages.ABOUT_ELEMENT_CRY, Pages.ABOUT_INFUSION,
				Pages.ABOUT_DEC_BOX, Pages.ABOUT_EWORKBENCH, Pages.ABOUT_ELEMENT_CUBE);
		// 魔力纸
		addToMap(ESInitInstance.ITEMS.MAGIC_PAPER, Pages.ABOUT_MAGIC_PAPER, Pages.ABOUT_SPELL_PAPER);
		addToMap(ESInitInstance.ITEMS.SPELL_PAPER, Pages.ABOUT_MAGIC_PAPER, Pages.ABOUT_SPELL_PAPER,
				Pages.ABOUT_BOOKCOVER);
		// 魔法书
		addToMap(ESInitInstance.ITEMS.SPELLBOOK_COVER, Pages.ABOUT_BOOKCOVER, Pages.ABOUT_SPELLBOOK);
		addToMap(ESInitInstance.ITEMS.SPELLBOOK, Pages.ABOUT_SPELLBOOK, Pages.ABOUT_BOOKCOVER, Pages.ABOUT_SPELEMENT);
	}

	private static void addToMap(Block block, int... ids) {
		itemToIds.put(block.getRegistryName(), ids);
	}

	private static void addToMap(Item item, int... ids) {
		itemToIds.put(item.getRegistryName(), ids);
	}

	// 如果是有stack的羊皮卷，则有很小的概率给出下一张
	static private int[] pageAware0(ItemStack stack) {
		if (stack.getItem() == ESInitInstance.ITEMS.PARCHMENT) {
			int id = Pages.getPageId(stack);
			if (id > 0 && Math.random() < 0.05) {
				if (id >= Pages.getCount())
					return null;
				return new int[] { id };
			}
		}
		return null;
	}

	// 如果stack是某种物品，则产生相关的id
	static private int[] pageAware1(ItemStack stack) {
		Item item = stack.getItem();
		Block block = Block.getBlockFromItem(item);
		if (block != Blocks.AIR) {
			if (block == ESInitInstance.BLOCKS.INFUSION_BOX) {
				if (Math.random() < 0.2)
					return RandomHelper.randomSelect(Pages.ABOUT_ELEMENT_CRY, Pages.ABOUT_SPELL_CRY);
				return RandomHelper.randomSelect(Pages.ABOUT_INFUSION, Pages.ABOUT_MAGICAL_PIECE,
						Pages.ABOUT_MAGIC_CRY);
			}
		} else {
			if (item instanceof ItemKynaiteTools.toolsCapability) {
				return RandomHelper.randomSelect(Pages.ABOUT_KYNATIE_TOOLS, Pages.ABOUT_ELEMENT,
						Pages.ABOUT_ABSORB_BOX);
			} else if (item == ESInitInstance.ITEMS.PARCHMENT) {
				if (stack.getCount() >= 8) {
					return new int[] { Pages.ABOUT_MANUAL };
				}
			}
		}
		return null;
	}

	// 如果id具有某种元素，则产生相关的id
	static private int[] pageAware2(ItemStack stack) {
		ElementStack[] stacks = ElementMap.instance.toElement(stack);
		if (stacks != null && stacks.length > 0) {
			ElementStack estack = stacks[0];
			// 如果有earth元素
			if (estack.getElement() == ESInitInstance.ELEMENTS.EARTH) {
				// 能量大于10
				if (estack.getPower() >= 10) {
					if (Math.random() < 0.75f) {
						return new int[] { Pages.ABOUT_HEARTH, Pages.ABOUT_SMELT_BOX };
					} else {
						return new int[] { Pages.ABOUT_HEARTH };
					}
				}
			}
		}
		return null;
	}

}
