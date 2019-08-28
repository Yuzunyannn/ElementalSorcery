package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
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
import yuzunyannn.elementalsorcery.container.gui.GuiMDHearth;
import yuzunyannn.elementalsorcery.container.gui.GuiMDInfusion;
import yuzunyannn.elementalsorcery.container.gui.GuiMDMagicGen;
import yuzunyannn.elementalsorcery.container.gui.GuiMDMagicSolidify;
import yuzunyannn.elementalsorcery.container.gui.GuiMDRubbleRepair;
import yuzunyannn.elementalsorcery.container.gui.GuiParchment;
import yuzunyannn.elementalsorcery.container.gui.GuiSimple;
import yuzunyannn.elementalsorcery.container.gui.GuiSmeltBox;
import yuzunyannn.elementalsorcery.container.gui.GuiSupremeCraftingTable;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;

public class ESGuiHandler implements IGuiHandler {

	public static final int GUI_HEARTH = 1;
	public static final int GUI_SMELT_BOX = 2;
	public static final int GUI_ABSORB_BOX = 3;
	public static final int GUI_ELEMENT_WORKBENCH = 4;
	public static final int GUI_DECONSTRUCT_BOX = 5;
	public static final int GUI_PARCHMENT = 6;
	public static final int GUI_ELEMENT_CRAFTING_TABLE = 7;
	public static final int GUI_ANALYSIS_ALTAR = 8;
	public static final int GUI_SUPREME_CRAFTING_TABLE = 9;
	public static final int GUI_INVENTORY_WORKBENCH = 10;

	public static final int GUI_MD_MAGIC_GEN = 21;
	public static final int GUI_MD_HEARTH = 22;
	public static final int GUI_MD_RUBBLE_REPAIR = 23;
	public static final int GUI_MD_INFUSION = 24;
	public static final int GUI_MD_MAGIC_SOLIDIFY = 25;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		switch (ID) {
		case GUI_HEARTH:
			return new ContainerHearth(player, tileEntity);
		case GUI_SMELT_BOX:
			return new ContainerSmeltBox(player, tileEntity);
		case GUI_ABSORB_BOX:
			return new ContainerAbsorbBox(player, tileEntity);
		case GUI_ELEMENT_WORKBENCH:
			return new ContainerElementWorkbench(player.inventory, world, new BlockPos(x, y, z));
		case GUI_DECONSTRUCT_BOX:
			return new ContainerDeconstructBox(player, tileEntity);
		case GUI_PARCHMENT:
			return new ContainerParchment(player);
		case GUI_ELEMENT_CRAFTING_TABLE:
			return new ContainerElementCraftingTable(player, tileEntity);
		case GUI_ANALYSIS_ALTAR:
			return new ContainerAnalysisAltar(player, tileEntity);
		case GUI_SUPREME_CRAFTING_TABLE:
			return new ContainerSupremeCraftingTable(player, tileEntity);
		case GUI_INVENTORY_WORKBENCH:
			return new ContainerWorkbenchWithInventory(player, tileEntity);
		case GUI_MD_MAGIC_GEN:
			return new ContainerMDMagicGen(player, tileEntity);
		case GUI_MD_HEARTH:
			return new ContainerMDBase(player, (TileMDBase) tileEntity);
		case GUI_MD_RUBBLE_REPAIR:
			return new ContainerMDRubbleRepair(player, tileEntity);
		case GUI_MD_INFUSION:
			return new ContainerMDInfusion(player, tileEntity);
		case GUI_MD_MAGIC_SOLIDIFY:
			return new ContainerMDMagicSolidify(player, tileEntity);
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		switch (ID) {
		case GUI_HEARTH:
			return new GuiHearth(new ContainerHearth(player, tileEntity), player.inventory);
		case GUI_SMELT_BOX:
			return new GuiSmeltBox(new ContainerSmeltBox(player, tileEntity), player.inventory);
		case GUI_ABSORB_BOX:
			return new GuiAbsorbBox(new ContainerAbsorbBox(player, tileEntity), player.inventory);
		case GUI_ELEMENT_WORKBENCH:
			return new GuiElementWorkbench(
					new ContainerElementWorkbench(player.inventory, world, new BlockPos(x, y, z)), player.inventory);
		case GUI_DECONSTRUCT_BOX:
			return new GuiDeconstructBox(new ContainerDeconstructBox(player, tileEntity), player.inventory);
		case GUI_PARCHMENT:
			return new GuiParchment(new ContainerParchment(player));
		case GUI_ELEMENT_CRAFTING_TABLE:
			return new GuiElementCraftingTable(new ContainerElementCraftingTable(player, tileEntity), player.inventory);
		case GUI_ANALYSIS_ALTAR:
			return new GuiAnalysisAltar(new ContainerAnalysisAltar(player, tileEntity), player.inventory);
		case GUI_SUPREME_CRAFTING_TABLE:
			return new GuiSupremeCraftingTable(new ContainerSupremeCraftingTable(player, tileEntity), player.inventory);
		case GUI_INVENTORY_WORKBENCH:
			return new GuiSimple(new ContainerWorkbenchWithInventory(player, tileEntity), player.inventory,
					"tile.supremeCraftingTable.name", TEXTURE_CRAFTING_TABLE);
		case GUI_MD_MAGIC_GEN:
			return new GuiMDMagicGen(new ContainerMDMagicGen(player, tileEntity), player.inventory);
		case GUI_MD_HEARTH:
			return new GuiMDHearth(new ContainerMDBase(player, (TileMDBase) tileEntity), player.inventory);
		case GUI_MD_RUBBLE_REPAIR:
			return new GuiMDRubbleRepair(new ContainerMDRubbleRepair(player, tileEntity), player.inventory);
		case GUI_MD_INFUSION:
			return new GuiMDInfusion(new ContainerMDInfusion(player, tileEntity), player.inventory);
		case GUI_MD_MAGIC_SOLIDIFY:
			return new GuiMDMagicSolidify(new ContainerMDMagicSolidify(player, tileEntity), player.inventory);
		default:
			return null;
		}
	}

	public static final ResourceLocation TEXTURE_CRAFTING_TABLE = new ResourceLocation("minecraft",
			"textures/gui/container/crafting_table.png");

}
