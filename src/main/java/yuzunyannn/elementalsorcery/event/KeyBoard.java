package yuzunyannn.elementalsorcery.event;

import java.util.Iterator;
import java.util.LinkedList;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;

@SideOnly(Side.CLIENT)
public class KeyBoard {

	public static void registerAll() {
		ClientRegistry.registerKeyBinding(KEY_MANTRA_SHITF);
	}

	public static final KeyBinding KEY_MANTRA_SHITF = new KeyBinding("key.es.func", KeyConflictContext.IN_GAME,
			KeyModifier.NONE, Keyboard.KEY_TAB, "itemGroup.ElementalSorcery");

	public static void onKeyDown(InputEvent.KeyInputEvent e) {
		if (KEY_MANTRA_SHITF.isKeyDown()) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.currentScreen != null) return;
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (player.isHandActive()) return;
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
			Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
			if (grimoire == null) {
				if (elfTreeElevator(player)) e.setResult(Event.Result.ALLOW);
				return;
			}
			e.setResult(Event.Result.ALLOW);
			player.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_MANTRA_SHITF, player.world, 0, 0, 0);
		}
	}

	public static final LinkedList<BlockPos> elfTreeCoreCache = new LinkedList<>();

	public static boolean elfTreeElevator(EntityPlayer player) {
		BlockPos pos = player.getPosition().down();
		if (player.world.getBlockState(pos) != ESInit.BLOCKS.ELF_LOG.getDefaultState()) return false;

		if (elfTreeCoreCache.size() > 10) elfTreeCoreCache.removeFirst();
		Iterator<BlockPos> iter = elfTreeCoreCache.iterator();
		while (iter.hasNext()) {
			BlockPos p = iter.next();
			if (TileElfTreeCore.inRangeElevator(p, player)) {
				IBlockState state = player.world.getBlockState(p);
				if (state.getBlock() == ESInit.BLOCKS.ELF_TREE_CORE) {
					TileElfTreeCore.openElevatorUI(p, player);
					return true;
				} else iter.remove();
			}
		}
		BlockPos corePos = TileElfTreeCore.findTreeCoreFrom(player.world, pos);
		if (corePos != null) {
			elfTreeCoreCache.add(corePos);
			TileElfTreeCore.openElevatorUI(corePos, player);
			return true;
		}
		return false;
	}
}
