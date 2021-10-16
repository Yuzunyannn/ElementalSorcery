package yuzunyannn.elementalsorcery.api.element;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;


public interface IElementExplosion {
	/**
	 * 创建一个爆炸操作
	 * 
	 * @return 如果返回{@link ElementExplosion#SELF_DEAL}则表示newExplosion进行自行处理<br/>
	 *         返回null表示没有处理
	 */
	@Nullable
	public IExplosionExecutor newExplosion(World world, Vec3d pos, ElementStack eStack,
			@Nullable EntityLivingBase attacker);

}
