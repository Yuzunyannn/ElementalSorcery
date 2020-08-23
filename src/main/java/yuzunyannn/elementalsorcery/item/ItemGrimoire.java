package yuzunyannn.elementalsorcery.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;

public class ItemGrimoire extends Item {

	public ItemGrimoire() {
		this.setUnlocalizedName("grimoire");
		this.setMaxStackSize(1);
	}

	public IElementInventory initElementInventory(ItemStack stack) {
		return new ElementInventory(4);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new Grimoire.Provider(this.initElementInventory(stack));
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BLOCK;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		// 没有能力的，可能是别的内容
		if (!stack.hasCapability(Grimoire.GRIMOIRE_CAPABILITY, null))
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		// 必须主手
		if (handIn != EnumHand.MAIN_HAND) return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		// 开始释放！
		grimoire.load(stack);
		EntityGrimoire.start(worldIn, playerIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		if (entityLiving.world.isRemote) return;
		Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		grimoire.save(stack);
	}
}
