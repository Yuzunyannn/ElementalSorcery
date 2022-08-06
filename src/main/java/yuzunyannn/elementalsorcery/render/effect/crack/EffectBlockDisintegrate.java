package yuzunyannn.elementalsorcery.render.effect.crack;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityBlockMove;
import yuzunyannn.elementalsorcery.render.item.RenderItemElementCrack;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.render.Shaders;

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
	public Vec3d axis;
	public float sRate, prevSRate;

	public int startWaitingTick = 0;

	public final Color color = new Color();

	public float rate, prevRate;

	public EffectBlockDisintegrate(World world, Vec3d pos, IBlockState state) {
		super(world, pos.x, pos.y, pos.z);
		this.state = state;
		this.stack = ItemHelper.toItemStack(state);
		this.start = new BlockPos(pos);
		this.lifeTime = 20 + 1;
		axis = new Vec3d(-0.99, 16 + rand.nextInt(16), -0.99);
	}

	@Override
	protected String myGroup() {
		return EffectBlockConfusion.GROUP_CONFUSION;
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

		this.prevSRate = this.sRate;
		float ratio = rate / (MASKS.length - 1);
		float s = (float) Math.pow(100, ratio + 1) / 10000f;
		this.sRate = s * s;

		int dt = Math.max(1, (int) (ratio) * 40);
		if (this.lifeTime % dt == 0) {
			EffectFragmentCrackMove effect = new EffectFragmentCrackMove(world, this.getPositionVector());
			effect.defaultScale = effect.prevScale = effect.scale = rand.nextFloat() * 0.05f + 0.01f;
			effect.lifeTime = 30;
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

		float rate = RenderFriend.getPartialTicks(this.rate, this.prevRate, partialTicks);
		int aIndex = Math.min(MathHelper.floor(rate), MASKS.length - 1);
		int bIndex = Math.min(aIndex + 1, MASKS.length - 1);

		float sRate = RenderFriend.getPartialTicks(this.sRate, this.prevSRate, partialTicks);
		GlStateManager.translate(0, 0.5f, 0);
		GlStateManager.scale(1 + sRate * axis.x, 1 + sRate * axis.y, 1 + sRate * axis.z);
		GlStateManager.translate(0, -0.5f, 0);

		MASKS[aIndex].bindAtive(2);
		MASKS[bIndex].bindAtive(3);
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + 4);
		RenderItemElementCrack.bindCrackTexture();
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

		Shaders.BlockDisintegrate.bind();
		Shaders.BlockDisintegrate.setUniform("color", color);
		Shaders.BlockDisintegrate.setUniform("r", rate - aIndex);
		Shaders.BlockDisintegrate.setUniform("maskA", 2);
		Shaders.BlockDisintegrate.setUniform("maskB", 3);
		Shaders.BlockDisintegrate.setUniform("texB", 4);

		try {
			RenderEntityBlockMove.doRenderBlock(this.state, this.stack, partialTicks, world, this.start, this.tile);
		} catch (Exception e) {
			if (tile != null) tile = null;
			else throw e;
		}

		Shaders.BlockDisintegrate.unbind();
		MASKS[aIndex].unbindAtive(2);
		MASKS[bIndex].unbindAtive(3);
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + 4);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableTexture2D();

		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		RenderFriend.disableLightmap(true);

		GlStateManager.popMatrix();
	}

}
