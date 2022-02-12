package yuzunyannn.elementalsorcery.render.effect.batch;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class EffectElement extends EffectFacing {

	public final static EffectBatchTypeNormal BATCH_TYPE = new EffectBatchTypeNormal(
			"textures/effect/element_flare.png");
	public final static EffectBatchTypeNormal BATCH_TYPE_GLOW = new EffectBatchTypeGlow(
			"textures/effect/element_flare.png");

	public float dalpha;

	public EffectElement(World worldIn, double posXIn, double posYIn, double posZIn) {
		super(worldIn, posXIn, posYIn, posZIn);
		this.asParticle = true;
		this.dalpha = 1.0f / this.lifeTime;
		this.prevAlpha = this.alpha = 1.0f;
		this.prevScale = this.scale = rand.nextFloat() * 0.2f + 0.1f;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.alpha -= this.dalpha;
	}

	@Override
	protected EffectBatchTypeNormal typeBatch() {
		return isGlow ? BATCH_TYPE_GLOW : BATCH_TYPE;
	}

	@Override
	protected TextureBinder getTexture() {
		return typeBatch().TEXTURE;
	}

	public void setVelocity(Vec3d v) {
		motionX = v.x;
		motionY = v.y;
		motionZ = v.z;
	}

	public void setVelocity(double x, double y, double z) {
		motionX = x;
		motionY = y;
		motionZ = z;
	}

	@Override
	protected boolean canPassSpawn() {
		if (super.canPassSpawn()) return true;
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player == null) return true;
		float len = Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16 * 0.75f;
		if (player.getDistanceSq(posX, posY, posZ) >= len * len) return true;
		return false;
	}
}
