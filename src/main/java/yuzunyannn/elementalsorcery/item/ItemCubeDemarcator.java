package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryLimit;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemCubeDemarcator extends Item implements TileMDRubbleRepair.IExtendRepair {

	public ItemCubeDemarcator() {
		this.setUnlocalizedName("cubeDemarcator");
		this.setMaxDamage(512);
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		int damage = stack.getItemDamage();
		if (damage >= stack.getMaxDamage()) return EnumActionResult.PASS;

		TileEntity tile = BlockHelper.getTileEntity(worldIn, pos, TileEntity.class);
		IElementInventory eInv = ElementHelper.getElementInventory(tile);
		if (eInv == null) return EnumActionResult.PASS;
		if (!(eInv instanceof ElementInventoryLimit)) return EnumActionResult.PASS;

		ElementInventoryLimit eInvLimit = (ElementInventoryLimit) eInv;
		stack.damageItem(1, player);

		NBTTagCompound nbt = ItemHelper.getOrCreateTagCompound(stack);
		int elementPowerCount = nbt.getInteger("EPC");

		if (player.isSneaking()) {
			eInvLimit.setUpperLimit(elementPowerCount);
			if (worldIn.isRemote) {
				TextComponentTranslation text = new TextComponentTranslation("info.set.common",
						new TextComponentTranslation("info.elementCube.limit.upper", eInvLimit.getUpperLimit()));
				player.sendMessage(text.setStyle(new Style().setColor(TextFormatting.YELLOW)));
			}
		} else {
			eInvLimit.setLowerLimit(elementPowerCount);
			if (worldIn.isRemote) {
				TextComponentTranslation text = new TextComponentTranslation("info.set.common",
						new TextComponentTranslation("info.elementCube.limit.lower", eInvLimit.getLowerLimit()));
				player.sendMessage(text.setStyle(new Style().setColor(TextFormatting.YELLOW)));
			}
		}
		tile.markDirty();

		return EnumActionResult.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		NBTTagCompound nbt = ItemHelper.getOrCreateTagCompound(stack);
		int elementPowerCount = nbt.getInteger("EPC");
		int change = 10;

		if (elementPowerCount >= 200) change = 50;
		else if (elementPowerCount >= 100) change = 20;

		if (player.isSneaking()) {
			nbt.setShort("EPC", (short) Math.max(0, elementPowerCount - change));
		} else {
			nbt.setShort("EPC", (short) Math.min(1000, elementPowerCount + change));
		}
		if (worldIn.isRemote) {
			player.sendMessage(new TextComponentTranslation("info.power", nbt.getInteger("EPC")));

		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = ItemHelper.getOrCreateTagCompound(stack);
		int elementPowerCount = nbt.getInteger("EPC");
		tooltip.add(TextFormatting.YELLOW + I18n.format("info.power", elementPowerCount));
	}

	@Override
	public ItemStack getRepairOutput(ItemStack input) {
		if (input.getItemDamage() == 0) return ItemStack.EMPTY;
		input = input.copy();
		input.setItemDamage(input.getItemDamage() - 1);
		return input;
	}

	@Override
	public int getRepairCost(ItemStack input) {
		return 20;
	}

}
