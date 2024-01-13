package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectFragmentMove;
import yuzunyannn.elementalsorcery.render.tile.RenderTileIceRockSendRecv;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockSendRecv;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockSendRecv.FaceStatus;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public class EffectLaserMagicTransfer extends EffectLaser {

	public final TileIceRockSendRecv tile;
	public final EnumFacing facing;
	public final Color magicColor = new Color();

	public EffectLaserMagicTransfer(World world, TileIceRockSendRecv sr, EnumFacing facing, Vec3d from, Vec3d to) {
		super(world, from, to);
		this.tile = sr;
		this.facing = facing;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		Vec3d to = new Vec3d(targetX, targetY, targetZ);
		Vec3d from = this.getPositionVector();
		Vec3d tar = to.subtract(from);

		if (rand.nextFloat() < 0.2f) {
			double len = tar.length();
			Vec3d randSpeed = tar.normalize();
			FaceStatus fs = tile.getFaceStatus(facing);
			EffectFragmentMove effect;
			if (fs == FaceStatus.IN) {
				randSpeed = randSpeed.scale(-0.2);
				effect = new EffectFragmentMove(world, to);
			} else {
				randSpeed = randSpeed.scale(0.2);
				effect = new EffectFragmentMove(world, from);
			}
			effect.prevScale = effect.scale = effect.defaultScale = 0.005f + rand.nextFloat() * 0.01f;
			effect.xDecay = effect.zDecay = effect.yDecay = 1;
			effect.lifeTime = (int) (len * 5);
			effect.endLifeTick = effect.lifeTime / 10;
			effect.color.setColor(magicColor);
			effect.motionX = randSpeed.x;
			effect.motionY = randSpeed.y;
			effect.motionZ = randSpeed.z;
			addEffect(effect);
		}

		// 终点
		{
			Vec3d randSpeed = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).normalize()
					.scale(0.04);
			Vec3d speed = tar.normalize().scale(-0.05).add(randSpeed);
			EffectFragmentMove effect = new EffectFragmentMove(world, to);
			effect.endLifeTick = effect.lifeTime;
			effect.prevScale = effect.scale = effect.defaultScale = rand.nextFloat() * 0.02f + 0.05f;
			effect.color.setColor(this.color);
			effect.motionX = speed.x;
			effect.motionY = speed.y;
			effect.motionZ = speed.z;
			addEffect(effect);
		}

	}

	@Override
	protected void renderHeadTail(double len, float a, float partialTicks) {
		FaceStatus fs = tile.getFaceStatus(facing);
		a = Math.min(0.85f, a / (1 - 0.35f));
		float r = EventClient.getGlobalRotateInRender(partialTicks);
		float scale = 1.5f * a;
		GlStateManager.color(1, 1, 1, a);
		GlStateManager.translate(0, 0, -0.05);
		GlStateManager.scale(scale, scale, 1);
		GlStateManager.rotate(r, 0, 0, 1);
		RenderTileIceRockSendRecv.drawWithStatus(fs, a);
		scale = 1 / scale;
		GlStateManager.scale(scale, scale, 1);
		scale = 1f * a;
		GlStateManager.translate(0, 0, 0.05);
		GlStateManager.scale(scale, scale, 1);
		GlStateManager.rotate(-r * 2, 0, 0, 1);
		RenderTileIceRockSendRecv.drawWithStatus(fs, a);
		scale = 1 / scale;
		GlStateManager.scale(scale, scale, 1);
	}
}
