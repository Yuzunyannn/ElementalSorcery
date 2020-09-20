package yuzunyannn.elementalsorcery.elf.talk;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;

/** 特殊对话处理，在自己类的static中注册就可以 */
@SideOnly(Side.CLIENT)
public interface ITalkSpecial {

	public static final Map<String, ITalkSpecial> REGISTRY = new HashMap<String, ITalkSpecial>();

	public String deal(EntityPlayer player, EntityElfBase elf, TalkChapter chapter, TalkChapter.Iter iter,
			String originStr);

}
