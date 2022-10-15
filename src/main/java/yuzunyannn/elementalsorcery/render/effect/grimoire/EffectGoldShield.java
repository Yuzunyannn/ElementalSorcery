package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.render.effect.EffectCondition;
import yuzunyannn.elementalsorcery.render.effect.IBinder;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementReactor;

@SideOnly(Side.CLIENT)
public class EffectGoldShield extends EffectCondition {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/effect/gold_shield.png");

	public float rotate;
	public float preRotate = rotate;
	public float scale = 1;
	public float preScale = scale = 1.25f;
	public float alpha = 0;
	public float preAlpha = alpha;
	public float defaultScale = 1;
	public IBinder binder;
	public boolean isClientUser;

	public EffectGoldShield(World world, IBinder binder) {
		super(world);
		this.lifeTime = 1;
		this.binder = binder;
	}

	@Override
	public boolean isDead() {
		return this.lifeTime <= 0;
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.preRotate = this.rotate;
		this.preScale = this.scale;
		this.preAlpha = this.alpha;

		Vec3d vec = this.binder.getPosition();
		this.posX = vec.x;
		this.posY = vec.y;
		this.posZ = vec.z;

		this.rotate += 1f;

		if (this.isEnd()) {
			this.lifeTime--;
			this.scale = this.scale + (1.5f - this.scale) * 0.3f;
			this.alpha = this.alpha + (0f - this.alpha) * 0.3f;
			return;
		}
		this.lifeTime = 10;
		this.scale = this.scale + (1 - this.scale) * 0.2f;

		if (isClientUser && mc.gameSettings.thirdPersonView == 0) alpha += (0.25f - alpha) * 0.1;
		else alpha += (1 - alpha) * 0.1;
	}

	@Override
	protected void doRender(float partialTicks) {

		GlStateManager.pushMatrix();
		double posX = RenderFriend.getPartialTicks(this.posX, this.prevPosX, partialTicks);
		double posY = RenderFriend.getPartialTicks(this.posY, this.prevPosY, partialTicks);
		double posZ = RenderFriend.getPartialTicks(this.posZ, this.prevPosZ, partialTicks);
		GlStateManager.translate(posX, posY, posZ);

		float alpha = RenderFriend.getPartialTicks(this.alpha, this.preAlpha, partialTicks);
		float rotate = RenderFriend.getPartialTicks(this.rotate, this.preRotate, partialTicks);
		float scale = RenderFriend.getPartialTicks(this.scale, this.preScale, partialTicks) * defaultScale;
		TEXTURE.bind();

		GlStateManager.color(1, 1, 1, alpha);
		GlStateManager.rotate(rotate, 0, 1, 0);
		GlStateManager.scale(scale, scale, scale);
		RenderTileElementReactor.MODEL_SPHERE.render();

		GlStateManager.popMatrix();
	}

}
