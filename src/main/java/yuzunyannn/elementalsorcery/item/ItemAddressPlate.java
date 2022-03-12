package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.tile.TileElfBeacon;

public class ItemAddressPlate extends Item {

	public ItemAddressPlate() {
		this.setHasSubtypes(true);
		this.setTranslationKey("addressPlate");
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			items.add(new ItemStack(this, 1, 0));
			items.add(new ItemStack(this, 1, 1));
		}
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return;
		String address = nbt.getString("address");
		String signature = nbt.getString("signature");
		tooltip.add(TextFormatting.GREEN + I18n.format("info.address", address));
		if (!signature.isEmpty()) tooltip.add(TextFormatting.GOLD + I18n.format("info.signature", signature));
		if (stack.getMetadata() == 1) {
			int times = ElfPostOffice.getAddressPlateServiceCount(stack);
			tooltip.add(I18n.format("info.service.times", times));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (stack.getMetadata() == 1) {
			int times = ElfPostOffice.getAddressPlateServiceCount(stack);
			if (times <= 0 && !playerIn.isCreative()) return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
			if (worldIn.isRemote) return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			// 没地址走人
			String address = ElfPostOffice.getAddress(stack);
			if (address.isEmpty()) return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
			// 成就
			if (playerIn instanceof EntityPlayerMP)
				ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) playerIn, "post:vip");
			// 周围有精灵了,直接召唤他，不花费次数
			EntityElfBase postmain = TileElfBeacon.getPostmanAround(worldIn, playerIn.getPosition());
			if (postmain != null) {
				sendParcelForMe(postmain, address, playerIn);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
			// 没有尝试召唤
			Vec3d pos = playerIn.getPositionVector();
			pos = pos.add(playerIn.getLookVec().scale(3));
			postmain = TileElfBeacon.tryCreatePostman(worldIn, new BlockPos(pos.x, playerIn.posY, pos.z));
			if (postmain == null) return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
			if (!playerIn.isCreative()) ElfPostOffice.addAddressPlateServiceCount(stack, -1);
			sendParcelForMe(postmain, address, playerIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}

	public static void sendParcelForMe(EntityElfBase elf, String address, EntityPlayer player) {
		NBTTagCompound nbt = elf.getEntityData();
		nbt.setInteger("receiver", player.getEntityId());
		nbt.setString("address", address);
		nbt.setString("addressOwner", player.getName());
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return "item.addressPlate." + EnumType.byMetadata(stack.getMetadata()).getName();
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	static public enum EnumType implements IStringSerializable {
		NORMAL("normal"),
		VIP("vip");

		final String name;

		EnumType(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getMetadata() {
			return this.ordinal();
		}

		static public EnumType byMetadata(int meta) {
			return EnumType.values()[0x3 & meta];
		}
	}

}
