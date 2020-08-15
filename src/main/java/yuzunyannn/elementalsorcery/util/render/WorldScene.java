package yuzunyannn.elementalsorcery.util.render;

import java.lang.ref.WeakReference;

import org.lwjgl.util.glu.Project;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WorldScene {

	private static boolean inRender = false;
	private Framebuffer buffer;
	private WeakReference<Entity> looker = new WeakReference(null);
	private Minecraft mc = Minecraft.getMinecraft();

	public WorldScene(int width, int height) {
		buffer = new Framebuffer(width, height, true);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		buffer.dispose();
	}

	public void dispose() {
		buffer.dispose();
	}

	public void resize(int width, int height) {
		buffer.resize(width, height);
	}

	public Framebuffer getFramebuffer() {
		return buffer;
	}

	public void setLooker(Entity looker) {
		if (this.looker != null && this.looker.get() == looker) return;
		this.looker = new WeakReference(looker);
	}

	public boolean doRenderWorld() {
		if (Minecraft.getMinecraft().world == null) return false;
		if (inRender) return false;
		Framebuffer buffer = this.buffer;
		inRender = true;
		GlStateManager.pushMatrix();

		Entity originViewEntity = mc.getRenderViewEntity();
		mc.setRenderViewEntity(looker.get());
		int originThirdPersonView = mc.gameSettings.thirdPersonView;
		mc.gameSettings.thirdPersonView = 0;
		boolean originHideUI = mc.gameSettings.hideGUI;
		mc.gameSettings.hideGUI = true;

		WorldSceneEventHandle.instance.push(this);
		buffer.bindFrame();
		mc.entityRenderer.renderWorld(mc.getRenderPartialTicks(), System.nanoTime());
		buffer.unbindFrame();
		WorldSceneEventHandle.instance.pop();

		mc.gameSettings.hideGUI = originHideUI;
		mc.gameSettings.thirdPersonView = originThirdPersonView;
		mc.setRenderViewEntity(originViewEntity);

		GlStateManager.popMatrix();
		inRender = false;
		return true;
	}

	public boolean doRenderSky() {
		buffer.bindFrame();
		GlStateManager.clear(16640);
		float partialTicks = mc.getRenderPartialTicks();

		Entity origin = mc.getRenderViewEntity();
		mc.setRenderViewEntity(looker.get());
		float f = mc.gameSettings.renderDistanceChunks * 16;

		GlStateManager.pushMatrix();
		setupCameraTransform(partialTicks);

		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		Project.gluPerspective(70, (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, f * 2.0F);
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();

		Entity entity = this.mc.getRenderViewEntity();
		double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
		double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
		double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
		mc.renderGlobal.renderSky(partialTicks, 2);
		mc.renderGlobal.renderClouds(partialTicks, 2, d0, d1, d2);

		GlStateManager.matrixMode(5889);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();

		GlStateManager.disableFog();
		GlStateManager.popMatrix();

		mc.setRenderViewEntity(origin);
		buffer.unbindFrame();
		return true;
	}

	public void bind() {
		buffer.bindTexture();
	}

	public void unbind() {
		buffer.unbindTexture();
	}

	public static void init() {
		MinecraftForge.EVENT_BUS.register(WorldSceneEventHandle.instance);
	}

	public static class WorldSceneEventHandle {

		final static WorldSceneEventHandle instance = new WorldSceneEventHandle();

		private int i = 0;

		public void push(WorldScene scene) {
			i++;
		}

		public void pop() {
			i--;
		}

		public boolean inScene() {
			return i > 0;
		}

		@SubscribeEvent
		public void fixedFOV(EntityViewRenderEvent.FOVModifier event) {
			if (this.inScene()) event.setFOV(70);
		}

	}

	public float getFOVModifier(float partialTicks, boolean useFOVSetting) {
		Entity entity = this.mc.getRenderViewEntity();
		float f = 70.0F;
		if (useFOVSetting) f = this.mc.gameSettings.fovSetting;
		IBlockState iblockstate = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);
		if (iblockstate.getMaterial() == Material.WATER) f = f * 60.0F / 70.0F;
		return f;
	}

	public void hurtCameraEffect(float partialTicks) {
		if (this.mc.getRenderViewEntity() instanceof EntityLivingBase) {
			EntityLivingBase entitylivingbase = (EntityLivingBase) this.mc.getRenderViewEntity();
			float f = (float) entitylivingbase.hurtTime - partialTicks;
			if (entitylivingbase.getHealth() <= 0.0F) {
				float f1 = (float) entitylivingbase.deathTime + partialTicks;
				GlStateManager.rotate(40.0F - 8000.0F / (f1 + 200.0F), 0.0F, 0.0F, 1.0F);
			}
			if (f < 0.0F) return;
			f = f / (float) entitylivingbase.maxHurtTime;
			f = MathHelper.sin(f * f * f * f * (float) Math.PI);
			float f2 = entitylivingbase.attackedAtYaw;
			GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-f * 14.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
		}
	}

	private void orientCamera(float partialTicks) {
		Entity entity = this.mc.getRenderViewEntity();
		float f = entity.getEyeHeight();
		GlStateManager.translate(0.0F, 0.0F, 0.05F);
		if (!this.mc.gameSettings.debugCamEnable) {
			float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F;
			float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
			float roll = 0.0F;
			if (entity instanceof EntityAnimal) {
				EntityAnimal entityanimal = (EntityAnimal) entity;
				yaw = entityanimal.prevRotationYawHead
						+ (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0F;
			}
			IBlockState state = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);
			CameraSetup event = new CameraSetup(mc.entityRenderer, entity, state, partialTicks, yaw, pitch, roll);
			net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
			GlStateManager.rotate(event.getRoll(), 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(event.getPitch(), 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(event.getYaw(), 0.0F, 1.0F, 0.0F);
		}
		GlStateManager.translate(0.0F, -f, 0.0F);
	}

	public void setupCameraTransform(float partialTicks) {
		float farPlaneDistance = (float) (this.mc.gameSettings.renderDistanceChunks * 16);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		Project.gluPerspective(this.getFOVModifier(partialTicks, true),
				(float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F,
				farPlaneDistance * MathHelper.SQRT_2);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		this.hurtCameraEffect(partialTicks);
		float f1 = this.mc.player.prevTimeInPortal
				+ (this.mc.player.timeInPortal - this.mc.player.prevTimeInPortal) * partialTicks;
		if (f1 > 0.0F) {
			float f2 = 5.0F / (f1 * f1 + 5.0F) - f1 * 0.04F;
			f2 = f2 * f2;
		}
		this.orientCamera(partialTicks);
	}

}
