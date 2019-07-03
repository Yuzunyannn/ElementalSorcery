package yuzunyannn.elementalsorcery.render.particle;

import net.minecraft.client.particle.ParticleExplosion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleElementP extends ParticleExplosion {

	public ParticleElementP(World worldIn, Vec3d position) {
		super(worldIn, position.x, position.y, position.z, 0.0, 0.25, 0.0);
		this.particleScale = this.particleScale * 0.7f + 0.1f;
	}

	public void setColor(int color) {
		this.setRBGColorF(((color >> 16) & 0xff) / 255.0f, ((color >> 8) & 0xff) / 255.0f,
				(float) ((color >> 0) & 0xff));
	}
}
