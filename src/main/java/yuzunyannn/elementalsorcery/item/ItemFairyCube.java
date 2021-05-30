package yuzunyannn.elementalsorcery.item;

import java.util.List;

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
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.entity.fcube.EntityFairyCube;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.text.TextHelper;
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

		NBTTagCompound nbt = stack.getOrCreateSubCompound("fairyCube");
		EntityFairyCube fairyCube = new EntityFairyCube(player, nbt);
		fairyCube.setPosition(pos.getX(), pos.getY() + 0.5, pos.getZ());
		world.spawnEntity(fairyCube);
		stack.shrink(1);

		if (ElementalSorcery.isDevelop) {
			fairyCube.installModule(TextHelper.toESResourceLocation("destory_block"), null);
		}

		return EnumActionResult.SUCCESS;
	}

	public static ItemStack createStackWithEntity(EntityFairyCube fairyCube) {
		ItemStack stack = new ItemStack(ESInit.ITEMS.FAIRY_CUBE);
		NBTTagCompound nbt = stack.getOrCreateSubCompound("fairyCube");
		fairyCube.writeFairyCubeToNBT(nbt);
		return stack;
	}
}
