package yuzunyannn.elementalsorcery.render.effect.scrappy;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.mantra.IFragmentMantraLauncher;
import yuzunyannn.elementalsorcery.api.mantra.IFragmentMantraLauncher.MLPair;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectFragmentMove;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectLaser;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementReactor;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public class EffectReactorMantraSpell extends EffectLaser {

	public static void show(World world, Vec3d pos, NBTTagCompound nbt) {
		int id = nbt.getInteger("dimId");
		if (id != world.provider.getDimension()) return;
		MLPair pair = IFragmentMantraLauncher.fromId(nbt.getString("lId"));
		if (pair == null) return;
		EffectReactorMantraSpell effect = new EffectReactorMantraSpell(world, pos, true);
		effect.setMainColor(new Color(pair.mantra.getColor(null)));
		Effect.addEffect(effect);
		pair.launcher.castClientTo(world, new BlockPos(pos.subtract(0, -0.5, 0)));
	}

	public final Color circleColor = new Color();
	public float progress, prevProgress;
	public float upEndProgress = -1, prevUpEndProgress;
	public IFragmentMantraLauncher launcher;
	public boolean isDown;

	public int tick;

	public EffectReactorMantraSpell(World world, Vec3d pos, boolean isDown) {
		super(world, pos, pos);
		this.isDown = isDown;
		this.lifeTime = 40;
		onUpdate();
	}

	public void setMainColor(Color color) {
		this.color.setColor(color).weight(new Color(0xffffff), 0.9f);
		circleColor.setColor(color);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		tick++;
		Entity entity = mc.getRenderViewEntity();
		if (entity != null) {
			if (isDown) this.posY = entity.posY + 128;
			else this.targetY = entity.posY + 128;
		}
		if (this.isDown) {
			this.prevProgress = this.progress;
			this.progress = 1 - this.lifeTime / 40.0f;

			if (this.lifeTime == 39) return;
			for (int i = 0; i < 2; i++) {
				Vec3d to = new Vec3d(targetX, targetY, targetZ);
				Vec3d from = this.getPositionVector();
				Vec3d tar = to.subtract(from);
				Vec3d randSpeed = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).normalize()
						.scale(0.1);
				Vec3d speed = tar.normalize().scale(-0.15).add(randSpeed);
				EffectFragmentMove effect = new EffectFragmentMove(world, to) {
					@Override
					public void onUpdate() {
						super.onUpdate();
						this.alpha = this.alpha * EffectReactorMantraSpell.this.alpha;
					}
				};
				effect.endLifeTick = effect.lifeTime;
				effect.prevScale = effect.scale = effect.defaultScale = rand.nextFloat() * 0.1f + 0.1f;
				effect.color.setColor(this.color);
				effect.motionX = speed.x;
				effect.motionY = speed.y;
				effect.motionZ = speed.z;
				addEffect(effect);
			}
		} else {
			if (upEndProgress >= 0) {
				prevUpEndProgress = upEndProgress;
				upEndProgress = upEndProgress + (1 - upEndProgress) * 0.2f;
			}
		}
	}

	@Override
	protected void renderHeadTail(double len, float a, float partialTicks) {
		if (isDown) renderDown(len, a, partialTicks);
		else renderUp(len, a, partialTicks);
	}

	protected void renderDown(double len, float a, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(90, 1, 0, 0);

		float progress = RenderFriend.getPartialTicks(this.progress, this.prevProgress, partialTicks);
		float rotation = EventClient.getGlobalRotateInRender(partialTicks);

		boolean fb = false;
		float a_scale = 1;
		if (tick < 16) {
			a_scale = (tick + partialTicks) / 16.0f;
			a_scale = (float) (1 + 2.70158f * Math.pow(a_scale - 1, 3) + 1.70158f * Math.pow(a_scale - 1, 2));
		}
		if (lifeTime < 16) {
			a_scale = (lifeTime - partialTicks) / 16.0f;
			a_scale = (float) (1 + 2.70158f * Math.pow(a_scale - 1, 3) + 1.70158f * Math.pow(a_scale - 1, 2));
			fb = true;
		}

		GlStateManager.scale(a_scale, 1, a_scale);
		RenderTileElementReactor.renderCircle(-rotation, circleColor, 20f, 1, true);
		if (fb) GlStateManager.scale(1 / a_scale, 1, 1 / a_scale);

		GlStateManager.translate(0, 1, 0);
		RenderTileElementReactor.renderCircle(rotation, circleColor, 8.2f + (1 - a_scale) * 10, a_scale, true);
		if (launcher != null) {
			GlStateManager.rotate(90, 1, 0, 0);
			GlStateManager.rotate(rotation * 2, 0, 0, 1);
			GlStateManager.color(circleColor.r, circleColor.g, circleColor.b, a_scale);
			launcher.renderIcon(8.2f * a_scale, 1 - progress, partialTicks);
			GlStateManager.rotate(-rotation * 2, 0, 0, 1);
			GlStateManager.rotate(-90, 1, 0, 0);
		}

		GlStateManager.popMatrix();
	}

	protected void renderUp(double len, float a, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(90, 1, 0, 0);

		float progress = RenderFriend.getPartialTicks(this.progress, this.prevProgress, partialTicks);

		float aplha = Math.min(1, this.lifeTime / 20.0f);

		float a_aplha = 1;
		float b_aplha = 1;
		if (tick < 20) a_aplha = (tick + partialTicks) / 20.0f;
		if (tick < 40) b_aplha = (tick + partialTicks) / 40.0f;
		float rotation = EventClient.getGlobalRotateInRender(partialTicks);
		float defaultYMove = 0;
		float endPorgress = 0;

		if (upEndProgress >= 0) {
			endPorgress = RenderFriend.getPartialTicks(this.upEndProgress, this.prevUpEndProgress, partialTicks);
			defaultYMove = (float) (endPorgress * len);
			progress = Math.max(progress, endPorgress);
		}
		GlStateManager.translate(0, 3.5 + defaultYMove, 0);
		RenderTileElementReactor.renderCircle(rotation, circleColor, 8.2f + 10f * endPorgress, a_aplha * aplha, true);
		GlStateManager.translate(0, -3.5 - defaultYMove, 0);

		GlStateManager.translate(0, 4.5 + defaultYMove, 0);
		RenderTileElementReactor.renderCircle(-rotation, circleColor, 4f + 6f * endPorgress, b_aplha * aplha, true);
		GlStateManager.translate(0, -4.5 - defaultYMove, 0);

		GlStateManager.translate(0, 3.5 + (len - 4) * progress, 0);
		RenderTileElementReactor.renderCircle(rotation, circleColor, 8.2f, progress * a_aplha * aplha, true);
		if (launcher != null) {
			GlStateManager.rotate(90, 1, 0, 0);
			GlStateManager.rotate(rotation * 2, 0, 0, 1);
			GlStateManager.color(circleColor.r, circleColor.g, circleColor.b, progress * a_aplha * aplha);
			launcher.renderIcon(8.2f, progress * a_aplha * aplha, partialTicks);
			GlStateManager.rotate(-rotation * 2, 0, 0, 1);
			GlStateManager.rotate(-90, 1, 0, 0);
		}
		GlStateManager.translate(0, -3.5 - (len - 4) * progress, 0);

		float p = progress * progress;
		GlStateManager.translate(0, 4.5 + len * p, 0);
		RenderTileElementReactor.renderCircle(-rotation, circleColor, 4f + p * 16f, b_aplha * aplha, true);
		GlStateManager.translate(0, -4.5 - len * p, 0);

		GlStateManager.translate(0, len, 0);
		RenderTileElementReactor.renderCircle(-rotation, circleColor, 20f, a_aplha * aplha, true);

		GlStateManager.popMatrix();
	}
}
