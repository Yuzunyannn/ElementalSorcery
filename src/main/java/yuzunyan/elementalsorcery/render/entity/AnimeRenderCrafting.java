package yuzunyan.elementalsorcery.render.entity;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yuzunyan.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyan.elementalsorcery.entity.EntityCrafting;

public class AnimeRenderCrafting implements ICraftingLaunchAnime {

	// 目前所出的数量
	public int now_at = 0;
	// 旋转角度
	public float roate = 0;
	public float preRoate = 0;
	// 出生时时半径比例（0-1）
	public float r = 0;
	public float preR = 0;
	// 旋转范围
	public float range = 0.75f;
	public float preRange = 0.75f;

	@Override
	public void deRender(EntityCrafting entity, double x, double y, double z, float entityYaw, float partialTicks) {
		List<ItemStack> list = entity.getItemList();
		if (list == null)
			return;
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

		for (int i = 0; i < this.now_at; i++) {
			ItemStack stack = list.get(i);
			GlStateManager.translate(range, 0, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
			GlStateManager.translate(-range, 0, 0);
			GlStateManager.rotate(dr, 0, 1, 0);
		}
		if (this.now_at < list.size()) {
			sp_r = sp_r * sp_r;
			GlStateManager.translate(0, -ty + ty * sp_r, 0);
			ItemStack stack = list.get(this.now_at);
			GlStateManager.translate(range * sp_r, 0, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
			GlStateManager.translate(-range * sp_r, 0, 0);

		}
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}

	@Override
	public void endEffect(EntityCrafting entity, World world, BlockPos pos) {
		entity.defaultEndEffect();
	}

	@Override
	public void update(EntityCrafting entity, int endTick) {
		if (endTick >= 0) {
			this.preRange = this.range;
			this.range = endTick / 20.0f * 0.75f;
		}
		this.preR = this.r;
		this.preRoate = this.roate;
		// 旋转
		this.roate += 1.25f;
		// 物品出来绘图
		if (this.now_at < entity.getItemList().size()) {
			this.r += 0.05;
			if (this.r >= 1.0) {
				this.now_at++;
				this.r = 0;
			}
		}
	}

}
