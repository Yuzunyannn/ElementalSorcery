package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.render.effect.EffectCondition;

@SideOnly(Side.CLIENT)
public class EffectPlayerAt extends EffectCondition {

	static public final ModelBiped MODEL = new ModelPlayer(0, false);
	static {
		MODEL.isChild = false;
	}

	public boolean isGlow;
	public BlockPos pos;

	public EffectPlayerAt(World world, ICaster caster) {
		super(world);
		this.lifeTime = 1;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}

	@Override
	protected void doRender(float partialTicks) {
		if (pos == null) return;
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null) return;
		double x = pos.getX() + 0.5;
		double y = pos.getY();
		double z = pos.getZ() + 0.5;

		if (isGlow) GlStateManager.depthFunc(519);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/entity/steve.png"));
		GlStateManager.scale(-1, -1, -1);
		GlStateManager.translate(0, -1.5, 0);
		GlStateManager.rotate(-player.rotationYaw + 180, 0, 1, 0);
		GlStateManager.depthMask(true);
		GlStateManager.color(1, 1, 1, 0.5f);
		MODEL.render(player, 0, 0, 0, 0, 0, 0.0625f);
		GlStateManager.depthMask(false);
		GlStateManager.popMatrix();
		if (isGlow) GlStateManager.depthFunc(515);

	}

}
