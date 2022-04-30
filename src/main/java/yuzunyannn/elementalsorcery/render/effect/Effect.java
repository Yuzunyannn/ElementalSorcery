package yuzunyannn.elementalsorcery.render.effect;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
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

	public static final String GROUP_GUI = "gui";
	public static final String GROUP_NORMAL = "normal";
	public static final String GROUP_BATCH = "batch";

	public static final Minecraft mc = Minecraft.getMinecraft();
	public static int displayWidth = mc.displayWidth;
	public static int displayHeight = mc.displayHeight;
	public static boolean displayChange;

	public final World world;
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

	public boolean isDead() {
		// if (mc.getRenderViewEntity() == null) return true;
		// float distance = mc.gameSettings.renderDistanceChunks * 16;
		// double renderDistance =
		// mc.getRenderViewEntity().getPositionVector().squareDistanceTo(getPositionVector());
		// if (renderDistance > distance * distance) return true;
		return this.lifeTime <= 0;
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

	protected String myGroup() {
		return typeBatch() == null ? GROUP_NORMAL : GROUP_BATCH;
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

	/** 是否需要生成 */
	protected boolean canPassSpawn() {
		if (!asParticle) return false;
		// 0渲染全部
		int particleSetting = mc.gameSettings.particleSetting;
		if (particleSetting != 0) {
			if (particleSetting == 1) {
				if (rand.nextInt(10) != 0) return true;
			} else return true;
		}
		return false;
	}

	// ============-= 全局 =-============

	static final protected EffectGroup effectBatch = new EffectBatchSet();
	static final protected EffectGroup effectNormal = new EffectList();
	static final protected EffectGroup effectGUI = new EffectListGUI();

	static final public Map<String, EffectGroup> groupMap = new HashMap<>();
	static final private List<EffectGroup> worldGroupList = new ArrayList<>();
	static final private List<EffectGroup> guiGroupList = new ArrayList<>();
	static {
		addEffectGroup(GROUP_BATCH, effectBatch);
		addEffectGroup(GROUP_NORMAL, effectNormal);
		addEffectGroup(GROUP_GUI, effectGUI);
	}

	static public void addEffectGroup(String id, EffectGroup group) {
		groupMap.put(id, group);
		if (group.isGUI()) guiGroupList.add(group);
		else worldGroupList.add(group);
	}

	/** 運行effect時添加effect的容器 */
	static final private ArrayDeque<Effect> contextEffects = new ArrayDeque<>();
	static boolean inUpdate = false;

	static public void addEffect(Effect effect) {
		if (effect.canPassSpawn()) return;
		if (inUpdate) contextEffects.add(effect);
		else {
			String groupId = effect.myGroup();
			EffectGroup group = groupMap.get(groupId);
			if (group == null) return;
			group.add(effect);
		}
	}

	static public void clear() {
		for (EffectGroup group : groupMap.values()) group.clear();
		contextEffects.clear();
	}

	static private void updateProps() {
		displayChange = false;
		if (displayWidth != mc.displayWidth || displayHeight != mc.displayHeight) {
			displayWidth = mc.displayWidth;
			displayHeight = mc.displayHeight;
			displayChange = true;
		}
	}

	static public void updateAllEffects() {
		updateProps();
		inUpdate = true;
		for (EffectGroup group : worldGroupList) group.update();
		for (EffectGroup group : guiGroupList) group.update();
		inUpdate = false;
		while (!contextEffects.isEmpty()) addEffect(contextEffects.pop());
	}

	static public void updateGuiEffects() {
		inUpdate = true;
		for (EffectGroup group : guiGroupList) group.update();
		inUpdate = false;
		while (!contextEffects.isEmpty()) addEffect(contextEffects.pop());
	}

	static public void renderAllEffects(float partialTicks) {
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.enableNormalize();
		RenderHelper.disableStandardItemLighting();
		yuzunyannn.elementalsorcery.util.render.RenderHelper.disableLightmap(true);
		for (EffectGroup group : worldGroupList) group.render(partialTicks);
		yuzunyannn.elementalsorcery.util.render.RenderHelper.disableLightmap(false);
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
		for (EffectGroup group : guiGroupList) group.render(partialTicks);
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

}
