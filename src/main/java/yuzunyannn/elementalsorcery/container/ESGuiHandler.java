package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import yuzunyannn.elementalsorcery.container.gui.GuiAbsorbBox;
import yuzunyannn.elementalsorcery.container.gui.GuiAnalysisAltar;
import yuzunyannn.elementalsorcery.container.gui.GuiDeconstructBox;
import yuzunyannn.elementalsorcery.container.gui.GuiElementCraftingTable;
import yuzunyannn.elementalsorcery.container.gui.GuiElementWorkbench;
import yuzunyannn.elementalsorcery.container.gui.GuiHearth;
import yuzunyannn.elementalsorcery.container.gui.GuiInfusionBox;
import yuzunyannn.elementalsorcery.container.gui.GuiMDMagicGen;
import yuzunyannn.elementalsorcery.container.gui.GuiParchment;
import yuzunyannn.elementalsorcery.container.gui.GuiSimple;
import yuzunyannn.elementalsorcery.container.gui.GuiSmeltBox;
import yuzunyannn.elementalsorcery.container.gui.GuiSupremeCraftingTable;

public class ESGuiHandler implements IGuiHandler {

	public static final int GUI_HEARTH = 1;
	public static final int GUI_SMELT_BOX = 2;
	public static final int GUI_ABSORB_BOX = 3;
	public static final int GUI_ELEMENT_WORKBENCH = 4;
	public static final int GUI_DECONSTRUCT_BOX = 5;
	public static final int GUI_INFUSION_BOX = 6;
	public static final int GUI_PARCHMENT = 7;
	public static final int GUI_ELEMENT_CRAFTING_TABLE = 8;
	public static final int GUI_ANALYSIS_ALTAR = 9;
	public static final int GUI_SUPREME_CRAFTING_TABLE = 10;
	public static final int GUI_INVENTORY_WORKBENCH = 11;
	public static final int GUI_MD_MAGIC_GEN = 12;

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
		case GUI_ANALYSIS_ALTAR:
			return new ContainerAnalysisAltar(player, world.getTileEntity(new BlockPos(x, y, z)));
		case GUI_SUPREME_CRAFTING_TABLE:
			return new ContainerSupremeCraftingTable(player, world.getTileEntity(new BlockPos(x, y, z)));
		case GUI_INVENTORY_WORKBENCH:
			return new ContainerWorkbenchWithInventory(player, world.getTileEntity(new BlockPos(x, y, z)));
		case GUI_MD_MAGIC_GEN:
			return new ContainerMDMagicGen(player, world.getTileEntity(new BlockPos(x, y, z)));
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
		case GUI_ANALYSIS_ALTAR:
			return new GuiAnalysisAltar(new ContainerAnalysisAltar(player, world.getTileEntity(new BlockPos(x, y, z))),
					player.inventory);
		case GUI_SUPREME_CRAFTING_TABLE:
			return new GuiSupremeCraftingTable(
					new ContainerSupremeCraftingTable(player, world.getTileEntity(new BlockPos(x, y, z))),
					player.inventory);
		case GUI_INVENTORY_WORKBENCH:
			return new GuiSimple(
					new ContainerWorkbenchWithInventory(player, world.getTileEntity(new BlockPos(x, y, z))),
					player.inventory, "tile.supremeCraftingTable.name", TEXTURE_CRAFTING_TABLE);
		case GUI_MD_MAGIC_GEN:
			return new GuiMDMagicGen(new ContainerMDMagicGen(player, world.getTileEntity(new BlockPos(x, y, z))),
					player.inventory);
		default:
			return null;
		}
	}

	public static final ResourceLocation TEXTURE_CRAFTING_TABLE = new ResourceLocation("minecraft",
			"textures/gui/container/crafting_table.png");

}
