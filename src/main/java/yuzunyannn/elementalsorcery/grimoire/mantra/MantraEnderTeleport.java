package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectPlayerAt;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class MantraEnderTeleport extends MantraCommon {

	public MantraEnderTeleport() {
		this.setUnlocalizedName("enderTeleport");
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		ElementStack need = new ElementStack(ESInitInstance.ELEMENTS.ENDER, 15, 50);
		ElementStack get = caster.iWantSomeElement(need, false);
		dataEffect.markContinue(!get.isEmpty());
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		if (caster.iWantKnowCastTick() < 5) return;
		BlockPos pos = caster.iWantFoothold();
		if (pos == null) return;
		ElementStack need = new ElementStack(ESInitInstance.ELEMENTS.ENDER, 15, 50);
		ElementStack stack = caster.iWantSomeElement(need, true);
		if (stack.isEmpty()) return;
		Entity entity = caster.iWantCaster();
		double posX = pos.getX() + 0.5;
		double posY = pos.getY();
		double posZ = pos.getZ() + 0.5;
		if (world.isRemote) {
			// 客户端的粒子效果
			for (int i = 0; i < 32; ++i) {
				world.spawnParticle(EnumParticleTypes.PORTAL, posX, posY + RandomHelper.rand.nextDouble() * 2.0D, posZ,
						RandomHelper.rand.nextGaussian(), 0.0D, RandomHelper.rand.nextGaussian());
			}
			for (int i = 0; i < 32; ++i) {
				world.spawnParticle(EnumParticleTypes.PORTAL, entity.posX,
						entity.posY + RandomHelper.rand.nextDouble() * 2.0D, entity.posZ,
						RandomHelper.rand.nextGaussian(), 0.0D, RandomHelper.rand.nextGaussian());
			}
			return;
		}
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			if (player.connection.getNetworkManager().isChannelOpen() && player.world == world
					&& !player.isPlayerSleeping()) {
				// 传送事件
				EnderTeleportEvent event = new EnderTeleportEvent(player, posX, posY, posZ, 5.0F);
				// 事件成功
				if (!MinecraftForge.EVENT_BUS.post(event)) {
					// 下坐骑
					if (player.isRiding()) player.dismountRidingEntity();
					// 移动
					player.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
					player.fallDistance = 0.0F;
					// 声音
					world.playSound((EntityPlayer) null, player.prevPosX, player.prevPosY, player.prevPosZ,
							SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1.0F, 1.0F);
					world.playSound((EntityPlayer) null, event.getTargetX(), event.getTargetY(), event.getTargetZ(),
							SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1.0F, 1.0F);
				}
				if (entity.isWet()) entity.attackEntityFrom(DamageSource.DROWN, 1.0F);
			}
		} else if (entity != null) {
			entity.setPositionAndUpdate(posX, posY, posZ);
			entity.fallDistance = 0.0F;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean onSpellingEffect(World world, IMantraData data, ICaster caster) {
		if (super.onSpellingEffect(world, data, caster)) return true;
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		if (caster.iWantCaster() == Minecraft.getMinecraft().player)
			if (!dataEffect.hasMarkEffect(1)) dataEffect.addEffect(caster,new EffectPlayerAt(world, caster), 1);
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Element getMagicCircle() {
		return ESInitInstance.ELEMENTS.ENDER;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getIconResource() {
		return RenderObjects.MANTRA_TELEPORT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor() {
		return 0xc000eb;
	}

}
