package yuzunyan.elementalsorcery.tile;

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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.block.BlockSmeltBox;
import yuzunyan.elementalsorcery.element.ElementMap;
import yuzunyan.elementalsorcery.init.ESInitInstance;
import yuzunyan.elementalsorcery.item.ItemKynaiteTools;
import yuzunyan.elementalsorcery.item.ItemParchment;
import yuzunyan.elementalsorcery.item.ItemScroll;
import yuzunyan.elementalsorcery.parchment.Pages;
import yuzunyan.elementalsorcery.util.RandomHelper;

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

	/** 从物品中获得page的id */
	static public int[] pageAwareFromItem(ItemStack stack) {
		int[] ids = null;
		ids = TileStela.pageAware0(stack);
		if (ids != null)
			return ids;
		ids = TileStela.pageAware1(stack);
		if (ids != null)
			return ids;
		ids = TileStela.pageAware2(stack);
		if (ids != null)
			return ids;
		return new int[0];
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
			if (block == ESInitInstance.BLOCKS.KYNAITE_ORE || block == ESInitInstance.BLOCKS.KYNAITE_BLOCK) {
				return new int[] { Pages.ABOUT_KYNAITE };
			} else if (block instanceof BlockSmeltBox) {
				return RandomHelper.randomSelect(Pages.ABOUT_SMELT_BOX, Pages.ABOUT_HEARTH, Pages.ABOUT_MAGICAL_PIECE);
			} else if (block == ESInitInstance.BLOCKS.INFUSION_BOX) {
				if (Math.random() < 0.2)
					return RandomHelper.randomSelect(Pages.ABOUT_ELEMENT_CRY, Pages.ABOUT_SPELL_CRY);
				return RandomHelper.randomSelect(Pages.ABOUT_INFUSION, Pages.ABOUT_MAGICAL_PIECE,
						Pages.ABOUT_MAGIC_CRY);
			} else if (block == Blocks.ENCHANTING_TABLE) {
				return RandomHelper.randomSelect(Pages.ABOUT_ENCHANTINGBOOK, Pages.ABOUT_KYNATIE_TOOLS);
			}
		} else {
			if (item == ESInitInstance.ITEMS.KYNAITE) {
				return RandomHelper.randomSelect(Pages.ABOUT_KYNAITE, Pages.ABOUT_KYNATIE_TOOLS, Pages.ABOUT_INFUSION);
			} else if (item instanceof ItemKynaiteTools.toolsCapability) {
				return RandomHelper.randomSelect(Pages.ABOUT_KYNATIE_TOOLS, Pages.ABOUT_ELEMENT,
						Pages.ABOUT_ABSORB_BOX);
			} else if (item == ESInitInstance.ITEMS.MAGICAL_PIECE) {
				return RandomHelper.randomSelect(Pages.ABOUT_MAGICAL_ENDEREYE, Pages.ABOUT_MAGICAL_PIECE,
						Pages.ABOUT_INFUSION);
			} else if (item == Items.ENDER_EYE) {
				return RandomHelper.randomSelect(Pages.ABOUT_MAGICAL_ENDEREYE);
			} else if (item == ESInitInstance.ITEMS.MAGIC_CRYSTAL) {
				return RandomHelper.randomSelect(Pages.ABOUT_MAGIC_CRY, Pages.ABOUT_INFUSION, Pages.ABOUT_MAGIC_ESTONE);
			} else if (item == ESInitInstance.ITEMS.SPELL_CRYSTAL) {
				return RandomHelper.randomSelect(Pages.ABOUT_SPELL_CRY, Pages.ABOUT_INFUSION, Pages.ABOUT_MAGIC_PAPER);
			} else if (item == ESInitInstance.ITEMS.ELEMENT_CRYSTAL) {
				return RandomHelper.randomSelect(Pages.ABOUT_ELEMENT_CRY, Pages.ABOUT_INFUSION, Pages.ABOUT_DEC_BOX,
						Pages.ABOUT_EWORKBENCH, Pages.ABOUT_ELEMENT_CUBE);
			} else if (item == ESInitInstance.ITEMS.MAGIC_PAPER) {
				return RandomHelper.randomSelect(Pages.ABOUT_MAGIC_PAPER, Pages.ABOUT_SPELL_PAPER);
			} else if (item == ESInitInstance.ITEMS.SPELL_PAPER) {
				return RandomHelper.randomSelect(Pages.ABOUT_MAGIC_PAPER, Pages.ABOUT_SPELL_PAPER,
						Pages.ABOUT_BOOKCOVER);
			} else if (item == ESInitInstance.ITEMS.SPELLBOOK_COVER) {
				return RandomHelper.randomSelect(Pages.ABOUT_BOOKCOVER, Pages.ABOUT_SPELLBOOK);
			} else if (item == ESInitInstance.ITEMS.SPELLBOOK) {
				return RandomHelper.randomSelect(Pages.ABOUT_SPELLBOOK, Pages.ABOUT_BOOKCOVER, Pages.ABOUT_SPELEMENT);
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
