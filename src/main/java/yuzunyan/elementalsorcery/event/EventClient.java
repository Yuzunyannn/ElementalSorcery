package yuzunyan.elementalsorcery.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.capability.Spellbook;
import yuzunyan.elementalsorcery.item.ItemSpellbook;

public class EventClient {

	static public Random rand = new Random();

	static private List<SpellbookOpenMsg> spellbook_open_msgs = new LinkedList<SpellbookOpenMsg>();

	static private class SpellbookOpenMsg {
		public EntityLivingBase entity;
		public ItemStack stack;
	}

	@SideOnly(Side.CLIENT)
	static public void addSpellbookOpen(EntityLivingBase entity, ItemStack stack) {
		if (!stack.hasCapability(Spellbook.SPELLBOOK_CAPABILITY, null))
			return;
		if (!(stack.getItem() instanceof ItemSpellbook))
			return;
		Spellbook book = stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		SpellbookOpenMsg msg = new SpellbookOpenMsg();
		msg.entity = entity;
		msg.stack = stack;
		spellbook_open_msgs.add(msg);
		ItemSpellbook.renderStart(book);
	}

	// 全局旋转，单位角度
	public static float global_rotate = 0.0f;
	// 旋转角度增量
	public static final float DGLOBAL_ROTATE = 2.25f * 0.7f;
	// 全局client的tick
	public static int tick = 0;
	// 全局随机的，隔一段时间随机的一个整数
	public static int rand_int = rand.nextInt();

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onTick(TickEvent.ClientTickEvent event) {
		// tick增加
		tick++;
		// 处理书打开队列
		Iterator<SpellbookOpenMsg> it = spellbook_open_msgs.iterator();
		while (it.hasNext()) {
			SpellbookOpenMsg x = it.next();
			Spellbook book = x.stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
			if (x.entity.isHandActive()) {
				ItemSpellbook.renderOpen(book);
				((ItemSpellbook) x.stack.getItem()).onUsingTickClient(x.entity, x.stack, book);
			} else {
				if (ItemSpellbook.renderClose(book)) {
					ItemSpellbook.renderEnd(book);
					it.remove();
				}
			}
		}
		// 全局旋转
		global_rotate += DGLOBAL_ROTATE;
		if (global_rotate >= 360 * 10) {
			global_rotate -= 360 * 10;
		}
		// 全局随机整数
		if (tick % 80 == 0) {
			rand_int = rand.nextInt();
			rand_int = Math.abs(rand_int);
		}
		// 其他处理

	}

}
