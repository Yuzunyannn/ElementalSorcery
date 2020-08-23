package yuzunyannn.elementalsorcery.event;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
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

@SideOnly(Side.CLIENT)
public class KeyBoard {

	public static void registerAll() {
		ClientRegistry.registerKeyBinding(KEY_MANTRA_SHITF);
	}

	public static final KeyBinding KEY_MANTRA_SHITF = new KeyBinding("key.mantra.shitf", KeyConflictContext.IN_GAME,
			KeyModifier.NONE, Keyboard.KEY_TAB, "itemGroup.ElementalSorcery");

	public static void onKeyDown(InputEvent.KeyInputEvent e) {
		if (KEY_MANTRA_SHITF.isKeyDown()) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.currentScreen != null) return;
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (player.isHandActive()) return;
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
			Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
			if (grimoire == null) return;
			e.setResult(Event.Result.ALLOW);
			player.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_MANTRA_SHITF, player.world, 0, 0, 0);
		}
	}
}
