package yuzunyannn.elementalsorcery.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface IItemSmashable {

	/**
	 * 
	 * @param stack can Modify
	 * 
	 */
	public void doSmash(World world, Vec3d vec, ItemStack stack, List<ItemStack> outputs, @Nullable Entity operator);

}
