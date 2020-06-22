package yuzunyannn.elementalsorcery.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import yuzunyannn.elementalsorcery.api.ability.IElementInventory;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.util.ElementHelper;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemKyaniteTools {

	public static final ToolMaterial KYANITE = EnumHelper.addToolMaterial("Kyanite", 2, 750, 6.0F, 2.5F, 10);
	static {
		KYANITE.setRepairItem(new ItemStack(ESInitInstance.ITEMS.KYANITE));
	}

	// 添加物品信息
	private static void addToolsInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		IElementInventory inventory = new ElementInventory();
		if (inventory.hasState(stack) == false) return;
		inventory.loadState(stack);
		tooltip.add("§d" + I18n.format("info.kyaniteTools.el"));
		ElementHelper.addElementInformation(inventory, worldIn, tooltip, flagIn);
	}

	/// 当破坏方块
	private static void dealBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, EntityLivingBase entity) {
		IElementInventory inventory = new ElementInventory();
		if (inventory.hasState(stack) == false) return;
		Block block = state.getBlock();
		ElementStack[] estacks = ElementMap.instance.toElement(state.getBlock());
		if (estacks != null) {
			ElementStack estack;
			if (Math.random() >= 0.333333) return;
			// 如果着火挖方块，必然会获得火元素
			if (entity.isBurning()) {
				estack = new ElementStack(ESInitInstance.ELEMENTS.FIRE, 3, 3);
			}
			// 如果在水里挖方块，可能会会的水元素
			else if (entity.isInWater() && Math.random() > 0.33333f) {
				estack = new ElementStack(ESInitInstance.ELEMENTS.WATER, 1, 3);
			} else {
				estack = estacks[0].copy();
				estack = estack.becomeElementWhenDeconstruct(worldIn, ItemHelper.toItemStack(state),
						ElementMap.instance.complex(state.getBlock()), Element.DP_TOOLS);
			}
			inventory.loadState(stack);
			inventory.insertElement(estack, false);
			inventory.saveState(stack);
		}
	}

	// 工具具有储存元素的能力
	public static interface toolsCapability {
		default void provide(ItemStack stack) {
			IElementInventory inventory = new ElementInventory(3);
			inventory.saveState(stack);
		}
	}

	// 镐子
	public static class ItemKyanitePickaxe extends ItemPickaxe implements toolsCapability {
		public ItemKyanitePickaxe() {
			super(KYANITE);
			this.setUnlocalizedName("kyanitePickaxe");
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
			this.setUnlocalizedName("kyaniteAxe");
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
			this.setUnlocalizedName("kyaniteSpade");
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
	public static class ItemKyaniteHoe extends ItemHoe implements toolsCapability {
		public ItemKyaniteHoe() {
			super(KYANITE);
			this.setUnlocalizedName("kyaniteHoe");
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
			ElementStack estack = new ElementStack(ESInitInstance.ELEMENTS.ENDER, 10, 5);
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
				worldIn.setBlockState(pos, ESInitInstance.BLOCKS.INVALID_ENCHANTMENT_TABLE.getDefaultState());
				Block.spawnAsEntity(worldIn, pos, new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_ENCHANTMENT, 1));
				if (!player.isCreative()) stack.damageItem(10, player);
				inventory.saveState(stack);
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
			this.setUnlocalizedName("kyaniteSword");
		}

		@Override
		public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
				ITooltipFlag flagIn) {
			ItemKyaniteTools.addToolsInformation(stack, worldIn, tooltip, flagIn);
		}
	}
}
