package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.util.client.ESResources;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.elf.ElfChamberOfCommerce;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class MantraGoldShield extends MantraTypePersistent {

	final public static float SUPER_POTENT_POWER = 0.5f;

	public MantraGoldShield() {
		this.setTranslationKey("goldShield");
		this.setColor(0xffe63f);
		this.setIcon("gold_shield");
		this.setRarity(30);
		this.setOccupation(5);
		this.setInterval(5);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.METAL, 1, 100), true);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);
	}

	@Override
	protected void onUpdate(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mData = (MantraDataCommon) data;
		EntityLivingBase entity = caster.iWantRealCaster().asEntityLivingBase();
		if (entity == null) return;

		PotionEffect effct = entity.getActivePotionEffect(ESObjects.POTIONS.GOLD_SHIELD);
		int amplifier = effct == null ? 0 : effct.getAmplifier();
		int lev = getLevelWithElement(mData.get(ESObjects.ELEMENTS.METAL));
		if (effct != null && lev == amplifier && effct.getDuration() > 20) return;
		setGoldShild(entity, lev, 160);
	}

	public static int getLevelWithElement(ElementStack eStack) {
		return Math.min(eStack.getPower() / 128, 16);
	}

	public static void setGoldShild(EntityLivingBase entity, int lev, int duration) {
		PotionEffect effect = entity.getActivePotionEffect(ESObjects.POTIONS.GOLD_SHIELD);
		int amplifier = effect == null ? 0 : effect.getAmplifier();
		if (effect != null && lev < amplifier) entity.removePotionEffect(ESObjects.POTIONS.GOLD_SHIELD);
		entity.addPotionEffect(new PotionEffect(ESObjects.POTIONS.GOLD_SHIELD, duration, lev));
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		EntityLivingBase entity = caster.iWantRealCaster().asEntityLivingBase();
		if (entity == null) return;
		float r = Math.min(1, caster.iWantKnowCastTick() / 160f);
		float potent = caster.iWantBePotent(1, true);
		if (potent <= 0.2 || r < 0.25f) {
			entity.removePotionEffect(ESObjects.POTIONS.GOLD_SHIELD);
			return;
		}
		caster.iWantBePotent(1, false);
		MantraDataCommon mData = (MantraDataCommon) data;
		int lev = getLevelWithElement(mData.get(ESObjects.ELEMENTS.METAL));
		setGoldShild(entity, lev, (int) (320 * (1 + potent) * r));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderShiftIcon(NBTTagCompound mantraData, float suggestSize, float suggestAlpha, float partialTicks) {
		ESResources.MANTRA_COMMON_CIRCLE.bind();
		RenderFriend.drawTexturedRectInCenter(0, 0, suggestSize, suggestSize);
		GlStateManager.color(1, 1, 1, suggestAlpha);
		ResourceLocation res = this.getIconResource();
		if (res == null) res = ESResources.MANTRA_VOID.getResource();
		TextureBinder.bindTexture(res);
		suggestSize = suggestSize * 0.5f;
		RenderFriend.drawTexturedRectInCenter(0, 0, suggestSize, suggestSize);
	}

	public static double getValueCoefficient(int amplifier) {
		return (Math.pow(amplifier, 2) / 20 + 1) * 0.005;
	}

	public static double spItemCoefficient(ItemStack stack) {
		if (stack.getItem().getRegistryName().getPath().toLowerCase().indexOf("gold") != -1) return 0.5f;
		return 0;
	}

	public static double findValue(double coefficient, double demand, IInventory inv, boolean consume) {
		if (inv == null) return 0;
		double valueDmage = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			int price = ElfChamberOfCommerce.priceIt(stack);
			if (price <= 0) continue;
			double p = price * coefficient * (spItemCoefficient(stack) + 1);
			double need = demand - valueDmage;
			int n = Math.min(MathHelper.ceil(need / p), stack.getCount());
			valueDmage += n * p;
			if (consume) stack.shrink(n);
			if (valueDmage >= demand) break;
		}
		return valueDmage;
	}

	public static void setReflect(Entity attacker, Entity src, double dmg) {
		if (attacker == null || attacker == src) {
			if (src != null) setReflect(src.getPositionVector().add(0, src.height / 2, 0), src, dmg);
			return;
		}
		if (attacker.world.isRemote) return;
		EventServer.addWorldTask(attacker.world, (w) -> {
			DamageSource ds = DamageHelper.getMagicDamageSource(src, null);
			ds.setDamageAllowedInCreativeMode();
			if (attacker.attackEntityFrom(ds, (float) dmg)) {
				Effects.spawnTypeEffect(w, attacker.getPositionVector().add(0, attacker.height / 2, 0), 2,
						(byte) Math.min(16, attacker.width));
			} else {
				// 打不了，只能反弹了
				setReflect(attacker.getPositionVector().add(0, attacker.height / 2, 0), attacker, dmg);
			}
		});
	}

	public static void setReflect(Vec3d vec, Entity src, double dmg) {
		if (src == null) return;
		if (src.world.isRemote) return;
		EventServer.addWorldTask(src.world, (w) -> {
			DamageSource ds = DamageHelper.getMagicDamageSource(src, null);
			ds.setDamageAllowedInCreativeMode();
			AxisAlignedBB aabb = WorldHelper.createAABB(vec, 3, 1.5, 2.5);
			List<EntityLivingBase> entities = w.getEntitiesWithinAABB(EntityLivingBase.class, aabb, e -> {
				return !EntityHelper.isSameTeam(src, e);
			});
			for (EntityLivingBase entity : entities) entity.attackEntityFrom(ds, (float) dmg / entities.size());
			Effects.spawnTypeEffect(src.world, vec, 2, (byte) 5);
		});

	}

}
