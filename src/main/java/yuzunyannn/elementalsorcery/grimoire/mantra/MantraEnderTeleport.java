package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon.ConditionEffect;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectFlags;
import yuzunyannn.elementalsorcery.grimoire.WantedTargetResult;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectLookAt;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectPlayerAt;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.world.CasterHelper;

public class MantraEnderTeleport extends MantraCommon {

	public MantraEnderTeleport() {
		this.setTranslationKey("enderTeleport");
		this.setColor(0xc000eb);
		this.setIcon("teleport");
		this.setRarity(40);
		this.setOccupation(5);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);
		if (!target.isNonBoss()) return;

		BlockPos pos = caster.iWantFoothold();
		if (pos == null) return;

		ElementStack stack = getElement(caster, ESInit.ELEMENTS.ENDER, 10, 40);
		if (stack.isEmpty()) return;

		doEnderTeleportWithDrown(world, target, new Vec3d(pos).add(0.5, 0, 0.5));
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		Entity entity = caster.iWantCaster().asEntity();
		if (entity == null) return;

		MantraDataCommon dataEffect = (MantraDataCommon) data;
		ElementStack need = new ElementStack(ESInit.ELEMENTS.ENDER, 10, 50);
		ElementStack stack = caster.iWantSomeElement(need, false);
		dataEffect.markContinue(!stack.isEmpty());
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) data;
		mdc.setProgress(caster.iWantKnowCastTick(), 5);
		super.onSpelling(world, data, caster);
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) data;
		if (mdc.getProgress() < 1) return;

		Entity entity = caster.iWantCaster().asEntity();
		if (entity == null) return;

		boolean needSuper = checkCanBeSuper(caster, entity);

		BlockPos pos = findFoothold(world, caster, needSuper);
		if (pos == null) return;

		ElementStack stack = getElement(caster, ESInit.ELEMENTS.ENDER, 10, 50);
		if (stack.isEmpty()) return;

		if (needSuper) caster.iWantBePotent(0.75f, false);

		doEnderTeleportWithDrown(world, entity, new Vec3d(pos).add(0.5, 0, 0.5));
	}

	public static BlockPos findFoothold(World world, ICaster caster, boolean isSuper) {
		if (!isSuper) return caster.iWantFoothold();
		WantedTargetResult result = caster.iWantBlockTarget();
		BlockPos pos = result.getPos();
		EnumFacing facing = result.getFace();
		if (pos == null || facing == null) return null;
		facing = facing.getOpposite();

		for (int i = 0; i < 64; i++) {
			if (world.isOutsideBuildHeight(pos)) return null;
			if (CasterHelper.canStand(world, pos) && CasterHelper.canStand(world, pos.up())) return pos;
			pos = pos.offset(facing);
		}

		return null;
	}

	private boolean checkCanBeSuper(ICaster caster, Entity entity) {
		if (entity.isSneaking()) return caster.iWantBePotent(0.75f, true) >= 0.5f;
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		super.onSpellingEffect(world, data, caster);
		if (!hasEffectFlags(world, data, caster, MantraEffectFlags.INDICATOR)) return;
		MantraDataCommon mdc = (MantraDataCommon) data;
		IWorldObject casterObject = caster.iWantCaster();
		if (casterObject.isClientPlayer()) {
			if (!mdc.hasMarkEffect(1)) mdc.addEffect(caster, new EffectPlayerAt(world, caster), 1);
		}

		Entity entity = caster.iWantCaster().asEntity();
		if (entity == null) return;

		boolean needSuper = checkCanBeSuper(caster, entity);

		// super模式下 添加指针
		if (needSuper && casterObject.isClientPlayer()) {
			if (!mdc.hasMarkEffect(4)) {
				EffectLookAt lookAt = new EffectLookAt(world, caster, getColor(mdc));
				lookAt.setCondition(new ConditionEffect(caster.iWantCaster().asEntity(), mdc, 4, true) {
					@Override
					public Boolean apply(Void t) {
						if (!super.apply(t)) return false;
						isFinish = !checkCanBeSuper(caster, entity);
						if (isFinish) {
							data.unmarkEffect(unmark);
							return false;
						}
						return true;
					}
				});
				mdc.addEffect(caster, lookAt, 4);
			}
		}

		EffectPlayerAt effectPlayAt = mdc.getMarkEffect(1, EffectPlayerAt.class);
		if (effectPlayAt == null) return;

		effectPlayAt.isGlow = needSuper;
		effectPlayAt.pos = findFoothold(world, caster, needSuper);
	}

	@SideOnly(Side.CLIENT)
	static public void addEffect(World world, Vec3d vec) {
		for (int i = 0; i < 32; ++i) {
			world.spawnParticle(EnumParticleTypes.PORTAL, vec.x, vec.y + RandomHelper.rand.nextDouble() * 2.0D, vec.z,
					RandomHelper.rand.nextGaussian(), 0.0D, RandomHelper.rand.nextGaussian());
		}
		world.playSound(vec.x, vec.y, vec.z, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1, 1, true);
	}

	public static void doEnderTeleportWithDrown(World world, Entity target, Vec3d pos) {
		doEnderTeleport(world, target, pos);
		if (target.isWet()) target.attackEntityFrom(DamageSource.DROWN, 1.0F);
	}
	
	public static void doEnderTeleport(World world, Entity target, Vec3d pos) {
		if (world.isRemote) {
			// 客户端的粒子效果
			addEffect(world, pos);
			addEffect(world, new Vec3d(target.posX, target.posY, target.posZ));
			return;
		}
		if (target instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) target;
			if (player.connection.getNetworkManager().isChannelOpen() && player.world == world
					&& !player.isPlayerSleeping()) {
				// 传送事件
				EnderTeleportEvent event = new EnderTeleportEvent(player, pos.x, pos.y, pos.z, 5.0F);
				// 事件成功
				if (!MinecraftForge.EVENT_BUS.post(event)) {
					// 下坐骑
					if (player.isRiding()) player.dismountRidingEntity();
					// 移动
					player.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
					player.fallDistance = 0.0F;
				}
			}
		} else if (target != null) {
			if (target instanceof EntityCreature) ((EntityCreature) target).getNavigator().clearPath();
			target.setPositionAndUpdate(pos.x, pos.y, pos.z);
			target.fallDistance = 0.0F;
		}
	}

}
