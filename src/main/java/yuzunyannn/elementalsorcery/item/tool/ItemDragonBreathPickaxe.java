package yuzunyannn.elementalsorcery.item.tool;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.helper.OreHelper;
import yuzunyannn.elementalsorcery.util.helper.OreHelper.OreEnum;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemDragonBreathPickaxe extends ItemPickaxe {

	public ItemDragonBreathPickaxe() {
		super(ToolMaterial.DIAMOND);
		this.setTranslationKey("dragonBreathPickaxe");
		this.setMaxDamage(64 * 4);
		this.attackDamage = ToolMaterial.DIAMOND.getAttackDamage() * 2;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (enchantment == Enchantments.SILK_TOUCH) return false;
		if (enchantment == Enchantments.UNBREAKING) return false;
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public int getItemEnchantability(ItemStack stack) {
		return super.getItemEnchantability(stack) * 2;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		if (OreHelper.isOre(state)) return super.getDestroySpeed(stack, state) * 4;
		return super.getDestroySpeed(stack, state);
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return false;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos,
			EntityLivingBase entityLiving) {
		if (!worldIn.isRemote) {
			if (OreHelper.isOre(state)) {
				super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
				if (worldIn.rand.nextInt(100) < 75) moreDrop(worldIn, state, pos);
			} else if (worldIn.rand.nextInt(100) < 8) super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
		}
		return true;
	}

	public void moreDrop(World worldIn, IBlockState state, BlockPos pos) {
		OreEnum ore = OreHelper.getOreInfo(state);
		if (ore == null) return;
		ItemStack stack = ore.createOreProduct(Math.abs(worldIn.rand.nextInt()));
		if (stack.isEmpty()) return;
		ItemHelper.dropItem(worldIn, pos, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.YELLOW + I18n.format("item.dragonBreathPickaxe.describe"));
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
}
