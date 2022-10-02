package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.render.effect.EffectCondition;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;

@SideOnly(Side.CLIENT)
public class EffectLookAtBlock extends EffectCondition {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/magic_circles/triangle.png");

	public ICaster caster;
	public float r = 0;
	public float g = 0;
	public float b = 0;
	public float rotate;
	public float preRotate = rotate;
	public float scale = 1;
	public float preScale = scale;
	public float alpha = 0;
	public float preAlpha = alpha;
	public EnumFacing facing = EnumFacing.UP;

	public EffectLookAtBlock(World world, ICaster caster, int color) {
		super(world);
		this.lifeTime = 1;
		this.caster = caster;
		Vec3d c = ColorHelper.color(color);
		r = (float) c.x;
		g = (float) c.y;
		b = (float) c.z;
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

		WorldTarget wr = caster.iWantBlockTarget();
		BlockPos pos = wr.getPos();
		if (pos == null) {
			alpha += (0 - alpha) * 0.1;
			return;
		}
		facing = wr.getFace();
		EnumFacing facing = this.facing == null ? EnumFacing.UP : this.facing;
		Vec3i dv = facing.getDirectionVec();

		posX = pos.getX() + 0.5 + dv.getX() * 0.51;
		posY = pos.getY() + 0.5 + dv.getY() * 0.51;
		posZ = pos.getZ() + 0.5 + dv.getZ() * 0.51;

		rotate = rotate + 3f;
		scale -= 0.05f;
		if (scale <= 0) preScale = scale = 1;
		alpha += (1 - alpha) * 0.1;
	}

	@Override
	protected void doRender(float partialTicks) {
		EnumFacing facing = this.facing == null ? EnumFacing.UP : this.facing;
		GlStateManager.pushMatrix();
		double posX = RenderFriend.getPartialTicks(this.posX, this.prevPosX, partialTicks);
		double posY = RenderFriend.getPartialTicks(this.posY, this.prevPosY, partialTicks);
		double posZ = RenderFriend.getPartialTicks(this.posZ, this.prevPosZ, partialTicks);
		GlStateManager.translate(posX, posY, posZ);

		Vec3i dv = facing.getDirectionVec();
		GlStateManager.rotate(90, dv.getY(), dv.getX(), dv.getZ());

		float alpha = RenderFriend.getPartialTicks(this.alpha, this.preAlpha, partialTicks);
		float rotate = RenderFriend.getPartialTicks(this.rotate, this.preRotate, partialTicks);
		float scale = RenderFriend.getPartialTicks(this.scale, this.preScale, partialTicks);
		TEXTURE.bind();

		int l = (Math.abs(dv.getX()) + dv.getZ() - Math.abs(dv.getY()));
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, l * (1 - scale) * 0.2f);
		GlStateManager.rotate(rotate, 0, 0, 1);
		GlStateManager.scale(scale, scale, scale);
		this.renderTexRectInCenter(0, 0, 1, 1, r, g, b, scale * alpha);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, l * scale * 0.1f);
		GlStateManager.rotate(rotate, 0, 0, 1);
		GlStateManager.scale(scale + 1, scale + 1, scale + 1);
		this.renderTexRectInCenter(0, 0, 1, 1, r, g, b, (1 - scale) * alpha);
		GlStateManager.popMatrix();

		GlStateManager.popMatrix();
	}

}
