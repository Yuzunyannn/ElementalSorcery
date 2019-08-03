package yuzunyannn.elementalsorcery.crafting;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public interface ICraftingCommit extends INBTSerializable<NBTTagCompound> {
	/**
	 * 获取掉落和展示的物品集 （注意：这个函数会经常被调用）
	 */
	@Nullable
	List<ItemStack> getItems();

	/** 给予相关信息 */
	default public void setWorldInfo(World world, BlockPos pos, @Nullable EntityLivingBase player) {

	}

	/** 祭坛被破坏时调用 */
	default public void CraftingDisappear(World world, BlockPos pos) {

	}

}
