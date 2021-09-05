package yuzunyannn.elementalsorcery.render.effect.batch;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EffectElementFly extends EffectElement {

	static public final float g = -0.025f;
	static public final double AT_HIGH_SPEED = 0.05;

	// 直线状态
	private boolean straight = true;
	// 上升
	private boolean upward = true;

	// 移动结束
	private boolean isEnd = false;

	// 闪烁标识
	private boolean fadeOut = true;

	private Vec3d ori;
	// 水平朝向目标向量
	private Vec3d tar;
	// 圆环半径
	private double r;
	private double theta = 0;
	private double dtheta = 0;
	private double bottom;

	public EffectElementFly(World world, Vec3d from, Vec3d to) {
		super(world, from.x, from.y, from.z);
		this.scale = rand.nextFloat() * 0.1f + 0.1f;
		// 计算圆弧
		tar = to.subtract(from);
		tar = new Vec3d(tar.x, 0, tar.z);
		r = tar.lengthVector() / 2;
		if (from.y > to.y) {
			// 高到低
			bottom = to.y;
			ori = new Vec3d(from.x, from.y, from.z);
			this.motionY = 0.0;
			upward = false;
			straight = false;
		} else {
			// 低到高
			bottom = from.y;
			ori = new Vec3d(from.x, to.y, from.z);
			double high = to.y - from.y;
			this.motionY = Math.sqrt(AT_HIGH_SPEED * AT_HIGH_SPEED - 2 * g * high);
		}
	}

	public void setEnd() {
		this.isEnd = true;
		this.alpha = 1.0f;
	}

	@Override
	public void onUpdate() {
		if (this.isEnd) {
			super.onUpdate();
		} else this.move();
	}

	private void move() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.prevAlpha = this.alpha;
		this.prevScale = this.scale;

		this.posY += this.motionY;
		if (this.fadeOut) {
			this.alpha -= 0.02;
			if (this.alpha <= 0.5) this.fadeOut = false;
		} else {
			this.alpha += 0.02;
			if (this.alpha >= 0.75) this.fadeOut = true;
		}
		if (this.straight) {
			this.motionY += g;
			if (this.upward) {
				if (this.motionY <= AT_HIGH_SPEED || this.posY > ori.y) {
					this.posY = ori.y;
					this.straight = false;
					this.theta = this.dtheta = AT_HIGH_SPEED / r;
				}
			} else {
				if (this.posY < bottom) this.setEnd();
			}
		} else {
			this.theta += this.dtheta;
			this.dtheta += 0.0025f;
			this.posY = MathHelper.sin((float) this.theta) * r + ori.y;
			double factor = (r - MathHelper.cos((float) this.theta) * r) / (r * 2);
			Vec3d pos = this.ori.add(this.tar.scale(factor));
			this.posX = pos.x;
			this.posZ = pos.z;
			if (this.upward) {
				if (this.theta >= Math.PI) this.setEnd();
			} else {
				if (this.theta >= Math.PI) {
					this.straight = true;
					this.motionY = -this.dtheta * r;
				}
			}
		}
	}
}
