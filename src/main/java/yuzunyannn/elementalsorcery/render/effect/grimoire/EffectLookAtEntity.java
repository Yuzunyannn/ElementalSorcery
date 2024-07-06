package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.api.util.target.WorldTarget;
import yuzunyannn.elementalsorcery.render.effect.EffectCondition;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public class EffectLookAtEntity extends EffectCondition {

	public static final TextureBinder TEXTURE = EffectLookAtBlock.TEXTURE;

	public ICaster caster;
	public Color color = new Color();
	public float rotate;
	public float preRotate = rotate;
	public float scale = 1;
	public float preScale = scale;
	public float alpha = 0;
	public float preAlpha = alpha;
	public float height = 0;

	public EffectLookAtEntity(World world, ICaster caster, int color) {
		super(world);
		this.lifeTime = 1;
		this.caster = caster;
		this.color.setColor(color);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		preRotate = rotate;
		preScale = scale;
		preAlpha = alpha;

		WorldTarget wr = caster.iWantEntityTarget(EntityLivingBase.class);
		Entity hitEntity = wr.getEntity();
		if (hitEntity == null) {
			alpha += (0 - alpha) * 0.1;
			return;
		}

		posX = hitEntity.posX;
		posY = hitEntity.posY;
		posZ = hitEntity.posZ;
		height = hitEntity.height;

		rotate = rotate + 3f;
		scale -= 0.05f;
		if (scale <= 0) preScale = scale = 1;
		alpha += (1 - alpha) * 0.1;
	}

	@Override
	protected void doRender(float partialTicks) {

		GlStateManager.pushMatrix();
		double posX = RenderFriend.getPartialTicks(this.posX, this.prevPosX, partialTicks);
		double posY = RenderFriend.getPartialTicks(this.posY, this.prevPosY, partialTicks);
		double posZ = RenderFriend.getPartialTicks(this.posZ, this.prevPosZ, partialTicks);
		GlStateManager.translate(posX, posY, posZ);
		GlStateManager.rotate(90, 1, 0, 0);

		float alpha = RenderFriend.getPartialTicks(this.alpha, this.preAlpha, partialTicks);
		float rotate = RenderFriend.getPartialTicks(this.rotate, this.preRotate, partialTicks);
		float scale = RenderFriend.getPartialTicks(this.scale, this.preScale, partialTicks);
		TEXTURE.bind();

		float r = color.r;
		float g = color.g;
		float b = color.b;

		GlStateManager.translate(0, 0, -scale * height);
		GlStateManager.rotate(rotate, 0, 0.1f, 1);
		GlStateManager.scale(1.25, 1.25, 1.25);
		GlStateManager.scale(scale + 1, scale + 1, scale + 1);
		this.renderTexRectInCenter(0, 0, 1, 1, r, g, b, (1 - scale) * alpha);

		GlStateManager.popMatrix();
	}

}
