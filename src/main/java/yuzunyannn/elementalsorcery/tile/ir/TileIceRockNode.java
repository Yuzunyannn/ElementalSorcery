package yuzunyannn.elementalsorcery.tile.ir;

import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectFragmentMove;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class TileIceRockNode extends TileIceRockEnergy implements ITickable {

	@Override
	public boolean hasUpDownFace() {
		return true;
	}

	@Override
	public void update() {
		if (!isLinked()) {
			onUnLink();
			return;
		}
		this.onUpdate();
		if (world.isRemote) {
			updateClient();
			return;
		}
	}

	public void onUnLink() {
		world.destroyBlock(pos, false);
	}

	@Override
	protected void findIceRockCore() {
		super.findIceRockCore();
		TileIceRockStand stand = tileCoreRef.get();
		if (stand != null) stand.notifySubNodeCome(this);
	}

	@SideOnly(Side.CLIENT)
	public float stockRatio;

	@SideOnly(Side.CLIENT)
	public boolean isRendered;

	@SideOnly(Side.CLIENT)
	public float spawnRatio, prevSpawnRatio;

	@SideOnly(Side.CLIENT)
	public void updateClient() {

		if ((tick - 1) % 20 == 0) {
			TileIceRockStand tile = getIceRockCore();
			if (tile != null)
				stockRatio = (float) Math.min(tile.getMagicFragment() / tile.getMagicFragmentCapacity(), 1);
		}

		prevSpawnRatio = spawnRatio;
		spawnRatio = spawnRatio + (1 - spawnRatio) * 0.075f;

		if (isRendered) {

			float range = 0.35f;
			Vec3d at = new Vec3d(pos).add(0.5, 0.5, 0.5).add(Effect.rand.nextGaussian() * range,
					Effect.rand.nextGaussian() * range, Effect.rand.nextGaussian() * range);
			EffectFragmentMove effect = new EffectFragmentMove(world, at);
			effect.lifeTime = 20 + Effect.rand.nextInt(20);
			Effect.addEffect(effect);
			effect.color.setColor(0x7cd0d3).weight(new Color(0x9956d0), stockRatio);
			BlockPos link = getLinkPos();
			if (link != null) {
				Vec3d target = new Vec3d(link).add(0.5, 0.5 + 1, 0.5);
				Vec3d tar = target.subtract(at).normalize().scale(Effect.rand.nextDouble() * 0.15 + 0.05);
				effect.xAccelerate = tar.x;
				effect.yAccelerate = tar.y;
				effect.zAccelerate = tar.z;
				effect.yDecay = effect.xDecay = effect.zDecay = 0.3;
			}

			isRendered = false;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean needRenderFaceEffect() {
		return true;
	}
}
