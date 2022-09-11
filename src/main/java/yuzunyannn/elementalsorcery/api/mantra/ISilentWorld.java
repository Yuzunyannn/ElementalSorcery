package yuzunyannn.elementalsorcery.api.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface ISilentWorld {

	public boolean isSilent(Entity entity, SilentLevel level);

	public boolean isSilent(World world, Vec3d pos, SilentLevel level);

}
