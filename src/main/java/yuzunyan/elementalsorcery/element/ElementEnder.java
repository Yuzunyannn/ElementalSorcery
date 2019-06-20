package yuzunyan.elementalsorcery.element;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.api.element.Element;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.api.element.IElementSpell;

public class ElementEnder extends Element implements IElementSpell {

	public ElementEnder() {
		super(rgb(204, 0, 250));
		this.setUnlocalizedName("ender");
	}

	@Override
	public int spellBegin(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
		return IElementSpell.SPELL_ONCE | IElementSpell.NEED_BLOCK;
	}

	@Override
	public void spelling(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {

	}

	@Override
	public void spellEnd(World world, EntityLivingBase entitylivingbase, ElementStack estack, SpellPackage pack) {
		if (pack.isFail())
			return;
		if (pack.pos == null)
			return;
		pack.pos = pack.pos.up();
		float posX = pack.pos.getX() + 0.5f;
		float posY = pack.pos.getY() + 0.5f;
		float posZ = pack.pos.getZ() + 0.5f;
		// 粒子效果
		for (int i = 0; i < 32; ++i) {
			world.spawnParticle(EnumParticleTypes.PORTAL, posX, posY + rand.nextDouble() * 2.0D, posZ,
					rand.nextGaussian(), 0.0D, rand.nextGaussian());
		}
		if (world.isRemote)
			return;
		if (!world.isAirBlock(pack.pos)) {
			entitylivingbase.attackEntityFrom(DamageSource.FALL, 1.0f);
			return;
		}

		if (entitylivingbase instanceof EntityPlayerMP) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) entitylivingbase;
			if (entityplayermp.connection.getNetworkManager().isChannelOpen() && entityplayermp.world == world
					&& !entityplayermp.isPlayerSleeping()) {
				// 传送事件
				net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(
						entityplayermp, posX, posY, posZ, 5.0F);
				// 事件成功
				if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
					// 下坐骑
					if (entitylivingbase.isRiding()) {
						entitylivingbase.dismountRidingEntity();
					}
					// 移动
					entitylivingbase.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
					entitylivingbase.fallDistance = 0.0F;
					// 声音
					world.playSound((EntityPlayer) null, entitylivingbase.prevPosX, entitylivingbase.prevPosY,
							entitylivingbase.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE,
							1.0F, 1.0F);
					world.playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
							SoundCategory.HOSTILE, 1.0F, 1.0F);
				}
			}
		} else if (entitylivingbase != null) {
			entitylivingbase.setPositionAndUpdate(posX, posY, posZ);
			entitylivingbase.fallDistance = 0.0F;
		}
	}

	@Override
	public int cast(ElementStack estack, int level) {
		return 40;
	}

	@Override
	public int cost(ElementStack estack, int level) {
		return 20;
	}

	@Override
	public int lowestPower(ElementStack estack, int level) {
		return 100;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInfo(ElementStack estack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn,
			int level) {
		tooltip.add(I18n.format("info.element.spell.ender"));
	}
}
