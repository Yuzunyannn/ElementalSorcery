package yuzunyannn.elementalsorcery.api.element;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface IElementExplosion {
	/**
	 * 创建一个爆炸操作
	 * 
	 * @return 返回null表示没有处理
	 */
	@Nullable
	public IExplosionExecutor newExplosion(World world, Vec3d pos, ElementStack eStack,
			@Nullable EntityLivingBase attacker);

}
