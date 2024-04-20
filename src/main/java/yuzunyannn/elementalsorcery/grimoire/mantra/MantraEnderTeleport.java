package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.mantra.MantraEffectType;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.target.WorldTarget;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectMap;
import yuzunyannn.elementalsorcery.grimoire.remote.FMantraEnderTeleportFrom;
import yuzunyannn.elementalsorcery.grimoire.remote.FMantraEnderTeleportTo;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.ParticleEffects;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectLookAtBlock;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectPlayerAt;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.world.CasterHelper;

public class MantraEnderTeleport extends MantraTypeAccumulative {

	public MantraEnderTeleport() {
		this.setTranslationKey("enderTeleport");
		this.setColor(0xc000eb);
		this.setIcon("teleport");
		this.setRarity(40);
		this.setOccupation(5);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.ENDER, 2, 50), 10, 10);
		this.addFragmentMantraLauncher(new FMantraEnderTeleportTo());
		this.addFragmentMantraLauncher(new FMantraEnderTeleportFrom());
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);
		if (!target.isNonBoss()) return;

		BlockPos pos = caster.iWantFoothold();
		if (pos == null) return;

		ElementStack stack = getElement(caster, ESObjects.ELEMENTS.ENDER, 10, 40);
		if (stack.isEmpty()) return;

		doEnderTeleportWithDrown(world, target, new Vec3d(pos).add(0.5, 0, 0.5));
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) data;
		if (mdc.getProgress() < 1) return;

		Entity entity = caster.iWantCaster().toEntity();
		if (entity == null) return;

		boolean needSuper = checkCanBeSuper(caster, entity);

		BlockPos pos = findFoothold(world, caster, needSuper);
		if (pos == null) return;

		ElementStack stack = mdc.get(ESObjects.ELEMENTS.ENDER);
		if (stack.isEmpty()) return;

		if (needSuper) caster.iWantBePotent(0.75f, false);

		doEnderTeleportWithDrown(world, entity, new Vec3d(pos).add(0.5, 0, 0.5));
	}

	public static BlockPos findFoothold(World world, ICaster caster, boolean isSuper) {
		if (!isSuper) return caster.iWantFoothold();
		WorldTarget result = caster.iWantBlockTarget();
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
		if (addCustomEffectHandle(world, data, caster, MantraEffectType.INDICATOR)) return;
		MantraDataCommon mdc = (MantraDataCommon) data;
		IWorldObject casterObject = caster.iWantCaster();
		if (casterObject.isClientPlayer()) {
			if (!mdc.getEffectMap().hasMark(MantraEffectType.INDICATOR)) {
				EffectPlayerAt effect = new EffectPlayerAt(world, caster);
				effect.setCondition(MantraEffectMap.condition(caster, mdc).setCheckContinue(true));
				mdc.getEffectMap().addAndMark(MantraEffectType.INDICATOR, effect);
			}
		}

		Entity entity = caster.iWantCaster().toEntity();
		if (entity == null) return;

		boolean needSuper = checkCanBeSuper(caster, entity);

		// super模式下 添加指针
		if (needSuper && casterObject.isClientPlayer()) {
			if (!mdc.getEffectMap().hasMark(MantraEffectType.INDICATOR)) {
				EffectLookAtBlock lookAt = new EffectLookAtBlock(world, caster, getColor(mdc));
				lookAt.setCondition(new MantraEffectMap.MantraCondition(caster, mdc) {
					@Override
					public Boolean apply(Void t) {
						if (!super.apply(t)) return false;
						isFinish = !checkCanBeSuper(caster, entity);
						if (isFinish) {
							data.getEffectMap().unmark(unmark);
							return false;
						}
						return true;
					}
				}.setCheckContinue(true));
				mdc.getEffectMap().addAndMark(MantraEffectType.INDICATOR, lookAt);
			}
		}

		EffectPlayerAt effectPlayAt = mdc.getEffectMap().getMark(MantraEffectType.INDICATOR, EffectPlayerAt.class);
		if (effectPlayAt == null) return;

		effectPlayAt.isGlow = needSuper;
		effectPlayAt.pos = findFoothold(world, caster, needSuper);
	}

	public static void doEnderTeleportWithDrown(World world, Entity target, Vec3d pos) {
		doEnderTeleport(world, target, pos);
		if (target.isWet()) target.attackEntityFrom(DamageSource.DROWN, 1.0F);
	}

	@SideOnly(Side.CLIENT)
	static public void addEffect(World world, Vec3d vec) {
		for (int i = 0; i < 32; ++i) {
			world.spawnParticle(EnumParticleTypes.PORTAL, vec.x, vec.y + RandomHelper.rand.nextDouble() * 2.0D, vec.z,
					RandomHelper.rand.nextGaussian(), 0.0D, RandomHelper.rand.nextGaussian());
		}
		world.playSound(vec.x, vec.y, vec.z, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1, 1, true);
	}

	@SideOnly(Side.CLIENT)
	public static void doEffect(World world, Vec3d from, Vec3d to) {
		addEffect(world, from);
		addEffect(world, to);
	}

	@SideOnly(Side.CLIENT)
	public static void doEffect(World world, Vec3d pos, NBTTagCompound nbt) {
		doEffect(world, pos, NBTHelper.getVec3d(nbt, "to"));
	}

	public static void playEnderTeleportEffect(World world, Entity target, Vec3d pos) {
		if (world.isRemote) {
			// 客户端的粒子效果
			doEffect(world, pos, new Vec3d(target.posX, target.posY, target.posZ));
			return;
		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("type", ParticleEffects.ENDER_TELEPORT);
		NBTHelper.setVec3d(nbt, "to", new Vec3d(target.posX, target.posY, target.posZ));
		Effects.spawnEffect(world, Effects.PARTICLE_EFFECT, pos, nbt);
	}

	public static void doEnderTeleport(World world, Entity target, Vec3d pos) {
		if (world.isRemote) {
			playEnderTeleportEffect(world, target, pos);
			return;
		}
		if (target instanceof EntityPlayer) doEnderTeleportPlayer(world, (EntityPlayer) target, pos, false);
		else if (target != null) {
			if (target instanceof EntityCreature) ((EntityCreature) target).getNavigator().clearPath();
			target.setPositionAndUpdate(pos.x, pos.y, pos.z);
			target.fallDistance = 0.0F;
		}
	}

	public static void doEnderTeleportPlayer(World world, EntityPlayer target, Vec3d pos, boolean passEvent) {
		if (target instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) target;
			if (player.connection.getNetworkManager().isChannelOpen() && player.world == world
					&& !player.isPlayerSleeping()) {
				if (!passEvent) {
					// 传送事件
					EnderTeleportEvent event = new EnderTeleportEvent(player, pos.x, pos.y, pos.z, 5.0F);
					// 事件失败
					if (MinecraftForge.EVENT_BUS.post(event)) return;
					// 更新传送坐标
					pos = new Vec3d(event.getTargetX(), event.getTargetY(), event.getTargetZ());
				}
				// 下坐骑
				if (player.isRiding()) player.dismountRidingEntity();
				// 移动
				player.setPositionAndUpdate(pos.x, pos.y, pos.z);
				player.fallDistance = 0.0F;
			}
		}
	}

}
