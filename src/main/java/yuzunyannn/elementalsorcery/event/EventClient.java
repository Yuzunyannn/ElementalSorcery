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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.config.ESConfig;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfessionMerchant;
import yuzunyannn.elementalsorcery.item.ItemRiteManual;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.ts.PocketWatchClient;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.IItemUseClientUpdate;

@SideOnly(Side.CLIENT)
public class EventClient {

	static public final Random rand = new Random();

	static private final List<ITickTask> tickList = new LinkedList<ITickTask>();

	@SubscribeEvent
	public static void onKeyPressed(InputEvent.KeyInputEvent e) {
		KeyBoard.onKeyDown(e);
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
	// 全局client的渲染专用tick
	public static int tickRender = 0;
	// 全局随机的，隔一段时间随机的一个整数
	public static int randInt = rand.nextInt();
	// 客户端的mc指针
	public static final Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	static public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.isGamePaused()) return;
		if (event.phase == Phase.START) {
			if (!PocketWatchClient.isActive()) return;
			PocketWatchClient.tick();
			Effect.updateGuiEffects();
			return;
		}
		// tick增加
		tick++;
		// 处理tick队列
		Iterator<ITickTask> iter = tickList.iterator();
		while (iter.hasNext()) {
			ITickTask task = iter.next();
			int flags = task.onTick();
			if (flags == ITickTask.END) iter.remove();
		}
		if (PocketWatchClient.isActive()) return;
		tickRender++;
		// 全局旋转
		globalRotate += DGLOBAL_ROTATE;
		if (globalRotate >= 360 * 10000) globalRotate -= 360 * 10000;
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

	@SubscribeEvent
	static public void onPlaySound(PlaySoundEvent event) {
		if (!PocketWatchClient.isActive()) return;
		if (event.getSound().getCategory() != SoundCategory.PLAYERS) {
			event.setResultSound(null);
			return;
		}
	}

	private static int renderIterate = 0;

	@SubscribeEvent
	static public void renderWord(RenderWorldLastEvent e) {
		GlStateManager.pushMatrix();
		float partialTicks = e.getPartialTicks();
		// 将坐标归位
		EntityPlayer entityplayer = Minecraft.getMinecraft().player;
		double ex = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double) partialTicks;
		double ey = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double) partialTicks;
		double ez = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double) partialTicks;
		GlStateManager.translate(-ex, -ey, -ez);
		if (PocketWatchClient.isActive()) partialTicks = 0;
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
		Effect.renderAllEffects(partialTicks);
		GlStateManager.popMatrix();
	}

	@SubscribeEvent
	static public void onFogColors(FogColors event) {
		if (PocketWatchClient.isActive()) {
			float c = event.getRed() * 0.299f + event.getGreen() * 0.587f + event.getBlue() * 0.114f;
			event.setBlue(c);
			event.setRed(c);
			event.setGreen(c);
		}
	}

	@SubscribeEvent
	static public void renderGUI(RenderGameOverlayEvent.Post e) {
		Effect.renderAllGuiEffects(e.getPartialTicks());
	}

	@SubscribeEvent
	static public void playerExit(PlayerEvent.PlayerLoggedOutEvent e) {
		renderList.clear();
		Effect.clear();
	}

	@SubscribeEvent
	static public void entityUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity.world.isRemote && entity.isHandActive()) {
			ItemStack stack = entity.getActiveItemStack();
			Item item = stack.getItem();
			if (item instanceof IItemUseClientUpdate) {
				((IItemUseClientUpdate) item).onUsingTickClient(stack, entity, entity.getItemInUseCount());
			}
		}
	}

	/** 全局信息显示 */
	@SubscribeEvent
	static public void drawTooltip(ItemTooltipEvent event) {
		ItemRiteManual.drawTooltip(event);
		drawDebugTooltip(event);
	}

	static public void drawDebugTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();
		// 即将删除告示
		if (item.getClass().getAnnotation(Deprecated.class) != null
				|| Block.getBlockFromItem(item).getClass().getAnnotation(Deprecated.class) != null) {
			event.getToolTip().add(TextFormatting.GOLD + "该道具即将被移除。");
			event.getToolTip().add(TextFormatting.GOLD + "This item will be removed soon.");
		}
		// 显示元素
		if (!ESConfig.ENABLE_ITEM_ELEMENT_TOOLTIP_SHOW) return;
		EntityPlayer player = event.getEntityPlayer();
		if (player == null || !player.isCreative()) return;
		IToElementInfo teInfo = ElementMap.instance.toElement(stack);
		ElementStack[] estacks = teInfo == null ? null : teInfo.element();
		if (estacks == null) return;
		List<String> tooltip = event.getToolTip();
		tooltip.add(TextFormatting.DARK_RED + I18n.format("info.itemCrystal.complex", teInfo.complex()));
		for (ElementStack estack : estacks) {
			if (estack.isEmpty()) continue;
			ElementHelper.addElementInformation(estack, tooltip);
		}

		if (ElementalSorcery.isDevelop) {
			tooltip.add(TextFormatting.GREEN + "Develop Info:");
			tooltip.add(TextFormatting.GOLD + "" + ElfProfessionMerchant.priceIt(stack) + "$");
			for (ElementStack estack : estacks) {
				if (estack.isEmpty()) continue;
				estack = estack.copy().becomeElementWhenDeconstruct(Minecraft.getMinecraft().world, stack,
						teInfo.complex(), Element.DP_ALTAR_SURPREME);
				String str = I18n.format("info.elementCrystal.has", estack.getDisplayName(), estack.getCount(),
						estack.getPower());
				tooltip.add(TextFormatting.AQUA + str);
			}
			int[] ids = OreDictionary.getOreIDs(stack);
			for (int i : ids) {
				tooltip.add(TextFormatting.BOLD + OreDictionary.getOreName(i));
			}
		}
	}
}
