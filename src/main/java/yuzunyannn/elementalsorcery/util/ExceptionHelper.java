package yuzunyannn.elementalsorcery.util;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ExceptionHelper {

	static public String getExcptionMsg(String msg) {
		String color = TextFormatting.RED.toString() + TextFormatting.BOLD;
		return color + "[异常][ES出现异常，请将错误日志作者，谢谢！]\n" + color
				+ "[Crash][ES have A unknown Crash! Please Send log to author, Thank!]\n" + color + "[message]" + msg;
	}

	static public void warnSend(@Nullable World world, String msg) {
		msg = getExcptionMsg(msg);
		if (world != null) {
			if (!world.isRemote) {
				for (EntityPlayer player : ((WorldServer) world).playerEntities)
					player.sendMessage(new TextComponentString(msg));
			}
		}
	}
}
