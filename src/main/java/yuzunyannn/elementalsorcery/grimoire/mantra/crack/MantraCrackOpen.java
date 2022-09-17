package yuzunyannn.elementalsorcery.grimoire.mantra.crack;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.ElementTransition;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.mantra.MantraEffectType;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet.Variable;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraCommon;
import yuzunyannn.elementalsorcery.item.prop.ItemElementCrack;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.crack.EffectCylinderCrackBlast;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class MantraCrackOpen extends MantraCrackCommon {

	public static void attack(World world, BlockPos at, double size, Entity caster, double startCost,
			boolean dropCrack) {
		MantraCrackCommon mantra = (MantraCrackCommon) ESObjects.MANTRAS.ECRACK_OPEN;
		VariableSet set = new VariableSet();
		set.set(FRAGMENT, size / 6 * mantra.getMaxFragment());
		set.set(VEC, new Vec3d(at).add(0.5, 0.5, 0.5));
		set.set(COST, startCost);
		if (dropCrack) set.set(DROP_CRACK, true);
		MantraCommon.fireMantra(world, mantra, caster, set);
	}

	public static final Variable<Boolean> DROP_CRACK = new Variable<>("dCrack", VariableSet.BOOL);
	public static final Variable<Double> COST = new Variable<>("cost", VariableSet.DOUBLE);
	public static final Variable<NBTTagCompound> SERVER_SLOW_DATA = new Variable<>("slow", VariableSet.NBT_TAG);

	public MantraCrackOpen() {
		this.setTranslationKey("eCrackOpen");
		this.setRarity(-1);
		this.setMaxFragment(ElementTransition.toMagicFragment(new ElementStack(ESObjects.ELEMENTS.STAR, 400, 500)));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		addEffectProgress(world, data, caster);
		addEffectIndicatorEffect(world, data, caster);
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		WorldTarget wTarget = caster.iWantBlockTarget();
		if (wTarget.isEmpty()) {
			MantraDataCommon mdc = (MantraDataCommon) data;
			mdc.markContinue(false);
			return;
		}
		Vec3d vec = new Vec3d(wTarget.getPos()).add(0.5, 0.5, 0.5);
		caster.iWantDirectCaster().setPositionVector(vec);
		sendMantraDataToClient(world, data, caster);
	}

	@Override
	public boolean afterSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) data;
		double fragment = mdc.get(FRAGMENT);
		double cost = (mdc.get(COST) + 0.1) * 1.05;
		mdc.set(COST, cost);
		if (fragment > cost) fragment = fragment - cost;
		else fragment = fragment / 2;
		NBTTagCompound sld = mdc.has(SERVER_SLOW_DATA) ? mdc.get(SERVER_SLOW_DATA) : null;
		if (sld != null) mdc.remove(SERVER_SLOW_DATA);
		if (fragment < 0.000000001) {
			if (world.isRemote) {
				addAndUpdateEffectCrackOpen(world, data, caster);
				return true;
			}
			if (sld == null) {
				if (mdc.get(DROP_CRACK)) {
					ItemStack elementCrack = new ItemStack(ESObjects.ITEMS.ELEMENT_CRACK, 1);
					EntityItem entityitem = ItemHelper.dropItem(world, caster.iWantDirectCaster().getPositionVector(),
							elementCrack);
					entityitem.motionX = entityitem.motionY = entityitem.motionZ = 0;
					entityitem.velocityChanged = true;
				}
				return false;
			}
		}
		cost = mdc.get(FRAGMENT) - fragment;
		mdc.set(FRAGMENT, fragment);
		double lastSize = mdc.get(SIZED);
		mdc.set(SIZED, Math.min(lastSize + cost / 750000, 128));
		if (world.isRemote) {
			addAndUpdateEffectCrackOpen(world, data, caster);
			return true;
		}
		double nowSize = mdc.get(SIZED);
		int oldSize = (int) lastSize;
		int size = (int) nowSize;
		int originSize = size;
		if (oldSize < size || sld != null) notFin: {
			if (sld != null) {
				if (sld.hasKey("s")) size = sld.getInteger("s");
				else sld = null;
			}
			BlockPos center = caster.iWantDirectCaster().getPosition();
			BlockPos pos = new BlockPos(center.getX(), 0, center.getZ());
			int rx = MathHelper.ceil(size);
			long startTime = System.currentTimeMillis();
			for (int x = sld != null ? sld.getInteger("x") : -rx; x <= rx; x++) {
				int rz = MathHelper.ceil(Math.cos(Math.asin(x / (double) rx)) * rx);
				for (int z = sld != null ? sld.getInteger("z") : -rz; z <= rz; z++) {
					sld = null;
					for (int y = 0; y < 256; y++) {
						BlockPos at = pos.add(x, y, z);
						if (BlockHelper.isBedrock(world, at)) continue;
						if (world.isAirBlock(at)) continue;
						world.setBlockState(at, Blocks.AIR.getDefaultState());
					}
					// 都20毫秒了，行吧，别算了，下次再算
					if (System.currentTimeMillis() - startTime > 16) {
						NBTTagCompound dat = new NBTTagCompound();
						dat.setInteger("s", size);
						dat.setInteger("x", x);
						dat.setInteger("z", z);
						mdc.set(SERVER_SLOW_DATA, dat);
						break notFin;
					}
				}
			}
			if (originSize != size) mdc.set(SERVER_SLOW_DATA, new NBTTagCompound());
		}
		if (caster.iWantKnowCastTick() % 5 == 0) {
			BlockPos center = caster.iWantDirectCaster().getPosition();
			AxisAlignedBB aabb = WorldHelper.createAABB(center, nowSize, 256, 256);
			List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb, e -> {
				double dx = e.posX - center.getX() - 0.5;
				double dz = e.posZ - center.getZ() - 0.5;
				double length = MathHelper.sqrt(dx * dx + dz * dz);
				return length - e.width / 2 <= nowSize;
			});
			for (Entity entity : entities) {
				ItemElementCrack.crackAttack(world, entity, caster.iWantCaster().asEntity());
				if (!EntityHelper.isCreative(entity)) {
					Vec3d target = new Vec3d(center).subtract(entity.getPositionVector()).normalize();
					entity.motionX += target.x;
					entity.motionZ += target.z;
					entity.velocityChanged = true;
				}
				if (entity instanceof EntityLivingBase && !ESAPI.isDevelop) {
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100));
				}
			}
		}

		return true;

	}

	@SideOnly(Side.CLIENT)
	public void addAndUpdateEffectCrackOpen(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) data;
		EffectCylinderCrackBlast effect = mdc.getEffectMap().getMark(MantraEffectType.MANTRA_EFFECT_1,
				EffectCylinderCrackBlast.class);
		if (effect != null) {
			effect.hold();
			effect.targetScale = mdc.get(SIZED);
			return;
		}
		effect = new EffectCylinderCrackBlast(world, caster.iWantDirectCaster().getPositionVector());
		mdc.getEffectMap().addAndMark(MantraEffectType.MANTRA_EFFECT_1, effect);
	}

}
