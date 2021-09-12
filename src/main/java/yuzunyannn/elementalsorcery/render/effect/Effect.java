package yuzunyannn.elementalsorcery.render.effect;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
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

	public double getRenderX(float partialTicks) {
		return this.prevPosX + (this.posX - this.prevPosX) * partialTicks;
	}

	public double getRenderY(float partialTicks) {
		return this.prevPosY + (this.posY - this.prevPosY) * partialTicks;
	}

	public double getRenderZ(float partialTicks) {
		return this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks;
	}

	/** 获取批量渲染的类型，同一种类型获取的实例应该是 单例！ */
	@Nullable
	protected EffectBatchType typeBatch() {
		return null;
	}

	/** 进行渲染,在无EffectBatchType的时候进行调用 */
	protected void doRender(float partialTicks) {

	}

	/** 进行渲染,在有EffectBatchType的时候进行调用 */
	protected void doRender(BufferBuilder bufferbuilder, float partialTicks) {

	}

	// ============-= 全局 =-============

	static final private Set<EffectBatchType> batchs = new HashSet<>();
	static final private ArrayDeque<Effect> effects = new ArrayDeque<>();
	static final private ArrayDeque<Effect> guiEffects = new ArrayDeque<>();
	static final private ArrayDeque<Effect> contextEffects = new ArrayDeque<>();
	static boolean inUpdate = false;

	static public void addEffect(Effect effect) {

		if (effect.asParticle) {
			// 0渲染全部
			int particleSetting = mc.gameSettings.particleSetting;
			if (particleSetting != 0) {
				if (particleSetting == 1) {
					if (rand.nextInt(10) != 0) return;
				} else return;
			}
		}

		if (inUpdate) contextEffects.add(effect);
		else {

			if (effect instanceof IGUIEffect) guiEffects.add(effect);
			else {
				EffectBatchType batch = effect.typeBatch();
				if (batch == null) effects.add(effect);
				else {
					batch.effects.add(effect);
					batchs.add(batch);
				}
			}
		}
	}

	static public void updateAllEffects() {
		update(batchs);
		update(effects);
		update(guiEffects);
		while (!contextEffects.isEmpty()) addEffect(contextEffects.pop());
	}

	static public void updateGuiEffects() {
		update(guiEffects);
		while (!contextEffects.isEmpty()) addEffect(contextEffects.pop());
	}

	static public void clear() {
		batchs.clear();
		effects.clear();
		guiEffects.clear();
		contextEffects.clear();
	}

	static private void update(ArrayDeque<Effect> effects) {
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

	static private void update(Set<EffectBatchType> batchs) {
		Iterator<EffectBatchType> iter = batchs.iterator();
		while (iter.hasNext()) {
			EffectBatchType batch = iter.next();
			update(batch.effects);
			if (batch.effects.isEmpty()) iter.remove();
		}
	}

	static private void renderBatchs(Set<EffectBatchType> batchs, float partialTicks) {
		if (batchs.isEmpty()) return;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		Iterator<EffectBatchType> iter = batchs.iterator();
		while (iter.hasNext()) {
			EffectBatchType batch = iter.next();
			batch.beginRender(tessellator, bufferbuilder);
			for (Effect effect : batch.effects) effect.doRender(bufferbuilder, partialTicks);
			batch.endRender(tessellator, bufferbuilder);
		}

	}

	static private void renderEffects(ArrayDeque<Effect> effects, float partialTicks) {
		for (Effect effect : effects) effect.doRender(partialTicks);
	}

	static public void renderAllEffects(float partialTicks) {
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		RenderHelper.disableStandardItemLighting();
		renderEffects(effects, partialTicks);
		renderBatchs(batchs, partialTicks);
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
		renderEffects(guiEffects, partialTicks);
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

}
