package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityBlockMove;
import yuzunyannn.elementalsorcery.render.item.RenderItemElementCrack;
import yuzunyannn.elementalsorcery.ts.PocketWatchClient;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.Shaders;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class EffectBlockDisintegrate extends Effect {

	public final static TextureBinder[] MASKS = new TextureBinder[12];

	static {
		for (int i = 0; i < MASKS.length; i++)
			MASKS[i] = new TextureBinder(String.format("textures/effect/block_disintegrate_mask/s_%02d.png", i + 1));
	}

	public IBlockState state;
	public ItemStack stack = ItemStack.EMPTY;
	public TileEntity tile;
	public BlockPos start;

	public int startWaitingTick = 0;

	public final Color color = new Color();

	public float rate, prevRate;

	public Vec3d rotateVec;

	public EffectBlockDisintegrate(World world, Vec3d pos, IBlockState state) {
		super(world, pos.x, pos.y, pos.z);
		this.state = state;
		this.stack = ItemHelper.toItemStack(state);
		this.start = new BlockPos(pos);
		this.lifeTime = 20 + 1;
	}

	@Override
	public void onUpdate() {
		if (startWaitingTick > 0) {
			startWaitingTick--;
			return;
		}
		this.lifeTime--;
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.prevRate = this.rate;

		this.rate = Math.min(this.rate + (MASKS.length - 1) / 20f, MASKS.length - 1);

		this.posY += 0.01f;

		int dt = Math.max(1, (int) ((this.rate / (MASKS.length - 1)) * 40));
		if (this.lifeTime % dt == 0) {
			EffectElementMove effect = new EffectElementMove(world, this.getPositionVector());
			effect.setColor(color.r, color.g, color.b);
			Vec3d speed = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
			effect.setVelocity(speed.scale(0.1));
			effect.xDecay = effect.yDecay = effect.zDecay = 0.8f;
			addEffect(effect);
		}
	}

	@Override
	protected void doRender(float partialTicks) {
		GlStateManager.pushMatrix();
		double x = getRenderX(partialTicks);
		double y = getRenderY(partialTicks);
		double z = getRenderZ(partialTicks);
		GlStateManager.translate(x, y - 0.5f, z);
		GlStateManager.depthMask(true);

		float rate = RenderHelper.getPartialTicks(this.rate, this.prevRate, partialTicks);
		int a = Math.min(MathHelper.floor(rate), MASKS.length - 1);
		int b = Math.min(a + 1, MASKS.length - 1);

		float rr = rate / (MASKS.length - 1);
		if (rotateVec != null) {
			GlStateManager.rotate((float) Math.pow(4, rr + 1) * rr, (float) rotateVec.x, (float) rotateVec.y,
					(float) rotateVec.z);
		}
		float ss = 1 - rr * 0.2f;
		GlStateManager.scale(ss, ss, ss);

		final boolean canUseShader = true;

		if (canUseShader) {
			MASKS[a].bindAtive(1);
			MASKS[b].bindAtive(2);
			RenderItemElementCrack.END_SKY_TEXTURE.bindAtive(3);
			Shaders.BlockDisintegrate.bind();
			// Shaders.BlockDisintegrate.setUniform("rc", true);
			Shaders.BlockDisintegrate.setUniform("gray", PocketWatchClient.isActive());
			Shaders.BlockDisintegrate.setUniform("color", color);
			Shaders.BlockDisintegrate.setUniform("r", rate - a);
			Shaders.BlockDisintegrate.setUniform("maskA", 1);
			Shaders.BlockDisintegrate.setUniform("maskB", 2);
			Shaders.BlockDisintegrate.setUniform("texB", 3);
		}

		try {
			RenderEntityBlockMove.doRenderBlock(this.state, this.stack, partialTicks, world, this.start, this.tile);
		} catch (Exception e) {
			if (tile != null) tile = null;
			else throw e;
		}

		if (canUseShader) {
			Shaders.BlockDisintegrate.unbind();
			MASKS[a].unbindAtive(1);
			MASKS[b].unbindAtive(2);
			RenderItemElementCrack.END_SKY_TEXTURE.unbindAtive(3);
		}

		GlStateManager.enableBlend();
		GlStateManager.enableTexture2D();
		GlStateManager.depthMask(false);
		GlStateManager.popMatrix();
	}

}
