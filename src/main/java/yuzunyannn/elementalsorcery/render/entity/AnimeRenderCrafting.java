package yuzunyannn.elementalsorcery.render.entity;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.crafting.IRecipe;
import yuzunyannn.elementalsorcery.crafting.ICraftingCommit;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyannn.elementalsorcery.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.crafting.altar.CraftingCrafting;
import yuzunyannn.elementalsorcery.entity.EntityCrafting;
import yuzunyannn.elementalsorcery.render.effect.FirewrokShap;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

@SideOnly(Side.CLIENT)
public class AnimeRenderCrafting implements ICraftingLaunchAnime {

	// 目前所出的数量
	public int nowAt = 0;
	// 旋转角度
	public float roate = 0;
	public float preRoate = 0;
	// 出生时时半径比例（0-1）
	public float r = 0;
	public float preR = 0;
	// 旋转范围
	public float range = 0.75f;
	public float preRange = 0.75f;
	// 结束颜色
	public int[] endColors = null;

	@Override
	public void doRender(ICraftingCommit commit, double x, double y, double z, float entityYaw, float partialTicks) {
		List<ItemStack> list = commit.getItems();
		if (list == null) return;
		float range = this.preRange + (this.range - this.preRange) * partialTicks;
		float sp_r = this.preR + (this.r - this.preR) * partialTicks;
		float roate = this.preRoate + (this.roate - this.preRoate) * partialTicks;
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y + 0.5, (float) z);
		GlStateManager.enableRescaleNormal();

		float ty = 1.0f + MathHelper.sin(roate * 0.01745329f) * 0.25f;
		float dr = 360.0f / list.size();
		GlStateManager.translate(0, ty, 0);
		GlStateManager.rotate(roate, 0, 1, 0);

		for (int i = 0; i < this.nowAt; i++) {
			ItemStack stack = list.get(i);
			GlStateManager.translate(range, 0, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
			GlStateManager.translate(-range, 0, 0);
			GlStateManager.rotate(dr, 0, 1, 0);
		}
		if (this.nowAt < list.size()) {
			sp_r = sp_r * sp_r;
			GlStateManager.translate(0, -ty + ty * sp_r, 0);
			ItemStack stack = list.get(this.nowAt);
			GlStateManager.translate(range * sp_r, 0, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
			GlStateManager.translate(-range * sp_r, 0, 0);

		}
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}

	@Override
	public void endEffect(ICraftingCommit commit, World world, BlockPos pos, int flags) {
		if (flags != ICraftingLaunch.SUCCESS) return;
		if (endColors != null) {
			Vec3d position = new Vec3d(pos).add(0.5, 0.5, 0.5);
			FirewrokShap.createECircle(world, position, 0.2, 3, endColors);
			return;
		}
		EntityCrafting.defaultEndEffect(world, pos);
	}

	@Override
	public void update(ICraftingCommit commit, World world, BlockPos pos, int endTick) {
		if (endTick >= 0) {
			this.preRange = this.range;
			this.range = endTick / 20.0f * 0.75f;
		}
		this.preR = this.r;
		this.preRoate = this.roate;
		// 旋转
		this.roate += 1.25f;
		// 物品出来绘图
		if (this.nowAt < commit.getItems().size()) {
			this.r += 0.05;
			if (this.r >= 1.0) {
				this.nowAt++;
				this.r = 0;
			}
		}
		if (endColors == null) {
			if (commit instanceof CraftingCrafting) {
				CraftingCrafting cc = (CraftingCrafting) commit;
				IRecipe recipe = RecipeManagement.instance.findMatchingRecipe(cc.getWorkingInventory(), world);
				if (recipe != null) endColors = ElementHelper.toColor(recipe.getNeedElements());
			}
			if (endColors == null || endColors.length <= 0) endColors = new int[] { 0xe2e2ef, 0xa590de };
		}
	}

}
