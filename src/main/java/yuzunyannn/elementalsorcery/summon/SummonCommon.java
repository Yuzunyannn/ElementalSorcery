package yuzunyannn.elementalsorcery.summon;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class SummonCommon extends Summon {

	protected int size = 1;
	protected int height = 2;
	protected int color;
	protected int tick;

	public SummonCommon(World world, BlockPos pos) {
		this(world, pos, 0xda003e);
	}

	public SummonCommon(World world, BlockPos pos, int color) {
		super(world, pos);
		this.initData();
		if (this.color == 0) this.color = color;
		if (world.isRemote) this.initRender();
	}

	public void initData() {

	}

	public BlockPos getPosition() {
		return pos;
	}

	@Override
	public boolean update() {
		this.tick++;
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void initRender() {
		renders.clear();
		Vec3d c = ColorHelper.color(getColor());
		r = (float) c.x;
		g = (float) c.y;
		b = (float) c.z;

		final float HIGH = height;

		RenderObject ro1 = new RenderObject();
		ro1.y = 0.1f;
		ro1.dRotate = -1.75f;
		ro1.alpha = 0.45f;
		ro1.scale = 2f;
		renders.add(ro1);

		RenderObject ro2 = new RenderObject();
		ro2.dRotate = 1;
		renders.add(ro2);

		RenderObject ro3 = new RenderObject();
		ro3.y = HIGH;
		ro3.dRotate = -1.75f;
		ro3.alpha = 0.75f;
		ro3.scale = 2f;
		renders.add(ro3);

		{
			RenderObject ro = new RenderObject() {
				@Override
				public void update() {
					super.update();
					y += HIGH / (20 * 8);
					if (y > HIGH) {
						preY = y = 0;
						preRotate = ro2.preRotate;
						rotate = ro2.rotate;
						preScale = scale = 1;
					}
					alpha = (1 - y / HIGH) * 0.75f;
					scale = 1 + y / HIGH * 0.25f;
				}
			};
			ro.dRotate = 1.25f;
			renders.add(ro);
		}
		{
			RenderObject ro = new RenderObject() {
				@Override
				public void update() {
					super.update();
					y -= HIGH / (20 * 6);
					if (y <= 0) {
						preY = y = HIGH;
						preScale = scale = 1.75f;
						preAlpha = alpha = 0;
					}
					if (y / HIGH > 0.5) alpha = (0.5f - (y / HIGH - 0.5f)) * 2;
					else alpha = y / HIGH * 2;
					scale = 1.75f - (1 - y / HIGH) * 0.5f;
				}
			};
			ro.dRotate = -1.5f;
			ro.preY = ro.y = 1;
			renders.add(ro);
		}
	}

	public float r, g, b;
	public float alpha = 0;
	public float preAlpha = alpha;

	public List<RenderObject> renders = new LinkedList<>();

	public class RenderObject {
		public float dRotate;
		public float rotate;
		public float preRotate = rotate;
		public float scale = 1;
		public float preScale = scale;
		public float alpha = 1;
		public float preAlpha = alpha;
		public float y = 0;
		public float preY = y;

		public void update() {
			preY = y;
			preRotate = rotate;
			preAlpha = alpha;
			preScale = scale;
			rotate += dRotate;
		}

		public void render(float partialTicks, float alpha) {
			GlStateManager.pushMatrix();
			float rotate = RenderFriend.getPartialTicks(this.rotate, this.preRotate, partialTicks);
			float scale = RenderFriend.getPartialTicks(this.scale, this.preScale, partialTicks);
			alpha = RenderFriend.getPartialTicks(this.alpha, this.preAlpha, partialTicks) * alpha;
			float y = RenderFriend.getPartialTicks(this.y, this.preY, partialTicks);
			GlStateManager.translate(0, 0, -y / size);
			GlStateManager.rotate(rotate, 0, 0, 1);
			GlStateManager.color(r, g, b, alpha);
			GlStateManager.scale(scale, scale, scale);
			RenderFriend.drawTextureRectInCenter(0, 0, 1, 1);
			GlStateManager.popMatrix();
		}
	}

	@SideOnly(Side.CLIENT)
	public int getColor() {
		return color;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderEndTick() {
		return 20;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateRender(int endTick) {
		preAlpha = alpha;
		if (endTick >= 20) alpha += (1 - alpha) * 0.05f;
		else alpha = endTick / 20f;
		for (RenderObject obj : renders) obj.update();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void doRender(Minecraft mc, float partialTicks) {
		BlockPos pos = this.getPosition();
		GlStateManager.pushMatrix();
		GlStateManager.translate(pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.scale(size, size, size);
		mc.getTextureManager().bindTexture(RenderObjects.MAGIC_CIRCLE_SUMMON);
		float alpha = RenderFriend.getPartialTicks(this.alpha, this.preAlpha, partialTicks);
		for (RenderObject obj : renders) obj.render(partialTicks, alpha);
		GlStateManager.popMatrix();
	}

	protected void genBlock(BlockPos pos, IBlockState state) {
		if (BlockHelper.isBedrock(world, pos)) return;
		Vec3d from = new Vec3d(pos).add(0.5, 0.5, 0.5);
		EntityBlockMove entity = new EntityBlockMove(world, from, pos, state);
		entity.setFlag(EntityBlockMove.FLAG_FORCE_DESTRUCT, true);
		entity.setFlag(EntityBlockMove.FLAG_INTO_CHEST, false);
		entity.setFlag(EntityBlockMove.FLAG_DESTRUCT_DROP, true);
		world.spawnEntity(entity);
	}

}
