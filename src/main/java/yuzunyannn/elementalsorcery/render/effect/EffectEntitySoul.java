package yuzunyannn.elementalsorcery.render.effect;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class EffectEntitySoul extends Effect {

	public static void show(World world, Vec3d pos, NBTTagCompound nbt) {
		int id = nbt.getInteger("deader");
		int kId = nbt.getInteger("killer");
		Entity deader = world.getEntityByID(id);
		Entity killer = world.getEntityByID(kId);
		if (deader instanceof EntityLivingBase) {
			EffectEntitySoul es = new EffectEntitySoul(world, (EntityLivingBase) deader);
			if (killer != null) es.setBinder(killer);
			Effect.addEffect(es);
		}
	}

	public IBinder binder;
	public ModelBase model;
	public EntityLivingBase entity;
	public static final TextureBinder TEXTURE = new TextureBinder("textures/entity/soul_tex.png");

	public float modelYoff = 0;
	public float alpha = 1;
	public float prevAlpha = alpha;

	public EffectEntitySoul(World world, EntityLivingBase entity) {
		super(world, entity.posX, entity.posY, entity.posZ);

		Render<?> render = mc.getRenderManager().getEntityRenderObject(entity);
		if (render instanceof RenderLiving) {
			RenderLiving rl = (RenderLiving) render;
			this.entity = entity;
			this.model = rl.getMainModel();
			if (render instanceof RenderEnderman) modelYoff = -2;
		}
		this.posY = this.posY + modelYoff;
	}

	public void setBinder(Entity entity) {
		binder = new IBinder.EntityBinder(entity, entity.height / 2);
	}

	public boolean isCanRender() {
		return model != null;
	}

	@Override
	public void onUpdate() {
		if (!this.isCanRender()) {
			this.lifeTime = 0;
			return;
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.prevAlpha = this.alpha;

		this.lifeTime--;
		this.alpha = Math.min(lifeTime, 20) / 20f;

		if (this.binder != null) {
			Vec3d to = this.binder.getPosition();
			Vec3d at = this.getPositionVector().add(0, -modelYoff, 0);
			Vec3d tar = to.subtract(at);
			float f = (40 - Math.min(lifeTime, 40f)) / 40f;
			tar = tar.scale(f * f);
			this.posX += tar.x;
			this.posY += tar.y;
			this.posZ += tar.z;
		}

	}

	@Override
	protected void doRender(float partialTicks) {
		if (model == null) return;
		GlStateManager.pushMatrix();
		double x = this.getRenderX(partialTicks);
		double y = this.getRenderY(partialTicks);
		double z = this.getRenderZ(partialTicks);
		float alpha = RenderHelper.getPartialTicks(this.alpha, this.prevAlpha, partialTicks);
		GlStateManager.color(1, 1, 1, 0.5f * alpha);
		GlStateManager.translate(x, y, z);

		GlStateManager.scale(-1, -1, -1);
		GlStateManager.translate(0, -entity.height, 0);
		float gr = EventClient.getGlobalRotateInRender(partialTicks) * 0.75f;
		float r = MathHelper.sin(gr / 180 * 3.14f * 60) * 5;
		TEXTURE.bind();
		model.render(entity, gr, 0.25f, EventClient.globalRotate, r, 0, 0.0625F);

		GlStateManager.popMatrix();
	}

}
