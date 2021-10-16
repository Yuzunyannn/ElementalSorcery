package yuzunyannn.elementalsorcery.api.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IJuice {

	/** 是否可以开喝 */
	boolean canDrink(World world, EntityLivingBase drinker);

	/** 当喝完，添加各种效果 */
	void onDrink(World world, EntityLivingBase drinker);

	/**
	 * 盛取某些液体
	 * 
	 * @return true表示盛取成功
	 */
	boolean onContain(World world, @Nullable EntityLivingBase drinker, BlockPos pos);

	/** 获取果汁量 */
	float getJuiceCount();

	/** 获取果汁杯子的总量 */
	float getMaxJuiceCount();

	@SideOnly(Side.CLIENT)
	void beforeRenderJuice();

	@SideOnly(Side.CLIENT)
	void addJuiceInformation(World worldIn, List<String> tooltip, ITooltipFlag flagIn);

}
