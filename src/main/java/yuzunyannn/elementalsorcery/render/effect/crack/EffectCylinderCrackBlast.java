package yuzunyannn.elementalsorcery.render.effect.crack;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.item.RenderItemElementCrack;
import yuzunyannn.elementalsorcery.util.render.ModelCylinder;

@SideOnly(Side.CLIENT)
public class EffectCylinderCrackBlast extends Effect {

	public final static ModelCylinder MODEL = new ModelCylinder();

	static {
		MODEL.height = 0.5;
	}

	public double scale, prevScale;
	public double targetScale;
	public double scaleHold = -1;
	public boolean isAttacking = true;

	public EffectCylinderCrackBlast(World worldIn, Vec3d vec) {
		super(worldIn, vec.x, vec.y, vec.z);
		this.hold();
	}

	@Override
	protected String myGroup() {
		return EffectBlockConfusion.GROUP_CONFUSION;
	}

	@Override
	public void onUpdate() {
		this.prevScale = this.scale;
		this.lifeTime--;
		if (!isAttacking) {
			if (scaleHold < 0) this.scaleHold = this.scale;
			float ratio = 1 - this.lifeTime / 40.0f;
			float s = (float) Math.pow(100, ratio + 1.5) / 10000 * ratio * ratio;
			if (s < 0.99) this.scale = this.scaleHold * (1 - s);
			else this.scale = this.scaleHold * 0.01 / (0.01 + s);
		} else {
			this.scale = this.targetScale;
			if (this.lifeTime == 40) this.isAttacking = false;
		}
	}

	public void hold() {
		this.lifeTime = 42;
	}

	@Override
	protected void doRender(float partialTicks) {
		double x = this.getRenderX(partialTicks);
		double y = this.getRenderY(partialTicks);
		double z = this.getRenderZ(partialTicks);
		double hScale = RenderFriend.getPartialTicks(scale, prevScale, partialTicks);

		Entity viewEntity = mc.getRenderViewEntity();
		double texGenScale = 5;
		double yScale = 256;
		if (viewEntity != null) {
			double ex = RenderFriend.getPartialTicks(viewEntity.posX, viewEntity.prevPosX, partialTicks);
			double ey = RenderFriend.getPartialTicks(viewEntity.posY, viewEntity.prevPosY, partialTicks);
			double ez = RenderFriend.getPartialTicks(viewEntity.posZ, viewEntity.prevPosZ, partialTicks);
			Vec3d tar = new Vec3d(ex - x, ey - y, ez - z);
			texGenScale = Math.max(0.1, MathHelper.sqrt(tar.length()));
			yScale = Math.abs(tar.y) * 2 + 256;
		}
		if (isAttacking) yScale = yScale * Math.min(hScale, 1);

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.scale(hScale, yScale, hScale);
		RenderItemElementCrack.bindCrackTexture();
		RenderItemElementCrack.startTexGen(texGenScale);
//		GlStateManager.depthFunc(519);
		MODEL.render();
//		GlStateManager.depthFunc(515);
		RenderItemElementCrack.endTexGen();
		GlStateManager.popMatrix();
	}

}
