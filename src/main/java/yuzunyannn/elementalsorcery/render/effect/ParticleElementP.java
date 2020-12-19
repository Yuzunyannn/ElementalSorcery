package yuzunyannn.elementalsorcery.render.effect;

import net.minecraft.client.particle.ParticleExplosion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
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