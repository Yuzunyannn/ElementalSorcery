package yuzunyannn.elementalsorcery.util;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import yuzunyannn.elementalsorcery.ElementalSorcery;

public class ExceptionHelper {

	static public void warn(@Nullable World world, String msg) {
		msg = "ES异常错误提示：" + msg;
		ElementalSorcery.logger.warn(msg);
		if (world != null) {
			if (!world.isRemote) {
				for (EntityPlayer player : ((WorldServer) world).playerEntities)
					player.sendMessage(new TextComponentString(msg));
			}
		}
	}

	static public void warn(@Nullable World world, String msg, String... strs) {
		msg = "ES异常错误提示：" + msg;
		for (String str : strs)
			msg += str;
		ElementalSorcery.logger.warn(msg);
		if (world != null) {
			if (!world.isRemote) {
				for (EntityPlayer player : ((WorldServer) world).playerEntities)
					player.sendMessage(new TextComponentString(msg));
			}
		}
	}
}
