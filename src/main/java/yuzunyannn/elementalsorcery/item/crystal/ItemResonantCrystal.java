package yuzunyannn.elementalsorcery.item.crystal;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.entity.EntityResonantCrystal;

public class ItemResonantCrystal extends ItemCrystal {

	public ItemResonantCrystal() {
		super("resonantCrystal", 42.31f, 0xff7200);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		NBTTagCompound nbt = ElementalSorcery.getPlayerData(Minecraft.getMinecraft().player);
		float fre = nbt.getFloat("resFre");
		tooltip.add(TextFormatting.GOLD + I18n.format("info.crystal.percept", fre));
		tooltip.add(I18n.format("info.resonantCrystal"));
	}

	@Override
	public float probabilityOfLeftDirtClear() {
		return 0.1f;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (!playerIn.capabilities.isCreativeMode) stack.shrink(1);
		if (worldIn.isRemote)
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		EntityResonantCrystal rc = new EntityResonantCrystal(worldIn, playerIn);
		rc.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
		worldIn.spawnEntity(rc);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}
}
