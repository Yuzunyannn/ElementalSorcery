package yuzunyan.elementalsorcery.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.ElementalSorcery;
import yuzunyan.elementalsorcery.capability.Spellbook;
import yuzunyan.elementalsorcery.item.ItemSpellbook;

@SideOnly(Side.CLIENT)
public class EventClient {

	static public final Random rand = new Random();

	static private final List<SpellbookOpenMsg> spellbook_open_msgs = new LinkedList<SpellbookOpenMsg>();
	static private final List<ITickClient> tick_list = new LinkedList<ITickClient>();

	static private class SpellbookOpenMsg {
		public EntityLivingBase entity;
		public ItemStack stack;
	}

	static public void addSpellbookOpen(EntityLivingBase entity, ItemStack stack) {
		if (!stack.hasCapability(Spellbook.SPELLBOOK_CAPABILITY, null))
			return;
		if (!(stack.getItem() instanceof ItemSpellbook))
			return;
		Spellbook book = stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		SpellbookOpenMsg msg = new SpellbookOpenMsg();
		if (Minecraft.getMinecraft().player != entity) {
			book.who = entity;
		}
		msg.entity = entity;
		msg.stack = stack;
		spellbook_open_msgs.add(msg);
		ItemSpellbook.renderStart(book);
		// 如果是当前客户端的其他玩家
		if (book.who != null) {
			Item item = stack.getItem();
			if (item instanceof ItemSpellbook) {
				((ItemSpellbook) item).spellBegin(entity.getEntityWorld(), entity, stack, book);
			} else
				ElementalSorcery.logger.warn("客户端传入的spellbook的打开消息的物品有误！传入物品：" + item);
		}
	}

	static public void addTickTask(ITickClient task) {
		if (task == null)
			return;
		tick_list.add(task);
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
		Iterator<SpellbookOpenMsg> iterBook = spellbook_open_msgs.iterator();
		while (iterBook.hasNext()) {
			SpellbookOpenMsg x = iterBook.next();
			Spellbook book = x.stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
			if (x.entity.isHandActive()) {
				ItemSpellbook.renderOpen(book);
				((ItemSpellbook) x.stack.getItem()).onUsingTickClient(x.entity, x.stack, book);
			} else {
				if (ItemSpellbook.renderClose(book)) {
					ItemSpellbook.renderEnd(book);
					if (book.who != null) {
						x.stack.getItem().onPlayerStoppedUsing(x.stack, x.entity.getEntityWorld(), x.entity,
								x.entity.getItemInUseCount());
					}
					iterBook.remove();
				}
			}
		}
		// 处理tick队列
		Iterator<ITickClient> iter = tick_list.iterator();
		while (iter.hasNext()) {
			ITickClient task = iter.next();
			int flags = task.onTick();
			if (flags == ITickClient.END)
				iter.remove();
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
