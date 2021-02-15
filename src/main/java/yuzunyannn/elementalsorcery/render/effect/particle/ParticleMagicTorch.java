package yuzunyannn.elementalsorcery.render.effect.particle;

import net.minecraft.client.particle.ParticleRedstone;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleMagicTorch extends ParticleRedstone {

	public ParticleMagicTorch(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, float scale, float red,
			float green, float blue) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, scale, red, green, blue);
	}

}
