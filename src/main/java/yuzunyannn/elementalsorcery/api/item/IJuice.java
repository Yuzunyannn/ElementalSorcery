package yuzunyannn.elementalsorcery.api.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;

public interface IJuice {

	/** 是否可以开喝 */
	boolean canDrink(World world, ItemStack stack, EntityLivingBase drinker);

	/** 当喝完，添加各种效果 */
	void onDrink(World world, ItemStack stack, EntityLivingBase drinker, @Nullable IElementInventory eInv);

	/**
	 * 盛取某些液体
	 * 
	 * @return true表示盛取成功
	 */
	boolean onContain(World world, ItemStack stack, @Nullable EntityLivingBase drinker, BlockPos pos);

	/** 获取果汁量 */
	float getJuiceCount();

	/** 获取果汁杯子的总量 */
	float getMaxJuiceCount();

	@SideOnly(Side.CLIENT)
	void beforeRenderJuice();

	@SideOnly(Side.CLIENT)
	void addJuiceInformation(World worldIn, List<String> tooltip, ITooltipFlag flagIn);

	@Nullable
	default IElementInventory getElementInventory(ItemStack stack) {
		return null;
	}

}
