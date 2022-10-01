package yuzunyannn.elementalsorcery.render.effect.grimoire;

import java.util.UUID;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.render.effect.EffectCondition;
import yuzunyannn.elementalsorcery.render.effect.IBinder;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectIceFragment;
import yuzunyannn.elementalsorcery.render.tile.RenderTileIceRockStand;
import yuzunyannn.elementalsorcery.util.render.DisplayList;

@SideOnly(Side.CLIENT)
public class EffectIceCrystalBomb extends EffectCondition {

	public static final TextureBinder TEXTURE = new TextureBinder("minecraft", "textures/blocks/ice_packed.png");
	public static final DisplayList MODEL = new DisplayList() {
		protected void doRender() {
			GlStateManager.translate(-0.5, -1.5, -0.5);
			RenderTileIceRockStand.renderCrystal(3);
			GlStateManager.translate(0.5, 1.5, 0.5);
		};
	};

	public IBinder binder;
	public UUID passHidePlayer;

	public EffectIceCrystalBomb(World world, IWorldObject obj) {
		super(world);
		this.binder = new IBinder.WorldObjectBinder(obj,
				(float) obj.getEyePosition().subtract(obj.getPositionVector()).y);
		this.init();
	}

	public EffectIceCrystalBomb(World world, Vec3d pos) {
		super(world);
		this.binder = new IBinder.VecBinder(pos);
		this.init();
	}

	protected void init() {
		this.preAlpha = this.alpha = 1;
		this.lifeTime = 10;
		this.setPosition(this.binder);
		randomStyleRoate = rand.nextFloat() * 60 + 60;
	}

	public void setPosition(IBinder binder) {
		this.setPosition(binder.getPosition());
	}

	public float randomStyleRoate;
	public float rotate;
	public float preRotate;
	public float alpha, preAlpha;

	public int endLockLifetime = 10;
	public boolean isFirst = true;
	public boolean isSuper = false;
	public float buildRate = 0;
	public float defaultScale = 0.5f;

	@Override
	public void onUpdate() {
		this.preRotate = this.rotate;
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.preAlpha = this.alpha;

		if (isEnd()) {
			this.lifeTime--;
			if (this.lifeTime == 0) {
				playEndBlastEffect(world, getPositionVector(), false);
				world.playSound(mc.player, posX, posY, posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.VOICE, 1, 1);
				return;
			}
			alpha = alpha + (1 - alpha) * 0.2f;
			float r = 1 - (this.lifeTime / (float) this.endLockLifetime);
			this.rotate += 100 * r;
		} else {
			this.lifeTime = 10;

			Entity viewEntity = mc.getRenderViewEntity();
			boolean isMy = mc.player != null && mc.player.getUniqueID().equals(passHidePlayer);
			if (viewEntity != null && !isMy) {
				double distance = viewEntity.getDistance(posX, posY, posZ);
				if (distance >= (isMy ? 16 : 5)) alpha = alpha + (0.025f - alpha) * 0.1f;
				else alpha = alpha + (1 - alpha) * 0.1f;
			} else alpha = alpha + (1 - alpha) * 0.2f;

			if (isSuper) {
				Vec3d at = this.getPositionVector();
				Vec3d speed = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).scale(0.02);
				at = at.add(speed.scale(-50 * rand.nextDouble() - 50));
				EffectIceFragment effect = new EffectIceFragment(world, at);
				effect.prevAlpha = effect.alpha = 1;
				effect.setAccelerate(speed);
				effect.setVelocity(speed.scale(25));
				effect.setDecay(0.3);
				addEffect(effect);
			}

			if (isFirst) {
				isFirst = false;
				for (int i = 0; i < 64; i++) {
					Vec3d at = this.getPositionVector();
					Vec3d speed = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
					speed = speed.normalize();
					EffectIceFragment effect = new EffectIceFragment(world, at);
					effect.prevAlpha = effect.alpha = 1;
					effect.setVelocity(speed);
					effect.setAccelerate(speed.add(0, -0.25, 0).normalize().scale(0.01));
					effect.setDecay(0.3);
					addEffect(effect);
				}
				world.playSound(mc.player, posX, posY, posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.VOICE, 1, 2);
			}
		}
		this.rotate += 1f;
	}

	public void toBomb(int tick) {
		this.endLockLifetime = this.lifeTime = tick;
		this.setCondition(v -> false);
	}

	@Override
	public boolean isDead() {
		return lifeTime <= 0;
	}

	public static void playEndBlastEffect(World world, Vec3d vec, boolean isSmall) {
		int times = isSmall ? 8 : 64;
		for (int i = 0; i < times; i++) {
			Vec3d speed = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
			EffectIceFragment effect = new EffectIceFragment(world, vec);
			effect.randomUV(0.25f);
			effect.prevAlpha = effect.alpha = 1;
			effect.prevScale = effect.scale = (float) (effect.scale * (1.5 + rand.nextDouble() * 3));
			effect.setDecay(0.9);
			effect.setVelocity(speed.add(0, 0.5, 0).normalize().scale(0.2));
			effect.setAccelerate(speed.add(0, -2, 0).normalize().scale(0.02 * rand.nextDouble()));
			addEffect(effect);
		}
		if (isSmall) return;
		for (int i = 0; i < 64; i++) {
			Vec3d speed = new Vec3d(rand.nextGaussian(), 0, rand.nextGaussian());
			EffectIceFragment effect = new EffectIceFragment(world, vec);
			effect.randomUV(0.25f);
			effect.prevAlpha = effect.alpha = 1;
			effect.prevScale = effect.scale = (float) (effect.scale * (1 + rand.nextDouble() * 2));
			effect.setDecay(0.7);
			effect.setVelocity(speed.normalize().scale(0.75));
			effect.setAccelerate(speed.add(0, -3, 0).normalize().scale(0.02 * rand.nextDouble()));
			addEffect(effect);
		}
	}

	@Override
	protected void doRender(float partialTicks) {
		GlStateManager.pushMatrix();

		double posX = RenderFriend.getPartialTicks(this.posX, this.prevPosX, partialTicks);
		double posY = RenderFriend.getPartialTicks(this.posY, this.prevPosY, partialTicks);
		double posZ = RenderFriend.getPartialTicks(this.posZ, this.prevPosZ, partialTicks);
		GlStateManager.translate(posX, posY + 0.1f, posZ);

		float rotate = RenderFriend.getPartialTicks(this.rotate, this.preRotate, partialTicks);
		GlStateManager.scale(defaultScale, defaultScale, defaultScale);
		GlStateManager.rotate(rotate, 0, 1, 0);

		float alpha = RenderFriend.getPartialTicks(this.alpha, this.preAlpha, partialTicks);
		GlStateManager.color(1, 1, 1, alpha);

		TEXTURE.bind();
		GlStateManager.depthMask(true);

		final float r = randomStyleRoate;
		MODEL.render();
		GlStateManager.translate(0.01, -0.01, 0.01);
		GlStateManager.rotate(r, 0, 0, 1);
		MODEL.render();
		GlStateManager.translate(0.01, 0.01, 0.01);
		GlStateManager.rotate(r + 180, 1, 0, 0);
		MODEL.render();
		if (isSuper) {
			GlStateManager.translate(0.01, 0.01, -0.01);
			GlStateManager.rotate(r / 4 * 3, 1, 1, 0.25f);
			MODEL.render();
			GlStateManager.translate(-0.01, -0.01, -0.01);
			GlStateManager.rotate(r + 180, 0, 0, 1);
			MODEL.render();
			GlStateManager.translate(-0.01, -0.01, 0.01);
			GlStateManager.rotate(-r, 1, 0, 0);
			MODEL.render();
		}

		GlStateManager.depthMask(false);
		GlStateManager.popMatrix();
	}

}
