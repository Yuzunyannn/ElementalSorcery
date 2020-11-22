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
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.ItemKyaniteTools;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.item.ItemScroll;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

@Deprecated
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
		this.invItem.deserializeNBT(compound.getCompoundTag("inv_item"));
		this.invPaper.deserializeNBT(compound.getCompoundTag("inv_paper"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setByte("face", (byte) face.ordinal());
		compound.setTag("inv_item", this.invItem.serializeNBT());
		compound.setTag("inv_paper", this.invPaper.serializeNBT());
		return super.writeToNBT(compound);
	}

	protected ItemStackHandler invItem = new ItemStackHandler(1);
	protected ItemStackHandler invPaper = new ItemStackHandler(1) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			// 如果物品不是羊皮纸
			if (stack.getItem() != ESInit.ITEMS.PARCHMENT) return stack;
			// 有内容的东西不能放入
			if (Pages.isVaild(stack)) return stack;
			return super.insertItem(slot, stack, simulate);
		}
	};

	// 拥有能力
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) { return true; }
		return super.hasCapability(capability, facing);
	}

	// 获取能力
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			if (facing == face.getOpposite()) return (T) invItem;
			return (T) invPaper;
		}
		return super.getCapability(capability, facing);
	}

	// 是否正在工作
	public boolean isRunning() {
		ItemStack paper = invPaper.extractItem(0, 1, true);
		if (paper.isEmpty()) return false;
		ItemStack item = invItem.extractItem(0, 1, true);
		if (item.isEmpty()) return false;
		return canRunning();
	}

	private boolean check(BlockPos pos) {
		IBlockState state = this.world.getBlockState(pos);
		if (state.getBlock() instanceof BlockFlower) return true;
		TileEntity tile = this.world.getTileEntity(pos);
		if (tile instanceof TileEntityFlowerPot) {
			Block flower = Block.getBlockFromItem(((TileEntityFlowerPot) tile).getFlowerPotItem());
			if (flower instanceof BlockFlower) return true;
		}
		return false;
	}

	// 检测周围的运行条件
	public boolean canRunning() {
		EnumFacing face = this.face.rotateY();
		return check(pos.offset(face)) && check(pos.offset(face.getOpposite()));
	}

	// 工作一次
	public void doOnce() {
		if (!this.isRunning()) return;
		if (this.world.isRemote) return;
		ItemStack item = invItem.extractItem(0, 64, true);
		String[] idArray = TileStela.pageAwareFromItem(item);
		if (idArray.length == 0) { return; }
		ItemStack paper = invPaper.extractItem(0, idArray.length, false);
		if (idArray.length == 1) {
			this.produce(ItemParchment.getParchment(idArray[0]));
			this.updateToClient();
			return;
		}
		int size = Math.min(paper.getCount(), idArray.length);
		String[] ids = new String[size];
		for (int i = 0; i < size; i++) ids[i] = idArray[i];
		this.produce(ItemScroll.getScroll(ids));
		this.updateToClient();
	}

	// 产生物品
	public void produce(ItemStack stack) {
		stack = BlockHelper.insertInto(world, this.pos.offset(this.face.getOpposite()), face, stack);
		if (!stack.isEmpty()) {
			stack = BlockHelper.insertInto(world, this.pos.offset(EnumFacing.DOWN), EnumFacing.UP, stack);
			if (stack.isEmpty()) return;
			Block.spawnAsEntity(this.world, this.pos, stack);
		}
	}

	public static Map<ResourceLocation, String[]> itemToIds = new HashMap<ResourceLocation, String[]>();

	/** 从物品中获得page的id */
	static public String[] pageAwareFromItem(ItemStack stack) {
		String[] ids = null;
		ResourceLocation rname = stack.getItem().getRegistryName();
		if (itemToIds.containsKey(rname)) return RandomHelper.randomSelect(itemToIds.get(rname));
		ids = TileStela.pageAware1(stack);
		if (ids != null) return ids;
		ids = TileStela.pageAware2(stack);
		if (ids != null) return ids;
		return new String[0];
	}

	/** 初始化 */
	static public void init() {
		// 蓝晶石矿
		addToMap(ESInit.BLOCKS.KYANITE_ORE, Pages.ABOUT_KYANITE);
		// 蓝晶石方块
		addToMap(ESInit.BLOCKS.KYANITE_BLOCK, Pages.ABOUT_KYANITE);
		// 烧炼箱
		addToMap(ESInit.BLOCKS.SMELT_BOX, Pages.ABOUT_SMELT_BOX, Pages.ABOUT_HEARTH);
		addToMap(ESInit.BLOCKS.SMELT_BOX_IRON, Pages.ABOUT_SMELT_BOX, Pages.ABOUT_HEARTH,
				Pages.ABOUT_MAGIC_PIECE);
		addToMap(ESInit.BLOCKS.SMELT_BOX_KYANITE, Pages.ABOUT_SMELT_BOX, Pages.ABOUT_HEARTH,
				Pages.ABOUT_MAGIC_PIECE, Pages.ABOUT_KYANITE);
		// 蓝晶石
		addToMap(ESInit.ITEMS.KYANITE, Pages.ABOUT_KYANITE, Pages.ABOUT_KYNATIE_TOOLS);
		// 魔力碎片
		addToMap(ESInit.ITEMS.MAGIC_PIECE, Pages.ABOUT_MAGICAL_ENDEREYE, Pages.ABOUT_MAGIC_PIECE);
		// 末影之眼
		addToMap(Items.ENDER_EYE, Pages.ABOUT_MAGICAL_ENDEREYE);
		// 带有魔力的末影之眼
		addToMap(ESInit.ITEMS.MAGICAL_ENDER_EYE, Pages.ABOUT_MAGICAL_ENDEREYE, Pages.ABOUT_MAGIC_PIECE);
		// 星石星沙
		addToMap(ESInit.BLOCKS.STAR_STONE, Pages.ABOUT_STAR_SAND, Pages.ABOUT_STONE_MILL);
		addToMap(ESInit.BLOCKS.STAR_SAND, Pages.ABOUT_STAR_SAND, Pages.ABOUT_STONE_MILL,
				Pages.ABOUT_MAGIC_STONE, Pages.ABOUT_MELT_CAULDRON);
		// 魔石
		addToMap(ESInit.ITEMS.MAGIC_STONE, Pages.ABOUT_MAGIC_STONE, Pages.ABOUT_STAR_SAND,
				Pages.ABOUT_MAGIC_PIECE, Pages.ABOUT_ASTONE);
		// 熔岩炉
		addToMap(ESInit.BLOCKS.MELT_CAULDRON, Pages.ABOUT_MELT_CAULDRON, Pages.ABOUT_ASTONE);
		// 通魔石
		addToMap(ESInit.BLOCKS.ASTONE, Pages.ABOUT_MELT_CAULDRON, Pages.ABOUT_ASTONE, Pages.ABOUT_MD);
		// 魔石火把
		addToMap(ESInit.BLOCKS.MAGIC_TORCH, Pages.ABOUT_MD, Pages.ABOUT_MAGIC_STONE);
		// 魔分箱
		addToMap(ESInit.BLOCKS.MD_MAGIC_GEN, Pages.ABOUT_ASTONE, Pages.ABOUT_MD, Pages.ABOUT_INFUSION);
		// 吸收箱
		//addToMap(ESInitInstance.BLOCKS.ABSORB_BOX, Pages.ABOUT_ABSORB_BOX, Pages.ABOUT_MAGIC_PLATFORM);
		// 魔力平台
		addToMap(ESInit.BLOCKS.MAGIC_PLATFORM, Pages.ABOUT_MAGIC_PLATFORM);
		// 附魔台
		addToMap(Blocks.ENCHANTING_TABLE, Pages.ABOUT_ENCHANTINGBOOK, Pages.ABOUT_KYNATIE_TOOLS);
		// 魔力水晶
		addToMap(ESInit.ITEMS.MAGIC_CRYSTAL, Pages.ABOUT_MAGIC_CRYSTAL, Pages.ABOUT_INFUSION,
				Pages.ABOUT_MAGIC_ESTONE);
		// 咒术水晶
		addToMap(ESInit.ITEMS.SPELL_CRYSTAL, Pages.ABOUT_SPELL_CRYSTAL, Pages.ABOUT_INFUSION,
				Pages.ABOUT_MAGIC_PAPER);
		// 元素水晶
		addToMap(ESInit.ITEMS.ELEMENT_CRYSTAL, Pages.ABOUT_ELEMENT_CRY, Pages.ABOUT_INFUSION,
				Pages.ABOUT_DEC_BOX, Pages.ABOUT_EWORKBENCH, Pages.ABOUT_ELEMENT_CUBE);
		// 魔力纸
		addToMap(ESInit.ITEMS.MAGIC_PAPER, Pages.ABOUT_MAGIC_PAPER, Pages.ABOUT_SPELL_PAPER);
		addToMap(ESInit.ITEMS.SPELL_PAPER, Pages.ABOUT_MAGIC_PAPER, Pages.ABOUT_SPELL_PAPER,
				Pages.ABOUT_BOOKCOVER);
		// 魔法书
		addToMap(ESInit.ITEMS.SPELLBOOK_COVER, Pages.ABOUT_BOOKCOVER, Pages.ABOUT_SPELLBOOK);
		addToMap(ESInit.ITEMS.SPELLBOOK, Pages.ABOUT_SPELLBOOK, Pages.ABOUT_BOOKCOVER, Pages.ABOUT_SPELEMENT);
	}

	private static void addToMap(Block block, String... ids) {
		itemToIds.put(block.getRegistryName(), ids);
	}

	private static void addToMap(Item item, String... ids) {
		itemToIds.put(item.getRegistryName(), ids);
	}

	// 如果stack是某种物品，则产生相关的id
	static private String[] pageAware1(ItemStack stack) {
		Item item = stack.getItem();
		Block block = Block.getBlockFromItem(item);
		if (block != Blocks.AIR) {
			if (block == ESInit.BLOCKS.MD_INFUSION) {
				if (Math.random() < 0.2)
					return RandomHelper.randomSelect(Pages.ABOUT_ELEMENT_CRY, Pages.ABOUT_SPELL_CRYSTAL);
				return RandomHelper.randomSelect(Pages.ABOUT_INFUSION, Pages.ABOUT_MAGIC_PIECE, Pages.ABOUT_MAGIC_CRYSTAL);
			}
		} else {
			if (item instanceof ItemKyaniteTools.toolsCapability) {
				return RandomHelper.randomSelect(Pages.ABOUT_KYNATIE_TOOLS, Pages.ABOUT_ELEMENT,
						Pages.ABOUT_ABSORB_BOX);
			} else if (item == ESInit.ITEMS.PARCHMENT) {
				if (stack.getCount() >= 8) { return new String[] { Pages.ABOUT_MANUAL }; }
			}
		}
		return null;
	}

	// 如果id具有某种元素，则产生相关的id
	static private String[] pageAware2(ItemStack stack) {
		ElementStack[] stacks = ElementMap.instance.toElement(stack);
		if (stacks != null && stacks.length > 0) {
			ElementStack estack = stacks[0];
			// 如果有earth元素
			if (estack.getElement() == ESInit.ELEMENTS.EARTH) {
				// 能量大于10
				if (estack.getPower() >= 10) {
					if (Math.random() < 0.75f) {
						return new String[] { Pages.ABOUT_HEARTH, Pages.ABOUT_SMELT_BOX, Pages.ABOUT_STONE_MILL };
					}
				}
			}
		}
		return null;
	}

}
