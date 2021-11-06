package yuzunyannn.elementalsorcery.api.element;

import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.world.Juice.JuiceMaterial;

public interface IElemetJuice {

	/**
	 * 当喝果汁
	 * 
	 * @param water    喝的液体的量
	 * @param drinkMap 喝的具体内容和对应的数量
	 * 
	 * 
	 */
	public void onDrinkJuice(World world, EntityLivingBase drinker, ElementStack estack, float water,
			Map<JuiceMaterial, Float> drinkMap);

}
