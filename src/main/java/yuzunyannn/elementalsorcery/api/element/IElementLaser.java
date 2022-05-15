package yuzunyannn.elementalsorcery.api.element;

import javax.annotation.Nonnull;

import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.VariableSet;

public interface IElementLaser {

	/**
	 * 强效预先储存的元素数量
	 * 
	 */
	ElementStack onLaserPotentPreStorage(World world);

	/**
	 * 激光更新
	 * 
	 * @return 本次是否进行一次消耗的数值
	 */
	@Nonnull
	ElementStack onLaserUpdate(World world, IWorldObject caster, WorldTarget target, ElementStack eStack,
			VariableSet content);

	/***
	 * 激光在消耗成功后对调用该函数 ,如果消耗返回的为EMPTY也对调用
	 */
	void onLaserExecute(World world, IWorldObject caster, WorldTarget target, ElementStack lastCost,
			VariableSet content);
}
