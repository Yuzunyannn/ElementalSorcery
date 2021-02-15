package yuzunyannn.elementalsorcery.render.effect;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** ES使用的自主particle */
@SideOnly(Side.CLIENT)
public abstract class Effect {

	public static final Minecraft mc = Minecraft.getMinecraft();

	/** 继承该接口的效果认为是gui效果 **/
	static public interface IGUIEffect {

	}

	protected World world;
	/** 是否作为大量的粒子效果，是的话，会根据粒子效果的设置，决定是否展示 */
	public boolean asParticle = false;
	public double prevPosX;
	public double prevPosY;
	public double prevPosZ;
	public double posX;
	public double posY;
	public double posZ;
	/** 生存时间，同时也作为标定是否需要删除的变量 */
	public int lifeTime;

	static public final Random rand = new Random();

	public Effect(World world, double x, double y, double z) {
		this.world = world;
		this.prevPosX = this.posX = x;
		this.prevPosY = this.posY = y;
		this.prevPosZ = this.posZ = z;
		this.lifeTime = 20 + rand.nextInt(40);
	}

	public void onUpdate() {
		this.lifeTime--;
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
	}

	public Vec3d getPositionVector() {
		return new Vec3d(posX, posY, posZ);
	}

	public void setPosition(double x, double y, double z) {
		this.prevPosX = this.posX = x;
		this.prevPosY = this.posY = y;
		this.prevPosZ = this.posZ = z;
	}

	public void setPosition(Vec3d pos) {
		this.prevPosX = this.posX = pos.x;
		this.prevPosY = this.posY = pos.y;
		this.prevPosZ = this.posZ = pos.z;
	}

	public void setPosition(Entity pos) {
		this.prevPosX = this.posX = pos.posX;
		this.prevPosY = this.posY = pos.posY;
		this.prevPosZ = this.posZ = pos.posZ;
	}

	protected abstract void doRender(float partialTicks);

	public double getRenderX(float partialTicks) {
		return this.prevPosX + (this.posX - this.prevPosX) * partialTicks;
	}

	public double getRenderY(float partialTicks) {
		return this.prevPosY + (this.posY - this.prevPosY) * partialTicks;
	}

	public double getRenderZ(float partialTicks) {
		return this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks;
	}

	static final private ArrayDeque<Effect> effects = new ArrayDeque<Effect>();
	static final private ArrayDeque<Effect> guiEffects = new ArrayDeque<Effect>();
	static final private ArrayDeque<Effect> contextEffects = new ArrayDeque<Effect>();
	static private boolean inUpdate = false;

	static public void addEffect(Effect effect) {

		if (effect.asParticle) {
			// 0渲染全部
			if (Minecraft.getMinecraft().gameSettings.particleSetting != 0) {
				// 其他的视形况
				return;
			}
		}

		if (inUpdate) contextEffects.add(effect);
		else {
			if (effect instanceof IGUIEffect) guiEffects.add(effect);
			else effects.add(effect);
		}
	}

	static public void updateAllEffects() {
		update(effects);
		update(guiEffects);
		while (!contextEffects.isEmpty()) addEffect(contextEffects.pop());
	}

	static public void clear() {
		effects.clear();
		guiEffects.clear();
		contextEffects.clear();
	}

	static private void update(ArrayDeque effects) {
		Iterator<Effect> iter = effects.iterator();
		World world = Minecraft.getMinecraft().world;
		inUpdate = true;
		while (iter.hasNext()) {
			Effect effect = iter.next();
			if (effect.lifeTime <= 0 || world != effect.world) {
				iter.remove();
				continue;
			}
			effect.onUpdate();
		}
		inUpdate = false;
	}

	static public void renderAllEffects(float partialTicks) {
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		RenderHelper.disableStandardItemLighting();
		Iterator<Effect> iter = effects.iterator();
		while (iter.hasNext()) {
			Effect effect = iter.next();
			effect.doRender(partialTicks);
		}
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
	}

	static public void renderAllGuiEffects(float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Iterator<Effect> iter = guiEffects.iterator();
		while (iter.hasNext()) {
			Effect effect = iter.next();
			effect.doRender(partialTicks);
		}
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

}
