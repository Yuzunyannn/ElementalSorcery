package yuzunyannn.elementalsorcery.util;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ExceptionHelper {

	static public void warnSend(@Nullable World world, String msg) {
		msg = "[ES出现异常，请联系作者，谢谢！]" + msg;
		if (world != null) {
			if (!world.isRemote) {
				for (EntityPlayer player : ((WorldServer) world).playerEntities)
					player.sendMessage(new TextComponentString(msg));
			}
		}
	}
}
