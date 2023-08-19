package yuzunyannn.elementalsorcery.item.tool;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.ElementTransition;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.CapabilityProvider;
import yuzunyannn.elementalsorcery.entity.EntityFloatCarpet;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.element.MagicExchangeInventory;

public class ItemFloatCarpet extends Item {

	public ItemFloatCarpet() {
		this.setMaxStackSize(1);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new CapabilityProvider.ElementInventoryUseProvider(stack, new MagicExchangeInventory() {
			@Override
			public int getMaxSizeInSlot(int slot) {
				return -1;
			}
		});
	}

	static public double getMagicPower(ItemStack stack) {
		IElementInventory inventory = ElementHelper.getElementInventory(stack);
		ElementStack magic = inventory.getStackInSlot(0);
		return ElementTransition.toFragment(magic);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		double fragment = getMagicPower(stack);
		tooltip.add(I18n.format("info.magic.fragment") + ": " + TextHelper.toAbbreviatedNumber(fragment, 2));
		tooltip.add(I18n.format("info.dungeon.floatCarpet"));
	}

	public String getTranslationKey() {
		return "entity.FloatCarpet";
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return this.getTranslationKey();
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {

		pos = pos.offset(facing);
		if (!world.isAirBlock(pos)) return EnumActionResult.FAIL;
		if (!world.isAirBlock(pos.up())) return EnumActionResult.FAIL;

		AxisAlignedBB aabb = new AxisAlignedBB(pos);
		List<EntityFloatCarpet> entities = world.getEntitiesWithinAABB(EntityFloatCarpet.class, aabb);
		if (!entities.isEmpty()) return EnumActionResult.FAIL;

		if (world.isRemote) return EnumActionResult.SUCCESS;

		ItemStack stack = player.getHeldItem(hand);

		EntityFloatCarpet floatCrapet = new EntityFloatCarpet(world);
		floatCrapet.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		floatCrapet.rotationYaw = player.rotationYaw;
		floatCrapet.setMagicPower(getMagicPower(stack));
		world.spawnEntity(floatCrapet);

		if (player.isCreative()) return EnumActionResult.SUCCESS;
		stack.shrink(1);

		return EnumActionResult.SUCCESS;
	}

}
