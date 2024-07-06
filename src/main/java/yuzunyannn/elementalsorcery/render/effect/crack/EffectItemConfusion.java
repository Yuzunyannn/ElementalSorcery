package yuzunyannn.elementalsorcery.render.effect.crack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.render.effect.Effect;

@SideOnly(Side.CLIENT)
public class EffectItemConfusion extends Effect {

	public EntityItem entityItem;
	public ItemStack stack = ItemStack.EMPTY;
	public float scale = 1;

	public EffectItemConfusion(World world, Vec3d pos, ItemStack stack) {
		super(world, pos.x, pos.y, pos.z);
		this.stack = stack;
		this.lifeTime = 20;
	}

	public EffectItemConfusion(World world, EntityItem entityItem) {
		super(world, entityItem.posX, entityItem.posY, entityItem.posZ);
		this.stack = entityItem.getItem();
		this.entityItem = entityItem;
		this.lifeTime = 20;
	}

	@Override
	protected String myGroup() {
		return EffectBlockConfusion.GROUP_CONFUSION;
	}

	@Override
	public void onUpdate() {
		this.lifeTime--;
		if (entityItem != null) {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.posX = entityItem.posX;
			this.posY = entityItem.posY + 0.25;
			this.posZ = entityItem.posZ;
		}
	}

	@Override
	protected void doRender(float partialTicks) {
		GlStateManager.pushMatrix();
		double x = getRenderX(partialTicks);
		double y = getRenderY(partialTicks);
		double z = getRenderZ(partialTicks);
		GlStateManager.translate(x, y, z);
		if (this.entityItem != null) {
			float yoff = MathHelper.sin((entityItem.getAge() + partialTicks) / 10.0F + entityItem.hoverStart) * 0.1F
					+ 0.1F;
			GlStateManager.translate(0, yoff, 0);
			float f3 = ((entityItem.getAge() + partialTicks) / 20.0F + entityItem.hoverStart)
					* (180F / (float) Math.PI);
			GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
		}
		GlStateManager.scale(scale, scale, scale);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.popMatrix();

		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		RenderFriend.disableLightmap(true);
	}

}
