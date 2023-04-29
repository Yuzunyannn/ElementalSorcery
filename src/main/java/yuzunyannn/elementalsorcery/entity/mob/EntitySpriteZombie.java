package yuzunyannn.elementalsorcery.entity.mob;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.init.LootRegister;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;

public class EntitySpriteZombie extends EntityZombie {

	public EntitySpriteZombie(World worldIn) {
		super(worldIn);

	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (DamageHelper.isRuleDamage(source)) return super.attackEntityFrom(source, amount);
		Entity entity = source.getImmediateSource();
		if (entity instanceof EntityLivingBase) {
			ItemStack stack = ((EntityLivingBase) entity).getHeldItemMainhand();
			if (stack.getItem() == ESObjects.ITEMS.MAGIC_GOLD_SWORD) return super.attackEntityFrom(source, amount / 2);
		}

		if (source.getTrueSource() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) source.getTrueSource();
			if (!DamageHelper.isMagicalDamage(source)) {
				NBTTagCompound datas = ESData.getRuntimeData(player);
				int count = datas.getInteger("spriteZBAttack");
				if (count % 10 == 0) {
					player.sendMessage(new TextComponentTranslation("info.need.magical.damage.attack")
							.setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
				}
				datas.setInteger("spriteZBAttack", count + 1);
				return false;
			}
		}

		return DamageHelper.isMagicalDamage(source) ? super.attackEntityFrom(source, amount) : false;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.extinguish();
		if (world.isRemote) updateEffect();
	}

	@SideOnly(Side.CLIENT)
	public void updateEffect() {
		if (this.ticksExisted % 3 == 0) {
			if (rand.nextBoolean()) return;
			double x = posX + rand.nextGaussian() * this.width * 0.5;
			double y = posY + rand.nextFloat() * this.height;
			double z = posZ + rand.nextGaussian() * this.width * 0.5;
			EffectElementMove effect = new EffectElementMove(world, new Vec3d(x, y, z));
			effect.yAccelerate = -0.0005;
			if (rand.nextBoolean()) effect.setColor(0x00c1e7);
			else effect.setColor(0x007fa2);
			Effect.addEffect(effect);
		}
	}

	@Nullable
	protected ResourceLocation getLootTable() {
		return LootRegister.SPRITE_ZOMBIE;
	}

}
