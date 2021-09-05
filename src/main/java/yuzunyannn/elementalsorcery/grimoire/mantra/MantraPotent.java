package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.container.gui.GuiMantraShitf;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.ICasterObject;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon.CollectResult;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.FirewrokShap;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementScrew;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectSpiralMove;
import yuzunyannn.elementalsorcery.util.DamageHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

public class MantraPotent extends MantraCommon {

	public MantraPotent() {
		this.setUnlocalizedName("potent");
		this.setColor(0xffde00);
		this.setIcon("potent");
		this.setRarity(20);
		this.setOccupation(7);
	}

	@Override
	public boolean canPotentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		float potent = caster.iWantBePotent(1f, true);
		if (potent < 0.5) return false;
		return super.canPotentAttack(world, grimoire, caster, target);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		float potent = caster.iWantBePotent(2f, false);
		doPotentAttackEffect(world, caster, target);
		
		EntityLivingBase player = caster.iWantCaster().asEntityLivingBase();
		float damage = DamageHelper.getNormalAttackDamage(player, target) + 1;

		// 非玩家boss生命值下限秒杀
		if (target instanceof EntityLiving && target.isNonBoss()) {
			float hp = ((EntityLiving) target).getHealth();
			if (hp < potent * 30) {
				damage = hp;
				if (world.isRemote) FirewrokShap.createECircleDispersed(world,
						target.getPositionVector().addVector(0, target.height / 2, 0), 1f, 4,
						new int[] { this.getRenderColor() });
			}
		}
		// 超级强效攻击
		potent = (float) Math.pow(potent + 0.5, 1.2) * 1.25f;
		damage = damage * (1 + potent);
		DamageSource ds = DamageHelper.getMagicDamageSource(player, player);
		target.attackEntityFrom(ds, damage);
	}

	@Override
	public void startSpelling(World world, IMantraData mData, ICaster caster) {
		MantraDataCommon data = (MantraDataCommon) mData;
		data.markContinue(true);

		Entity entity = caster.iWantCaster().asEntity();
		if (entity != null) data.markContinue(entity.onGround);
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) data;
		if (!mdc.isMarkContinue()) return;
		int tick = caster.iWantKnowCastTick();

		mdc.setProgress(tick, 20 * 20);
		float progress = mdc.getProgress();
		if (progress < 1) {
			CollectResult cr = mdc.tryCollect(caster, ESInit.ELEMENTS.MAGIC, 8, 65, 3000);
			ElementStack magic = cr.getElementStackGetted();
			if (!magic.isEmpty() && progress > 0.2f) {
				caster.iWantGivePotent(MathHelper.clamp(magic.getPower() / 950f, 0, 0.4f) * progress, 0.005f);
			}
		}

		if (world.isRemote) onSpellingEffect(world, data, caster);
	}

	@Override
	public void endSpelling(World world, IMantraData mData, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) mData;
		if (!mdc.isMarkContinue()) return;
		if (mdc.getProgress() < 1) return;

		ElementStack magic = mdc.get(ESInit.ELEMENTS.MAGIC);
		int count = magic.getCount();
		int power = magic.getPower();

		if (count < 320) return;

		float potent = (float) Math.min(1, Math.pow(power, 1.055) / 1000);
		float point = count / 100f;

		caster.iWantGivePotent(potent, point);
		if (world.isRemote) onEndEffect(world, mData, caster);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		super.onSpellingEffect(world, data, caster);
		MantraDataCommon mdc = (MantraDataCommon) data;
		if (mdc.getProgress() < 0.5f) return;
		if (mdc.get(ESInit.ELEMENTS.MAGIC).getCount() < 320) return;

		Random rand = world.rand;
		Vec3d vec = caster.iWantCaster().getEyePosition();
		Vec3d at = vec.addVector(rand.nextGaussian() * 3, rand.nextGaussian() * 2, rand.nextGaussian() * 3);
		EffectElementScrew screw = new EffectElementScrew(world, at, at);
		screw.lifeTime = 75 + rand.nextInt(50);
		screw.setColor(this.getColor(mdc));
		Effect.addEffect(screw);
	}

	@SideOnly(Side.CLIENT)
	public void onEndEffect(World world, IMantraData mData, ICaster caster) {
		ICasterObject ico = caster.iWantCaster();
		Vec3d vec = ico.getPositionVector();
		Random rand = world.rand;
		for (int i = 0; i < 125; i++) {
			Vec3d at = vec.addVector(rand.nextGaussian() * 3, rand.nextDouble() * 2 + 0.5f, rand.nextGaussian() * 3);
			EffectElementMove effect = new EffectElementMove(world, at);
			effect.motionY = rand.nextGaussian() * 0.2;
			effect.yDecay = 0.75;
			effect.setColor(this.getColor(mData));
			Effect.addEffect(effect);
		}
		for (int i = 0; i < 25; i++) {
			Vec3d at = vec.addVector(rand.nextGaussian() * 3, rand.nextDouble() * 2 + 0.5f, rand.nextGaussian() * 3);
			EffectSpiralMove effect = new EffectSpiralMove(world, at);
			effect.motionY = rand.nextDouble() * 0.01f + 0.005f;
			effect.yDecay = 0.98f;
			effect.alpha = 0;
			effect.setColor(this.getColor(mData));
			Effect.addEffect(effect);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderShiftIcon(Minecraft mc, NBTTagCompound mantraData, float suggestSize, float suggestAlpha,
			float partialTicks) {
		mc.getTextureManager().bindTexture(GuiMantraShitf.CIRCLE);
		RenderHelper.drawTexturedRectInCenter(0, 0, suggestSize, suggestSize);
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(this.icon);
		suggestSize = suggestSize * 0.5f;
		RenderHelper.drawTexturedRectInCenter(0, 0, suggestSize, suggestSize);
	}

}
