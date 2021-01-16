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
import yuzunyannn.elementalsorcery.render.effect.FirewrokShap;

@SideOnly(Side.CLIENT)
public class AnimeRenderConstruct implements ICraftingLaunchAnime {

	public float roate;
	public float preRoate;
	GlStateManager.LogicOp op = GlStateManager.LogicOp.INVERT;

	@Override
	public void doRender(ICraftingCommit commit, double x, double y, double z, float entityYaw, float partialTicks) {
		List<ItemStack> list = commit.getItems();
		if (list == null || list.isEmpty()) return;
		ItemStack stack = list.get(0);
		if (stack.isEmpty()) return;
		if (op == null) return;
		float roate = this.preRoate + (this.roate - this.preRoate) * partialTicks;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1.25f, z);
		GlStateManager.rotate(roate, 0, 1, 0);
		GlStateManager.enableColorLogic();
		GlStateManager.colorLogicOp(op);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.disableColorLogic();
		GlStateManager.popMatrix();
	}

	@Override
	public void endEffect(ICraftingCommit commit, World world, BlockPos pos, int flags) {
		if (flags != ICraftingLaunch.SUCCESS) return;
		Vec3d position = new Vec3d(pos).addVector(0.5, 1.25, 0.5);
		FirewrokShap.createECircle(world, position, 0.2, 3,
				new int[] { 0xe2e2ef, 0xcdcde4, 0x99ecff, 0x5c9ad8, 0x25346e });
	}

	@Override
	public void update(ICraftingCommit commit, World world, BlockPos pos, int endTick) {
		if (endTick > 0) {
			if (endTick % 3 == 0) {
				if (this.op == GlStateManager.LogicOp.INVERT) this.op = GlStateManager.LogicOp.COPY;
				else this.op = GlStateManager.LogicOp.INVERT;
			}
		}
		this.preRoate = this.roate;
		this.roate += 1.0f;
		if (this.roate <= 60) {
			if (((int) this.roate) % 5 == 0) {
				if (this.op == GlStateManager.LogicOp.INVERT) this.op = null;
				else this.op = GlStateManager.LogicOp.INVERT;
			}
		}
	}

}
