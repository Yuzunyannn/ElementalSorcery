package yuzunyannn.elementalsorcery.item;

import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfessionMerchant;
import yuzunyannn.elementalsorcery.elf.pro.merchant.ElfMerchantType;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfTravelling;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemMerchantInvitation extends Item {

	public ItemMerchantInvitation() {
		this.setTranslationKey("merchantInvitation");
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			items.add(new ItemStack(this, 1, 0));
		}
	}

	public static ItemStack createInvitationWithMerchantType(ElfMerchantType mType) {
		ItemStack stack = new ItemStack(ESObjects.ITEMS.MERCHANT_INVITATION);
		NBTTagCompound nbt = new NBTTagCompound();
		stack.setTagCompound(nbt);
		nbt.setString("inviteHim", mType.getRegistryName());
		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return;
	}

	public void tryGenMerchant(ItemStack stack, World world, BlockPos pos, Random random) {
		AxisAlignedBB aabb = WorldHelper.createAABB(pos, 12, 4, 4);
		List<EntityElfBase> merchants = WorldHelper.getElfWithAABB(world, aabb, ElfProfession.MERCHANT);
		if (merchants.size() >= 3) return;

		BlockPos spawnPos = WorldHelper.tryFindPlaceToSpawn(world, random, pos, 4);
		if (spawnPos == null) return;
		IBlockState spawState = world.getBlockState(spawnPos);

		ElfMerchantType mType = null;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) {
			mType = ElfMerchantType.getMerchantType(nbt.getString("inviteHim"));
			if (random.nextDouble() > 0.25) mType = null;
		}

		EntityElfBase elf = mType == null ? new EntityElfTravelling(world) : new EntityElfTravelling(world, mType);
		elf.setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5);
		if (!spawState.canEntitySpawn(elf)) return;

		ElfProfessionMerchant.setRemainTimeBeforeLeave(elf, (int) (20 * 60 * (2 + random.nextFloat() * 8)));
		world.spawnEntity(elf);
	}
}
