package yuzunyannn.elementalsorcery.item.tool;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.crafting.IToElementItem;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.crafting.element.ToElementInfoStatic;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemKyaniteTools {

	public static final ToolMaterial KYANITE = EnumHelper.addToolMaterial("Kyanite", 2, 750, 6.0F, 2.5F, 10);
	static {
		KYANITE.setRepairItem(new ItemStack(ESObjects.ITEMS.KYANITE));
	}

	// 添加物品信息
	private static void addToolsInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		IElementInventory inventory = new ElementInventory();
		if (inventory.hasState(stack) == false) return;
		inventory.loadState(stack);
		tooltip.add("§d" + I18n.format("info.kyaniteTools.el"));
		inventory.addInformation(worldIn, tooltip, flagIn);
	}

	/// 当破坏方块
	private static void dealBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, EntityLivingBase entity) {
		IElementInventory inventory = new ElementInventory();
		if (inventory.hasState(stack) == false) return;
		ItemStack blockStack = ItemHelper.toItemStack(state);
		if (blockStack.isEmpty()) return;
		IToElementInfo teInfo = ElementMap.instance.toElement(blockStack);
		ElementStack[] estacks = teInfo == null ? null : teInfo.element();
		if (estacks != null) {
			ElementStack estack;
			if (Math.random() >= 0.333333) return;
			// 如果着火挖方块，必然会获得火元素
			if (entity.isBurning()) {
				estack = new ElementStack(ESObjects.ELEMENTS.FIRE, 3, 3);
			}
			// 如果在水里挖方块，可能会会的水元素
			else if (entity.isInWater() && Math.random() > 0.33333f) {
				estack = new ElementStack(ESObjects.ELEMENTS.WATER, 1, 3);
			} else {
				estack = estacks[0].copy();
				estack = estack.onDeconstruct(worldIn, ItemHelper.toItemStack(state), teInfo.complex(),
						Element.DP_TOOLS);
			}
			inventory.loadState(stack);
			inventory.insertElement(estack, false);
			inventory.saveState(stack);
		}
	}

	// 工具具有储存元素的能力
	public static interface toolsCapability extends IToElementItem {

		default void provide(ItemStack stack) {
			IElementInventory inventory = new ElementInventory(6);
			inventory.saveState(stack);
		}

		default IToElementInfo toElement(ItemStack stack) {
			IElementInventory inventory = new ElementInventory(6);
			if (!inventory.hasState(stack)) return null;
			inventory.loadState(stack);
			return ToElementInfoStatic.createWithElementContainer(stack.copy(), inventory);
		}
	}

	// 镐子
	public static class ItemKyanitePickaxe extends ItemPickaxe implements toolsCapability {
		public ItemKyanitePickaxe() {
			super(KYANITE);
			this.setTranslationKey("kyanitePickaxe");
		}

		@Override
		public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
				ITooltipFlag flagIn) {
			ItemKyaniteTools.addToolsInformation(stack, worldIn, tooltip, flagIn);
		}

		@Override
		public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos,
				EntityLivingBase entityLiving) {
			ItemKyaniteTools.dealBlockDestroyed(stack, worldIn, state, entityLiving);
			return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
		}
	}

	// 斧头
	public static class ItemKyaniteAxe extends ItemAxe implements toolsCapability {
		public ItemKyaniteAxe() {
			super(KYANITE, KYANITE.getAttackDamage() * 3, -3.1F);
			this.setTranslationKey("kyaniteAxe");
		}

		@Override
		public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
				ITooltipFlag flagIn) {
			ItemKyaniteTools.addToolsInformation(stack, worldIn, tooltip, flagIn);
		}

		@Override
		public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos,
				EntityLivingBase entityLiving) {
			ItemKyaniteTools.dealBlockDestroyed(stack, worldIn, state, entityLiving);
			return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
		}

	}

	// 铁锨
	public static class ItemKyaniteSpade extends ItemSpade implements toolsCapability {
		public ItemKyaniteSpade() {
			super(KYANITE);
			this.setTranslationKey("kyaniteSpade");
		}

		@Override
		public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
				ITooltipFlag flagIn) {
			ItemKyaniteTools.addToolsInformation(stack, worldIn, tooltip, flagIn);
		}

		@Override
		public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos,
				EntityLivingBase entityLiving) {
			ItemKyaniteTools.dealBlockDestroyed(stack, worldIn, state, entityLiving);
			return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
		}
	}

	// 锄头
	public static class ItemKyaniteHoe extends ItemHoe implements toolsCapability, IToElementItem {
		public ItemKyaniteHoe() {
			super(KYANITE);
			this.setTranslationKey("kyaniteHoe");
		}

		@Override
		public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
			if (this.isInCreativeTab(tab)) {
				items.add(new ItemStack(this));
				ItemStack stack = new ItemStack(this);
				IElementInventory inventory = new ElementInventory();
				inventory.insertElement(new ElementStack(ESObjects.ELEMENTS.ENDER, 128, 32), false);
				inventory.saveState(stack);
				items.add(stack);
			}
		}

		@Override
		public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos,
				EntityLivingBase entityLiving) {
			ItemKyaniteTools.dealBlockDestroyed(stack, worldIn, state, entityLiving);
			return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
		}

		@Override
		public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
				ITooltipFlag flagIn) {
			ItemKyaniteTools.addToolsInformation(stack, worldIn, tooltip, flagIn);
		}

		@Override
		public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
				EnumFacing facing, float hitX, float hitY, float hitZ) {
			ItemStack stack = player.getHeldItem(hand);
			IElementInventory inventory = new ElementInventory();
			if (inventory.hasState(stack) == false)
				return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
			// 取出来看看末影元素够不够
			inventory.loadState(stack);
			ElementStack estack = new ElementStack(ESObjects.ELEMENTS.ENDER, 10, 5);
			ElementStack find = ElementStack.EMPTY;
			for (int i = 0; i < inventory.getSlots(); i++) {
				find = inventory.getStackInSlot(i);
				if (find.arePowerfulAndMoreThan(estack)) break;
			}
			if (!find.arePowerfulAndMoreThan(estack))
				return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
			// 默认的砖块
			IBlockState state = worldIn.getBlockState(pos);
			if (state.getBlock() == Blocks.ENCHANTING_TABLE) {
				// 拆书
				find.shrink(estack.getCount());
				worldIn.setBlockState(pos, ESObjects.BLOCKS.INVALID_ENCHANTMENT_TABLE.getDefaultState());
				Block.spawnAsEntity(worldIn, pos, new ItemStack(ESObjects.ITEMS.SPELLBOOK_ENCHANTMENT, 1));
				if (!player.isCreative()) stack.damageItem(10, player);
				inventory.saveState(stack);
				if (player instanceof EntityPlayerMP)
					ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "tear:enchantBook");
				return EnumActionResult.SUCCESS;
			} else if (state.getBlock() == Blocks.END_PORTAL_FRAME && state.getValue(BlockEndPortalFrame.EYE)) {
				// 拆末影之眼
				find.shrink(estack.getCount());
				worldIn.setBlockState(pos, state.withProperty(BlockEndPortalFrame.EYE, false));
				Block.spawnAsEntity(worldIn, pos, new ItemStack(Items.ENDER_EYE, 1));
				if (!player.isCreative()) stack.damageItem(10, player);
				inventory.saveState(stack);
				return EnumActionResult.SUCCESS;
			}
			return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		}

	}

	// 剑
	public static class ItemKyaniteSword extends ItemSword implements toolsCapability {
		public ItemKyaniteSword() {
			super(KYANITE);
			this.setTranslationKey("kyaniteSword");
		}

		@Override
		public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
				ITooltipFlag flagIn) {
			ItemKyaniteTools.addToolsInformation(stack, worldIn, tooltip, flagIn);
		}

		public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			IElementInventory inventory = new ElementInventory();
			if (inventory.hasState(stack) && Math.random() < 0.333333) {
				ElementStack estack = ElementStack.EMPTY;
				if (attacker.isBurning()) estack = new ElementStack(ESObjects.ELEMENTS.FIRE, 3, 3);
				else if (attacker.isInWater() && Math.random() > 0.33333f)
					estack = new ElementStack(ESObjects.ELEMENTS.WATER, 1, 3);
				else estack = new ElementStack(ESObjects.ELEMENTS.WOOD, 1, 3);
				inventory.loadState(stack);
				inventory.insertElement(estack, false);
				inventory.saveState(stack);
			}
			return super.hitEntity(stack, target, attacker);
		};
	}
}
