package yuzunyannn.elementalsorcery.item.tool;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.event.CommandESDebug;
import yuzunyannn.elementalsorcery.render.RenderRulerSelectRegion;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class ItemMagicRuler extends Item {

	public ItemMagicRuler() {
		this.setTranslationKey("magicRuler");
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		Integer dimensionId = ItemMagicRuler.getDimensionId(stack);
		if (dimensionId == null || dimensionId != player.dimension) {
			ItemMagicRuler.setDimensionId(stack, player.dimension);
			ItemMagicRuler.clearRulerPos(stack);
		}
		if (!player.isSneaking()) {
			BlockPos pos2 = ItemMagicRuler.getRulerPos(stack, false);
			if (this.checkFailToSelect(player, worldIn, pos, pos2)) return EnumActionResult.FAIL;
		} else {
			BlockPos pos1 = ItemMagicRuler.getRulerPos(stack, true);
			if (this.checkFailToSelect(player, worldIn, pos1, pos)) return EnumActionResult.FAIL;
		}
		// 没有检测出错误，设置位置
		ItemMagicRuler.setRulerPos(stack, pos, !player.isSneaking());
		return EnumActionResult.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (playerIn.isSneaking()) {
			ItemMagicRuler.clearRulerPos(playerIn.getHeldItem(handIn));
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		}
		if (ESAPI.isDevelop) next: {
			ItemStack stack = playerIn.getHeldItem(handIn);
			BlockPos pos1 = ItemMagicRuler.getRulerPos(stack, true);
			BlockPos pos2 = ItemMagicRuler.getRulerPos(stack, false);
			if (pos1 == null || pos2 == null) break next;
			try {
				CommandESDebug.doSomethingInRange(worldIn, playerIn, pos1, pos2);
			} catch (Exception e) {
				ESAPI.logger.warn("咋回事?", e);
			}

		}

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	/** 检测选择的距离是否失败 */
	private boolean checkFailToSelect(EntityPlayer player, World worldIn, BlockPos pos1, BlockPos pos2) {
		if (pos1 == null || pos2 == null) return false;
		if (pos1.distanceSq(pos2) > MAX_DIS_SQ) {
			if (worldIn.isRemote) player.sendMessage(new TextComponentTranslation("info.ruler.fail"));
			return true;
		}
		return false;
	}

	public static final int MAX_DIS_SQ = 128 * 128;

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote) return;
		if (entityIn instanceof EntityPlayer) {
			if (isSelected) {
				RenderRulerSelectRegion.showItem((EntityPlayer) entityIn, EnumHand.MAIN_HAND);
			} else if (itemSlot == 0 && stack == ((EntityPlayer) entityIn).getHeldItemOffhand()) {
				RenderRulerSelectRegion.showItem((EntityPlayer) entityIn, EnumHand.OFF_HAND);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		EnumDyeColor dyeColor = ItemMagicRuler.getColor(stack);
		tooltip.add(I18n.format("info.ruler.color", ColorHelper.toTextFormatting(dyeColor)
				+ I18n.format("item.fireworksCharge." + dyeColor.getTranslationKey())));
		Integer dimensionId = ItemMagicRuler.getDimensionId(stack);
		if (dimensionId == null) {
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.ruler.none"));
			return;
		} else if (Minecraft.getMinecraft().player.dimension != dimensionId) {
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.wrong.dimension"));
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.ruler.none"));
			return;
		}
		BlockPos pos1 = ItemMagicRuler.getRulerPos(stack, true);
		BlockPos pos2 = ItemMagicRuler.getRulerPos(stack, false);
		if (pos1 == null || pos2 == null) {
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.ruler.none"));
			return;
		}
		tooltip.add(TextFormatting.YELLOW + I18n.format("info.ruler.pos", 1, pos1.getX(), pos1.getY(), pos1.getZ()));
		tooltip.add(TextFormatting.YELLOW + I18n.format("info.ruler.pos", 2, pos2.getX(), pos2.getY(), pos2.getZ()));

	}

	/** 获取标尺世界 */
	public static Integer getDimensionId(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return null;
		if (nbt.hasKey("world")) return nbt.getInteger("world");
		else return null;
	}

	/** 设置标尺世界 */
	public static void setDimensionId(ItemStack stack, int world) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		nbt.setInteger("world", world);
	}

	/** 设置标尺的位置 */
	public static void setRulerPos(ItemStack stack, BlockPos pos, boolean first) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		if (first) NBTHelper.setBlockPos(nbt, "loc1", pos);
		else NBTHelper.setBlockPos(nbt, "loc2", pos);
	}

	/** 清空ruler */
	public static void clearRulerPos(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return;
		nbt.removeTag("loc1");
		nbt.removeTag("loc2");
	}

	/** 获取位置 */
	public static @Nullable BlockPos getRulerPos(ItemStack stack, boolean first) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return null;
		if (first) {
			if (NBTHelper.hasBlockPos(nbt, "loc1")) return NBTHelper.getBlockPos(nbt, "loc1");
			return null;
		} else {
			if (NBTHelper.hasBlockPos(nbt, "loc2")) return NBTHelper.getBlockPos(nbt, "loc2");
			return null;
		}
	}

	/** 获取颜色 */
	public static EnumDyeColor getColor(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return EnumDyeColor.PURPLE;
		if (nbt.hasKey("color")) {
			int meta = nbt.getInteger("color");
			return EnumDyeColor.byMetadata(meta);
		}
		return EnumDyeColor.PURPLE;
	}

	/** 设置颜色 */
	public static ItemStack setColor(ItemStack stack, EnumDyeColor color) {
		if (color == null) color = EnumDyeColor.PURPLE;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		nbt.setInteger("color", color.getMetadata());
		return stack;
	}
}
