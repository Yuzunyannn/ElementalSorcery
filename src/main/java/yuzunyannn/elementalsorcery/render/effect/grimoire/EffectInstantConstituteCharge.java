package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public class EffectInstantConstituteCharge extends Effect {

	public final EnumFacing facing;
	public double motion;
	public final Color color = new Color();

	public EffectInstantConstituteCharge(World worldIn, BlockPos pos, EnumFacing facing) {
		super(worldIn, pos.getX(), pos.getY() + 1f / 16, pos.getZ());
		this.facing = facing;
		this.lifeTime = 60;
		this.motion = 0.3 + rand.nextFloat() * 0.2f;
		if (this.facing.getXOffset() != 0) {
			this.posX += 0.5f;
			this.posZ += 4 * (rand.nextInt(3) + 1) / 16f;
		} else {
			this.posZ += 0.5f;
			this.posX += 4 * (rand.nextInt(3) + 1) / 16f;
		}
	}

	@Override
	public void onUpdate() {
		this.lifeTime--;
		final int sTick = 59;
		float maxLen = 0.0625f;
		float w = 2f / 16f;
		double more = maxLen / motion;
		if (this.lifeTime == sTick) {
			EffectBlockLine effect = new EffectBlockLine(world, new Vec3d(posX, posY + 0.001f, posZ));
			effect.color.setColor(color);
			effect.toFacing = facing;
			effect.distance = 0.5;
			effect.width = w;
			effect.motion = motion;
			effect.maxLength = maxLen;
			Effect.addEffect(effect);
		} else if (this.lifeTime == (int) (sTick - 0.5 / motion + more)) {
			EffectBlockLine effect = new EffectBlockLine(world,
					new Vec3d(posX + this.facing.getXOffset() * 0.501, posY, posZ + this.facing.getZOffset() * 0.501));
			effect.color.setColor(color);
			effect.toFacing = EnumFacing.DOWN;
			effect.flip = this.facing.getZOffset() == 0;
			effect.distance = 1 + 0.0625;
			effect.width = w;
			effect.motion = motion;
			effect.maxLength = maxLen;
			Effect.addEffect(effect);
		} else if (this.lifeTime == (int) (sTick - (1.5625) / motion + more * 2)) {
			EffectBlockLine effect = new EffectBlockLine(world, new Vec3d(posX + this.facing.getXOffset() * 0.5,
					posY - 1 - 1f / 16 + 0.01f, posZ + this.facing.getZOffset() * 0.5));
			effect.color.setColor(color);
			effect.toFacing = facing;
			effect.distance = 5;
			effect.width = w;
			effect.motion = motion;
			effect.maxLength = maxLen;
			Effect.addEffect(effect);
		} else if (this.lifeTime == (int) (sTick - (6.5625) / motion + more * 3)) {
			EffectBlockLine effect = new EffectBlockLine(world, new Vec3d(posX + this.facing.getXOffset() * 5.499,
					posY - 1 - 1f / 16, posZ + this.facing.getZOffset() * 5.499));
			effect.color.setColor(color);
			effect.toFacing = EnumFacing.UP;
			effect.flip = this.facing.getZOffset() == 0;
			effect.distance = 1.0625;
			effect.width = w;
			effect.motion = motion;
			effect.maxLength = maxLen;
			Effect.addEffect(effect);
		} else if (this.lifeTime == (int) (sTick - (7.625) / motion + more * 4)) {
			EffectBlockLine effect = new EffectBlockLine(world, new Vec3d(posX + this.facing.getXOffset() * 5.5,
					posY + 0.001f, posZ + this.facing.getZOffset() * 5.5));
			effect.color.setColor(color);
			effect.toFacing = facing;
			effect.distance = 0.5;
			effect.width = w;
			effect.motion = motion;
			effect.maxLength = maxLen;
			Effect.addEffect(effect);
		}
	}

}
