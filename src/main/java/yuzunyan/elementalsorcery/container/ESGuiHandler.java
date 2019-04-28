package yuzunyan.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import yuzunyan.elementalsorcery.container.gui.GuiAbsorbBox;
import yuzunyan.elementalsorcery.container.gui.GuiDeconstructBox;
import yuzunyan.elementalsorcery.container.gui.GuiElementCraftingTable;
import yuzunyan.elementalsorcery.container.gui.GuiElementWorkbench;
import yuzunyan.elementalsorcery.container.gui.GuiHearth;
import yuzunyan.elementalsorcery.container.gui.GuiInfusionBox;
import yuzunyan.elementalsorcery.container.gui.GuiParchment;
import yuzunyan.elementalsorcery.container.gui.GuiSmeltBox;

public class ESGuiHandler implements IGuiHandler {

	public static final int GUI_HEARTH = 1;
	public static final int GUI_SMELT_BOX = 2;
	public static final int GUI_ABSORB_BOX = 3;
	public static final int GUI_ELEMENT_WORKBENCH = 4;
	public static final int GUI_DECONSTRUCT_BOX = 5;
	public static final int GUI_INFUSION_BOX = 6;
	public static final int GUI_PARCHMENT = 7;
	public static final int GUI_ELEMENT_CRAFTING_TABLE = 8;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case GUI_HEARTH:
			return new ContainerHearth(player, world.getTileEntity(new BlockPos(x, y, z)));
		case GUI_SMELT_BOX:
			return new ContainerSmeltBox(player, world.getTileEntity(new BlockPos(x, y, z)));
		case GUI_ABSORB_BOX:
			return new ContainerAbsorbBox(player, world.getTileEntity(new BlockPos(x, y, z)));
		case GUI_ELEMENT_WORKBENCH:
			return new ContainerElementWorkbench(player.inventory, world, new BlockPos(x, y, z));
		case GUI_DECONSTRUCT_BOX:
			return new ContainerDeconstructBox(player, world.getTileEntity(new BlockPos(x, y, z)));
		case GUI_INFUSION_BOX:
			return new ContainerInfusionBox(player, world.getTileEntity(new BlockPos(x, y, z)));
		case GUI_PARCHMENT:
			return new ContainerParchment(player);
		case GUI_ELEMENT_CRAFTING_TABLE:
			return new ContainerElementCraftingTable(player, world.getTileEntity(new BlockPos(x, y, z)));
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case GUI_HEARTH:
			return new GuiHearth(new ContainerHearth(player, world.getTileEntity(new BlockPos(x, y, z))),
					player.inventory);
		case GUI_SMELT_BOX:
			return new GuiSmeltBox(new ContainerSmeltBox(player, world.getTileEntity(new BlockPos(x, y, z))),
					player.inventory);
		case GUI_ABSORB_BOX:
			return new GuiAbsorbBox(new ContainerAbsorbBox(player, world.getTileEntity(new BlockPos(x, y, z))),
					player.inventory);
		case GUI_ELEMENT_WORKBENCH:
			return new GuiElementWorkbench(
					new ContainerElementWorkbench(player.inventory, world, new BlockPos(x, y, z)), player.inventory);
		case GUI_DECONSTRUCT_BOX:
			return new GuiDeconstructBox(
					new ContainerDeconstructBox(player, world.getTileEntity(new BlockPos(x, y, z))), player.inventory);
		case GUI_INFUSION_BOX:
			return new GuiInfusionBox(new ContainerInfusionBox(player, world.getTileEntity(new BlockPos(x, y, z))),
					player.inventory);
		case GUI_PARCHMENT:
			return new GuiParchment(new ContainerParchment(player));
		case GUI_ELEMENT_CRAFTING_TABLE:
			return new GuiElementCraftingTable(
					new ContainerElementCraftingTable(player, world.getTileEntity(new BlockPos(x, y, z))),
					player.inventory);
		default:
			return null;
		}
	}

}
