package yuzunyannn.elementalsorcery.render.entity;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.crafting.ICraftingCommit;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyannn.elementalsorcery.entity.EntityCrafting;

@SideOnly(Side.CLIENT)
public class AnimeRenderDeconstruct implements ICraftingLaunchAnime {

	// 高度
	public float high = 0.0f;
	public float preHigh = 0.0f;
	// 旋转
	public float roate = 0;
	public float preRoate = 0;

	@Override
	public void doRender(ICraftingCommit commit, double x, double y, double z, float entityYaw, float partialTicks) {
		List<ItemStack> list = commit.getItems();
		if (list == null || list.isEmpty())
			return;
		ItemStack stack = list.get(0);
		float high = this.preHigh + (this.high - this.preHigh) * partialTicks;
		float roate = this.preRoate + (this.roate - this.preRoate) * partialTicks;
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y + 0.5 + high, (float) z);
		yuzunyannn.elementalsorcery.util.render.RenderHelper.layItemPositionFix(stack);
		GlStateManager.rotate(roate, 0, 1, 0);

		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);

		GlStateManager.popMatrix();
	}

	@Override
	public void endEffect(ICraftingCommit commit, World world, BlockPos pos, int flags) {
		if (flags == ICraftingLaunch.SUCCESS)
			EntityCrafting.defaultEndEffect(world, pos);
	}

	@Override
	public void update(ICraftingCommit commit, World world, int endTick) {
		this.preHigh = this.high;
		this.preRoate = this.roate;
		if (endTick >= 0) {

		}
		if (high < 0.5f)
			high += 0.01f;
		this.roate += 1.0f;
	}

}
