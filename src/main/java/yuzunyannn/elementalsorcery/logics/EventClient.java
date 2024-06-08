package yuzunyannn.elementalsorcery.logics;

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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.InputUpdateEvent;
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
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.ElementTransition;
import yuzunyannn.elementalsorcery.api.util.client.IRenderOutline;
import yuzunyannn.elementalsorcery.computer.WideNetworkCommon;
import yuzunyannn.elementalsorcery.computer.render.ComputerScreenRender;
import yuzunyannn.elementalsorcery.computer.softs.TaskNetworkGui;
import yuzunyannn.elementalsorcery.config.ESConfig;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.elf.ElfChamberOfCommerce;
import yuzunyannn.elementalsorcery.init.TileOutlineRenderRegistries;
import yuzunyannn.elementalsorcery.item.ItemRiteManual;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.item.RenderItemElementCrack;
import yuzunyannn.elementalsorcery.ts.PocketWatchClient;
import yuzunyannn.elementalsorcery.util.Stopwatch;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.IItemUseClientUpdate;

@SideOnly(Side.CLIENT)
public class EventClient {

	static public final Stopwatch bigComputeWatch = new Stopwatch();
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
	// 全局标注是否为一个tick状态的帧，渲染直接使用
	public static boolean canTickInRender = false;
	// 客户端的mc指针
	public static final Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	static public void onTick(TickEvent.ClientTickEvent event) {
		canTickInRender = true;
		if (mc.isGamePaused()) return;
		if (event.phase == Phase.START) {
			bigComputeWatch.clear();
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
		if (RenderItemElementCrack.updateRenderTextureFlag) {
			RenderItemElementCrack.updateRenderData();
			RenderItemElementCrack.updateRenderTextureFlag = false;
		}
		ComputerScreenRender.doUpdate();
		WideNetworkCommon.instanceClient.update();
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
			ESAPI.logger.warn("post渲染异常！", exce);
		}
		renderIterate--;
		Effect.renderAllEffects(partialTicks);
		GlStateManager.popMatrix();

		ComputerScreenRender.doRenderUpdate(e.getPartialTicks());
		if (RenderItemElementCrack.updateRenderTextureFlag) RenderItemElementCrack.updateRenderTexture(partialTicks);

		canTickInRender = false;
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
		Minecraft.getMinecraft().addScheduledTask(() -> {
			renderList.clear();
			Effect.clear();
			TaskNetworkGui.lastCacheUDID = null;
		});
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

	@SubscribeEvent
	static public void drawOutline(DrawBlockHighlightEvent event) {
		if (event.isCanceled()) return;

		RayTraceResult movingObjectPositionIn = event.getTarget();
		if (movingObjectPositionIn.typeOfHit != RayTraceResult.Type.BLOCK) return;

		World world = event.getPlayer().world;
		BlockPos blockpos = movingObjectPositionIn.getBlockPos();
		TileEntity tileEntity = world.getTileEntity(blockpos);
		if (tileEntity == null) return;

		IRenderOutline<?> renderOutline = TileOutlineRenderRegistries.instance.getRenderOutline(tileEntity);
		if (renderOutline == null) return;

		if (!world.getWorldBorder().contains(blockpos)) return;

		try {
			((IRenderOutline<TileEntity>) renderOutline).renderTileOutline(tileEntity, event.getPlayer(), blockpos, event.getPartialTicks());
			event.setCanceled(true);
		} catch (ClassCastException e) {}
	}

	@SubscribeEvent
	static public void playerInputHandler(InputUpdateEvent evt) {

	}
//
//	@SubscribeEvent
//	static public void onGuiOpen(GuiOpenEvent evt) {
//		GuiScreen screen = evt.getGui();
//		if (screen instanceof GuiContainerCreative) {
//			try {
//				GuiContainerCreative gui = (GuiContainerCreative) screen;
//				List<Slot> inventorySlots = gui.inventorySlots.inventorySlots;
//				for (int i = 0; i < inventorySlots.size(); i++) {
//					Slot slot = inventorySlots.get(i);
//					inventorySlots.set(i, new GuiDisableCreativeSyncSlot(slot));
//				}
//			} catch (Exception e) {}
//		}
//	}

	static public void drawDebugTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();
		// 即将删除告示
		if (item.getClass().getAnnotation(Deprecated.class) != null || Block.getBlockFromItem(item).getClass().getAnnotation(Deprecated.class) != null) {
			event.getToolTip().add(TextFormatting.GOLD + "该道具即将被移除。");
			event.getToolTip().add(TextFormatting.GOLD + "This item will be removed soon.");
		}
		// 显示元素
		if (!ESConfig.ENABLE_ITEM_ELEMENT_TOOLTIP_SHOW) return;
		EntityPlayer player = event.getEntityPlayer();
		if (player == null || !player.isCreative()) return;
		IToElementInfo teInfo = ElementMap.instance.toElement(stack);
		ElementStack[] estacks = teInfo == null ? null : teInfo.element();
		if (estacks != null) {
			List<String> tooltip = event.getToolTip();
			tooltip.add(TextFormatting.DARK_RED + I18n.format("info.itemCrystal.complex", teInfo.complex()));
			for (ElementStack estack : estacks) {
				if (estack.isEmpty()) continue;
				ElementHelper.addElementInformation(estack, tooltip, -1);
			}
		}

		if (ESAPI.isDevelop) {
			List<String> tooltip = event.getToolTip();
			tooltip.add(TextFormatting.GREEN + "Develop Info:");
			tooltip.add(TextFormatting.GRAY + "isReplairable: " + stack.getItem().isRepairable());
			if (estacks != null) {
				double f = 0;
				for (ElementStack estack : estacks) {
					if (estack.isEmpty()) continue;
					String str = String.format(TextFormatting.AQUA + "%sx%s" + TextFormatting.YELLOW
							+ " P:%d", estack.getDisplayName(), String.valueOf(estack.getCount()), estack.getPower());
					double fr = ElementTransition.toFragment(estack);
					tooltip.add(str + TextFormatting.LIGHT_PURPLE + " EF:" + TextHelper.toAbbreviatedNumber(fr));
					ElementTransition et = estack.getElement().getTransition();
					if (et == null) {
						if (!estack.isMagic()) f += fr;
					} else f += ElementTransition.transitionFrom(estack.getElement(), fr, et.getLevel());
				}
				tooltip.add(TextFormatting.LIGHT_PURPLE + "MFEM:" + TextHelper.toAbbreviatedNumber(f));
			}
			int price = ElfChamberOfCommerce.priceIt(stack);
			if (price > 0) tooltip.add(TextFormatting.GOLD + "" + price + "$");
			int[] ids = OreDictionary.getOreIDs(stack);
			for (int i : ids) tooltip.add(TextFormatting.BOLD + OreDictionary.getOreName(i));

			if (estacks != null) {
				int complex = teInfo.complex();
				for (ElementStack estack : estacks) {
					if (estack.isEmpty()) continue;
					ElementStack rStack = estack.copy().onDeconstruct(Minecraft.getMinecraft().world, stack, complex, Element.DP_ALTAR);
					String str = String.format(TextFormatting.DARK_AQUA + "%sx%s" + TextFormatting.YELLOW
							+ " P:%d", rStack.getDisplayName(), String.valueOf(rStack.getCount()), rStack.getPower());
					tooltip.add(str);
				}
			}
		}
	}
}
