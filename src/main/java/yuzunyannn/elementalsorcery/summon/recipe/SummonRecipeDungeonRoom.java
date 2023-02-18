package yuzunyannn.elementalsorcery.summon.recipe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.summon.Summon;
import yuzunyannn.elementalsorcery.summon.SummonDungeonRoom;

public class SummonRecipeDungeonRoom extends SummonRecipe {

	public static ItemStack createVestKeepsake(int areaId, int roomId) {
		ItemStack stack = new ItemStack(Items.APPLE);
		NBTTagCompound nbt = new NBTTagCompound();
		stack.setTagCompound(nbt);
		nbt.setInteger("roomId", roomId);
		nbt.setInteger("areaId", areaId);
		return stack;
	}

	@Override
	public boolean canBeKeepsake(ItemStack keepsake, World world, BlockPos pos) {
		return false;
	}

	@Override
	public Summon createSummon(ItemStack keepsake, World world, BlockPos pos) {
		NBTTagCompound nbt = keepsake.getTagCompound();
		Summon summon = new SummonDungeonRoom(world);
		if (nbt != null) summon.deserializeNBT(nbt);
		return summon;
	}
}
