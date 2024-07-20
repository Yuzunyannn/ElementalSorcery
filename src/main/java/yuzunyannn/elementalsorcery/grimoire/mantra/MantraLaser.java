package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.IElementLaser;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.ICasterObject;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.mantra.MantraCasterFlags;
import yuzunyannn.elementalsorcery.api.mantra.MantraEffectType;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.target.WorldTarget;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectLaserMantra;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;
import yuzunyannn.elementalsorcery.util.var.Variables;
import yuzunyannn.elementalsorcery.util.world.EntityMasterBinder;

public class MantraLaser extends MantraCommon {

	public static class MantraDataLaser extends MantraDataCommon {

		public final EntityMasterBinder afterTarget = new EntityMasterBinder();

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = super.serializeNBT();
			afterTarget.writeDataToNBT(nbt);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			super.deserializeNBT(nbt);
			afterTarget.readDataFromNBT(nbt);
		}

		@Override
		public NBTTagCompound serializeNBTForSend() {
			NBTTagCompound nbt = super.serializeNBTForSend();
			afterTarget.writeDataToNBT(nbt);
			return nbt;
		}
	}

	public MantraLaser() {
		this.setTranslationKey("laser");
		this.setColor(0xe2feff);
		this.setIcon("laser");
		this.setRarity(-1);
		this.setOccupation(4);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		ElementStack stack = findAnyElementCanUse(caster);
		if (stack.isEmpty()) {
			super.potentAttack(world, grimoire, caster, target);
			return;
		}

		Element element = stack.getElement();
		IElementLaser laser = (IElementLaser) element;
		ElementStack cost = laser.onLaserPotentPreStorage(world);
		if (cost.isEmpty()) {
			super.potentAttack(world, grimoire, caster, target);
			return;
		}

		float potent = caster.iWantBePotent(0.75f, false);
		if (potent < 0.5) {
			super.potentAttack(world, grimoire, caster, target);
			return;
		}

		ElementStack preGet = caster.iWantSomeElement(cost, true);
		if (preGet.isEmpty()) {
			super.potentAttack(world, grimoire, caster, target);
			return;
		}

		preGet.grow((int) (preGet.getCount() * (potent - 0.5f)));
		caster.iWantBePotent(0.5f, true);
		if (world.isRemote) setPotentContextColor(new Color(preGet.getColor()));
		super.potentAttack(world, grimoire, caster, target);
		if (world.isRemote) return;

		VariableSet set = new VariableSet();
		set.set(ELEMENT, stack);
		set.set(Variables.sUUID, target.getUniqueID());
		set.set(Variables.getElementVar(preGet.getElement()), preGet);
		MantraCommon.fireMantra(world, this, caster.iWantCaster().toEntity(), set);
	}

	@Override
	protected void initDirectLaunchMantraGrimoire(EntityGrimoire grimoire, VariableSet params) {
		MantraDataLaser mData = (MantraDataLaser) grimoire.getMantraData();
		mData.afterTarget.setMaster(params.get(Variables.sUUID));
		params.remove(Variables.sUUID);
	}

	@Override
	public IMantraData getData(NBTTagCompound origin, World world, ICaster caster) {
		return new MantraDataLaser();
	}

	@Override
	public int getColor(IMantraData data) {
		if (data == null) return super.getColor(data);
		MantraDataCommon mData = (MantraDataCommon) data;
		if (!mData.has(ELEMENT)) return super.getColor(mData);
		return mData.get(ELEMENT).getColor();
	}

	public ElementStack findAnyElementCanUse(ICaster caster) {
		for (int i = 0; i < 3; i++) {
			ElementStack stack = caster.iWantAnyElementSample(i);
			if (stack.isEmpty()) continue;
			if (stack.getElement() instanceof IElementLaser) return stack;
		}
		return ElementStack.EMPTY;
	}

	protected boolean updateLaser(World world, MantraDataLaser mData, ICaster caster, boolean isAfter) {
		ElementStack eStack = mData.get(ELEMENT);
		if (eStack.isEmpty()) return false;
		Element element = eStack.getElement();
		if (!(element instanceof IElementLaser)) {
			mData.remove(ELEMENT);
			return false;
		}
		mData.set(Variables.TICK, caster.iWantKnowCastTick());
		ElementStack cost = ElementStack.EMPTY;
		IElementLaser laser = (IElementLaser) element;

		ICasterObject directObject = caster.iWantDirectCaster();
		IWorldObject casterObject = caster.iWantRealCaster();
		WorldTarget target = null;

		if (mData.afterTarget.isOwnerless()) {
			target = caster.iWantEntityTarget(Entity.class);
			if (target.getEntity() != null)
				cost = laser.onLaserUpdate(world, casterObject, target, eStack, mData.getExtra());
			else {
				target = caster.iWantBlockTarget();
				if (target.getPos() != null)
					cost = laser.onLaserUpdate(world, casterObject, target, eStack, mData.getExtra());
			}
		} else {
			EntityLivingBase living = mData.afterTarget.tryGetMaster(world);
			if (living == null) return false;
			if (living.isDead) return false;
			directObject.setPositionVector(new Vec3d(living.posX, living.posY + living.height + 1, living.posZ), false);
			Vec3d vec = living.getPositionVector().add(0, living.height / 2, 0);
			target = new WorldTarget(living, vec);
			cost = laser.onLaserUpdate(world, casterObject, target, eStack, mData.getExtra());
		}

		ElementStack get = ElementStack.EMPTY;
		if (!cost.isEmpty()) {
			if (isAfter) {
				ElementStack myStack = mData.get(cost.getElement());
				if (myStack.arePowerfulAndMoreThan(cost)) get = myStack.splitStack(cost.getCount());
			} else get = caster.iWantSomeElement(cost, true);
			if (get.isEmpty()) mData.markContinue(false);
			else {
				float p = caster.iWantBePotent(0.01f, false);
				get.setPower((int) (get.getPower() * (1 + p) + 100 * p));
			}
		}
		if (!mData.isMarkContinue()) return false;
		if (world.isRemote) onSpellingEffect(world, mData, caster, isAfter, target);
		if (target.isEmpty()) return false;
		laser.onLaserExecute(world, casterObject, target, get, mData.getExtra());
		return true;
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mData = (MantraDataCommon) data;
		ElementStack stack = findAnyElementCanUse(caster);
		if (stack.isEmpty()) return;
		mData.set(ELEMENT, stack);
		mData.markContinue(true);
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataLaser mData = (MantraDataLaser) data;
		updateLaser(world, mData, caster, false);
	}

	@Override
	public boolean afterSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataLaser mData = (MantraDataLaser) data;
		if (mData.afterTarget.isOwnerless()) return false;
		mData.markContinue(true);
		return updateLaser(world, mData, caster, true);
	}

	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, MantraDataLaser mData, ICaster caster, boolean isAfter,
			WorldTarget target) {
		addSpellingEffect(world, mData, caster, MantraEffectType.MAGIC_CIRCLE);

		Vec3d at = target.getHitVec();

		IWorldObject wo = isAfter ? caster.iWantDirectCaster() : caster.iWantCaster();
		if (at == Vec3d.ZERO) at = wo.getEyePosition().add(caster.iWantDirection().scale(128));
		EffectLaserMantra effect = mData.getEffectMap().getMark(MantraEffectType.MANTRA_EFFECT_1,
				EffectLaserMantra.class);

		if (effect == null || effect.isDead()) {
			effect = new EffectLaserMantra(world, wo.getEyePosition(), at);
			mData.getEffectMap().addAndMark(MantraEffectType.MANTRA_EFFECT_1, effect);
			effect.magicCircleColor.setColor(getColor(mData));
			effect.color.setColor(getColor(mData)).weight(new Color(0xffffff), 0.75f);
		}

		int tick = caster.iWantKnowCastTick();
		Vec3d from = wo.getEyePosition();
		if (isAfter) {
			double dx = MathHelper.sin(tick / 10f) * 1.25;
			double dz = MathHelper.cos(tick / 10f) * 1.25;
			from = from.add(dx, 0, dz);
		} else {
			double yUp = 0;
			if (!JavaHelper.isTrue(caster.getCasterFlag(MantraCasterFlags.AUTO_MODE))) yUp = 0.75;
			double dx = MathHelper.sin(tick / 10f) * 0.05;
			double dy = MathHelper.cos(tick / 10f) * 0.05;
			Vec3d tar = at.subtract(from).normalize();
			from = from.add(tar.scale(1.25)).add(dx, yUp + dy, dx);
		}

		effect.hold(20);
		effect.toTarget = at;
		effect.toPos = from;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initEffectCreator() {
		super.initEffectCreator();
		setEffectCreator(MantraEffectType.MAGIC_CIRCLE, (world, mantra, mData, caster, effectBinder) -> {
			if (JavaHelper.isTrue(caster.getCasterFlag(MantraCasterFlags.AUTO_MODE))) return null;
			return createEffectMagicCircle(world, mantra, mData, caster, effectBinder);
		}, null);
	}
}
