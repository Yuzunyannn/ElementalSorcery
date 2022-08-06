package yuzunyannn.elementalsorcery.render.entity;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.crafting.ICraftingCommit;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyannn.elementalsorcery.crafting.altar.CraftingDeconstruct;
import yuzunyannn.elementalsorcery.entity.EntityCrafting;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FirewrokShap;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

@SideOnly(Side.CLIENT)
public class AnimeRenderDeconstruct implements ICraftingLaunchAnime {

	// 高度
	public float high = 0.0f;
	public float preHigh = 0.0f;
	// 旋转
	public float roate = 0;
	public float preRoate = 0;
	// 结束颜色
	public int[] endColors = null;

	@Override
	public void doRender(ICraftingCommit commit, double x, double y, double z, float entityYaw, float partialTicks) {
		List<ItemStack> list = commit.getItems();
		if (list == null || list.isEmpty()) return;
		ItemStack stack = list.get(0);
		float high = this.preHigh + (this.high - this.preHigh) * partialTicks;
		float roate = this.preRoate + (this.roate - this.preRoate) * partialTicks;
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y + 0.5 + high, (float) z);
		yuzunyannn.elementalsorcery.api.util.client.RenderFriend.layItemPositionFix(stack);
		GlStateManager.rotate(roate, 0, 1, 0);

		int count = stack.getCount() / 14 + 1;
		for (int i = 0; i < count; i++) {
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.translate(0.02, 0.02, 0.02);
		}
		GlStateManager.popMatrix();
	}

	@Override
	public void endEffect(ICraftingCommit commit, World world, BlockPos pos, int flags) {
		if (flags != ICraftingLaunch.SUCCESS) return;
		if (endColors != null) {
			Vec3d position = new Vec3d(pos).add(0.5, 0.5, 0.5);
			FirewrokShap.createECircle(world, position, 0.2, 2, endColors);
			return;
		}
		EntityCrafting.defaultEndEffect(world, pos);
	}

	@Override
	public void update(ICraftingCommit commit, World world, BlockPos pos, int endTick) {
		this.preHigh = this.high;
		this.preRoate = this.roate;
		if (endTick >= 0) {

		}
		if (high < 0.5f) high += 0.01f;
		this.roate += 1.0f;

		if (commit instanceof CraftingDeconstruct) {
			CraftingDeconstruct cd = (CraftingDeconstruct) commit;
			if (!cd.freeElement.isEmpty() && world.rand.nextFloat() <= 0.5) {
				Vec3d at = new Vec3d(pos.up());
				at = at.add(0.5, high, 0.5);
				EffectElementMove move = new EffectElementMove(world, at);
				Vec3d speed = new Vec3d(Math.random(), Math.random(), Math.random()).normalize();
				move.setVelocity(speed.scale(Math.random() * 0.1 - 0.05));
				move.setColor(cd.freeElement.getColor());
				Effect.addEffect(move);
			}
		}

		if (endColors == null) {
			if (commit instanceof CraftingDeconstruct) {
				CraftingDeconstruct cd = (CraftingDeconstruct) commit;
				endColors = ElementHelper.toColor(cd.getRestElementStacks());
			}
			if (endColors == null || endColors.length <= 0) endColors = new int[] { 0xe2e2ef, 0xa590de };
		}
	}

}
