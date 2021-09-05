package yuzunyannn.elementalsorcery.render.effect.batch;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EffectElementScrew extends EffectElement {

	public Vec3d to;

	public Vec3d a;
	public Vec3d b;
	public float prevRotate;
	public float rotate = 0;
	public double prevR;
	public double r = 5;
	public float minR = 1;

	public EffectElementScrew(World world, Vec3d from, Vec3d to) {
		super(world, from.x, from.y, from.z);
		this.lifeTime = 125;
		this.dalpha = 1.0f / this.lifeTime * 0.75f;
		this.to = to;
		this.scale = rand.nextFloat() * 0.1f + 0.1f;
		this.setNormal(new Vec3d(0, 1, 0));
	}

	public EffectElementScrew setMinRadius(float r) {
		minR = Math.max(0, r);
		return this;
	}

	public EffectElementScrew setDirect(Vec3d dir) {
		Vec3d tar = to.subtract(this.getPositionVector());
		a = tar.normalize();
		b = dir.crossProduct(tar);
		b = b.crossProduct(tar).normalize();
		rotate = 3.1415f;
		r = tar.lengthVector();
		this.prevR = this.r;
		this.prevRotate = this.rotate;
		return this;
	}

	public EffectElementScrew setNormal(Vec3d normal) {
		Vec3d n = normal.normalize();
		a = n.crossProduct(new Vec3d(1, 0, 0));
		if (a.lengthSquared() == 0) a = n.crossProduct(new Vec3d(0, 1, 0));
		b = n.crossProduct(a);
		a = a.normalize();
		b = b.normalize();
		return this;
	}

	@Override
	public void onUpdate() {
		this.prevAlpha = this.alpha;
		this.prevScale = this.scale;
		
		this.alpha -= this.dalpha;
		this.lifeTime--;

		this.prevR = this.r;
		this.prevRotate = this.rotate;

		this.rotate += 0.1f;
		if (r > minR) r -= 0.05f;
	}

	@Override
	protected void doRender(float partialTicks) {
		float rotate = this.prevRotate + (this.rotate - this.prevRotate) * partialTicks;
		double r = this.prevR + (this.r - this.prevR) * partialTicks;
		double x = to.x + r * MathHelper.cos(rotate) * a.x + r * MathHelper.sin(rotate) * b.x;
		double y = to.y + r * MathHelper.cos(rotate) * a.y + r * MathHelper.sin(rotate) * b.y;
		double z = to.z + r * MathHelper.cos(rotate) * a.z + r * MathHelper.sin(rotate) * b.z;
		doRender(x, y, z, partialTicks);
	}

	@Override
	protected void doRender(BufferBuilder bufferbuilder, float partialTicks) {
		float rotate = this.prevRotate + (this.rotate - this.prevRotate) * partialTicks;
		double r = this.prevR + (this.r - this.prevR) * partialTicks;
		double x = to.x + r * MathHelper.cos(rotate) * a.x + r * MathHelper.sin(rotate) * b.x;
		double y = to.y + r * MathHelper.cos(rotate) * a.y + r * MathHelper.sin(rotate) * b.y;
		double z = to.z + r * MathHelper.cos(rotate) * a.z + r * MathHelper.sin(rotate) * b.z;
		doRender(bufferbuilder, x, y, z, partialTicks);
	}

}
