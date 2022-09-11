package yuzunyannn.elementalsorcery.util.helper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.mantra.ISilentWorld;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;

public class SilentWorld implements ISilentWorld {

	@Override
	public boolean isSilent(Entity entity, SilentLevel level) {
		if (entity instanceof EntityLivingBase) {
			PotionEffect effect = ((EntityLivingBase) entity).getActivePotionEffect(ESObjects.POTIONS.SILENT);
			if (effect != null && (effect.getAmplifier() + 1) >= level.lev) return true;
		}
		return isSilent(entity.world, entity.getPositionVector(), level);
	}

	@Override
	public boolean isSilent(World world, Vec3d pos, SilentLevel level) {
		return false;
	}

	// 闭嘴
	public static void shutup(EntityLivingBase target, int sec) {
		target.addPotionEffect(new PotionEffect(ESObjects.POTIONS.SILENT, sec * 20));
	}

	public static String getSilentTalk(EntityPlayer player) {
		int n = RandomHelper.rand.nextInt(16) + 8;
		char[] chs = new char[n];
		for (int i = 0; i < chs.length; i++) chs[i] = (char) (RandomHelper.rand.nextInt(6) + 33);
		return new String(chs);
	}

	public static void sendSilentMessage(EntityPlayer player, SilentLevel level) {
		if (level == SilentLevel.SPELL)
			player.sendMessage(new TextComponentString(player.getName() + ": " + getSilentTalk(player))
					.setStyle(new Style().setColor(TextFormatting.GRAY)));
		else player.sendMessage(new TextComponentString("::>>::silent::<<")
				.setStyle(new Style().setColor(TextFormatting.GRAY).setObfuscated(true)));
	}

}
