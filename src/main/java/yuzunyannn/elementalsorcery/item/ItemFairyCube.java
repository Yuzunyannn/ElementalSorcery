package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.entity.fcube.EntityFairyCube;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemFairyCube extends Item {

	public ItemFairyCube() {
		this.setUnlocalizedName("fairyCube");
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {

		pos = pos.offset(facing);
		if (!world.isAirBlock(pos)) return EnumActionResult.FAIL;
		if (!world.isAirBlock(pos.up())) return EnumActionResult.FAIL;

		AxisAlignedBB aabb = WorldHelper.createAABB(pos, 8, 8, 8);
		List<EntityFairyCube> entities = world.getEntitiesWithinAABB(EntityFairyCube.class, aabb, entity -> {
			return entity.isMaster(player);
		});

		if (!entities.isEmpty()) return EnumActionResult.FAIL;

		if (world.isRemote) return EnumActionResult.SUCCESS;

		ItemStack stack = player.getHeldItem(hand);

		NBTTagCompound nbt = stack.getSubCompound("fairyCube");
		EntityFairyCube fairyCube = new EntityFairyCube(player, nbt);
		fairyCube.setPosition(pos.getX(), pos.getY() + 0.5, pos.getZ());
		world.spawnEntity(fairyCube);
		stack.shrink(1);

//		if (ElementalSorcery.isDevelop) {
//			fairyCube.installModule(TextHelper.toESResourceLocation("destory_block"), null);
//			fairyCube.installModule(TextHelper.toESResourceLocation("attr_silk"), null);
//			fairyCube.installModule(TextHelper.toESResourceLocation("fortune"), null);
//			fairyCube.installModule(TextHelper.toESResourceLocation("place_block"), null);
//			fairyCube.installModule(TextHelper.toESResourceLocation("heal"), null);
//			fairyCube.installModule(TextHelper.toESResourceLocation("lightweight"), null);
//			fairyCube.installModule(TextHelper.toESResourceLocation("attack"), null);
//			fairyCube.installModule(TextHelper.toESResourceLocation("plunder"), null);
//			fairyCube.installModule(TextHelper.toESResourceLocation("attack_range"), null);
//			fairyCube.installModule(TextHelper.toESResourceLocation("attack_critical"), null);
//			fairyCube.installModule(TextHelper.toESResourceLocation("exp_up"), null);
//		}

		return EnumActionResult.SUCCESS;
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = stack.getSubCompound("fairyCube");
		if (nbt == null) return;

		int level = (int) nbt.getFloat("level") + 1;
		tooltip.add(TextFormatting.GOLD + I18n.format("info.level", level));

		NBTTagList list = nbt.getTagList("modules", NBTTag.TAG_COMPOUND);
		int count = list.tagCount();
		tooltip.add(TextFormatting.DARK_GREEN + I18n.format("info.fairy.cube.module.count", count));

	}

	public static ItemStack createStackWithEntity(EntityFairyCube fairyCube) {
		ItemStack stack = new ItemStack(ESInit.ITEMS.FAIRY_CUBE);
		NBTTagCompound nbt = stack.getOrCreateSubCompound("fairyCube");
		fairyCube.writeFairyCubeToNBT(nbt);
		return stack;
	}
}
