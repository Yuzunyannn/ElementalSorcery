package yuzunyannn.elementalsorcery.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.item.ItemSpellbook;
import yuzunyannn.elementalsorcery.render.particle.Effect;

@SideOnly(Side.CLIENT)
public class EventClient {

	static public final Random rand = new Random();

	static private final List<ITickTask> tickList = new LinkedList<ITickTask>();

	static private class SpellbookOpenMsg implements ITickTask {
		public EntityLivingBase entity;
		public ItemStack stack;

		@Override
		public int onTick() {
			Spellbook book = this.stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
			if (this.entity.isHandActive()) {
				ItemSpellbook.renderOpen(book);
				((ItemSpellbook) this.stack.getItem()).onUsingTickClient(this.entity, this.stack, book);
			} else {
				if (ItemSpellbook.renderClose(book)) {
					ItemSpellbook.renderEnd(book);
					if (book.who != null) {
						this.stack.getItem().onPlayerStoppedUsing(this.stack, this.entity.getEntityWorld(), this.entity,
								this.entity.getItemInUseCount());
					}
					return ITickTask.END;
				}
			}
			return ITickTask.SUCCESS;
		}
	}

	static public void addSpellbookOpen(EntityLivingBase entity, ItemStack stack) {
		if (!stack.hasCapability(Spellbook.SPELLBOOK_CAPABILITY, null)) return;
		if (!(stack.getItem() instanceof ItemSpellbook)) return;
		Spellbook book = stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		if (book == null) {
			ElementalSorcery.logger.warn("客户端传入的物品不包含Spellbook但检测到了null！" + stack.toString());
			return;
		}
		SpellbookOpenMsg msg = new SpellbookOpenMsg();
		if (Minecraft.getMinecraft().player != entity) {
			book.who = entity;
		}
		msg.entity = entity;
		msg.stack = stack;
		EventClient.addTickTask(msg);
		ItemSpellbook.renderStart(book);
		// 如果是当前客户端的其他玩家
		if (book.who != null) {
			Item item = stack.getItem();
			if (item instanceof ItemSpellbook) {
				((ItemSpellbook) item).spellBegin(entity.getEntityWorld(), entity, stack, book);
			} else ElementalSorcery.logger.warn("客户端传入的spellbook的打开消息的物品有误！传入物品：" + item);
		}
	}

	/** 添加一个客户端的tick任务 */
	static public void addTickTask(ITickTask task) {
		if (task == null) return;
		tickList.add(task);
	}

	static public void addTickTask(ITickTask task, int tickout) {
		if (task == null) return;
		if (tickout <= 0) tickList.add(task);
		else tickList.add(new ITickTask() {
			int tick = 0;

			@Override
			public int onTick() {
				if (tick < tickout) {
					tick++;
					return ITickTask.SUCCESS;
				}
				return task.onTick();
			}
		});
	}

	// 全局旋转，单位角度
	public static float globalRotate = 0.0f;
	// 旋转角度增量
	public static final float DGLOBAL_ROTATE = 2.25f * 0.5f;
	// 全局client的tick
	public static int tick = 0;
	// 全局随机的，隔一段时间随机的一个整数
	public static int randInt = rand.nextInt();
	// 客户端的mc指针
	public static final Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	static public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.isGamePaused()) return;
		if (event.phase != Phase.END) return;
		// tick增加
		tick++;
		// 处理tick队列
		Iterator<ITickTask> iter = tickList.iterator();
		while (iter.hasNext()) {
			ITickTask task = iter.next();
			int flags = task.onTick();
			if (flags == ITickTask.END) iter.remove();
		}
		// 全局旋转
		globalRotate += DGLOBAL_ROTATE;
		if (globalRotate >= 360 * 1000) {
			globalRotate -= 360 * 1000;
		}
		// 全局随机整数
		if (tick % 80 == 0) {
			randInt = rand.nextInt();
			randInt = Math.abs(randInt);
		}
		// 更新所有ES粒子效果
		Effect.updateAllEffects();
		// 其他处理

	}

	/** 获取渲染旋转角度 */
	static public float getGlobalRotateInRender(float partialTicks) {
		return globalRotate + DGLOBAL_ROTATE * partialTicks;
	}

	static private final List<IRenderClient> renderList = new LinkedList<IRenderClient>();

	/** 添加一个客户端的渲染任务 */
	static public void addRenderTask(IRenderClient task) {
		if (task == null) return;
		renderList.add(task);
	}

	private static int renderIterate = 0;

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	static public void renderWord(RenderWorldLastEvent e) {
		GlStateManager.pushMatrix();
		float partialTicks = e.getPartialTicks();
		// 将坐标归位
		EntityPlayer entityplayer = Minecraft.getMinecraft().player;
		double ex = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double) partialTicks;
		double ey = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double) partialTicks;
		double ez = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double) partialTicks;
		GlStateManager.translate(-ex, -ey, -ez);
		renderIterate++;
		try {
			Iterator<IRenderClient> iter = renderList.iterator();
			while (iter.hasNext()) {
				IRenderClient task = iter.next();
				int flags = task.onRender(partialTicks);
				if (renderIterate > 1) continue;
				if (flags == IRenderClient.END) iter.remove();
			}
		} catch (Exception exce) {
			ElementalSorcery.logger.warn("post渲染异常！", exce);
		}
		renderIterate--;
		Effect.renderAllEffects(e.getPartialTicks());
		GlStateManager.popMatrix();
	}

	@SubscribeEvent
	static public void playerExit(PlayerEvent.PlayerLoggedOutEvent e) {
		renderList.clear();
	}

	/** 全局信息显示 */
	@SubscribeEvent
	static public void drawDebugTools(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();
		// 即将删除告示
		if (item.getClass().getAnnotation(Deprecated.class) != null
				|| Block.getBlockFromItem(item).getClass().getAnnotation(Deprecated.class) != null) {
			event.getToolTip().add(TextFormatting.GOLD + "该道具即将被移除。");
			event.getToolTip().add(TextFormatting.GOLD + "This item will be removed soon.");
		}
		// 显示元素
		if (!ElementalSorcery.config.SHOW_ELEMENT_TOOLTIP) return;
		EntityPlayer player = event.getEntityPlayer();
		if (player == null || !player.isCreative()) return;
		ElementStack[] estacks = ElementMap.instance.toElement(stack);
		if (estacks == null) return;
		List<String> tooltip = event.getToolTip();
		tooltip.add(
				TextFormatting.DARK_RED + I18n.format("info.itemCrystal.complex", ElementMap.instance.complex(stack)));
		for (ElementStack estack : estacks) {
			if (estack.isEmpty()) continue;
			String str;
			if (estack.usePower()) str = I18n.format("info.elementalCrystal.has",
					I18n.format(estack.getElementUnlocalizedName()), estack.getCount(), estack.getPower());
			else str = I18n.format("info.elementalCrystal.hasnp", I18n.format(estack.getElementUnlocalizedName()),
					estack.getCount());
			tooltip.add(TextFormatting.RED + str);
		}
	}
}
