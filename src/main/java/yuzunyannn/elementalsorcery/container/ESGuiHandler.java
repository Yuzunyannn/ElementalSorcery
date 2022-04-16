package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.gui.GuiAnalysisAltar;
import yuzunyannn.elementalsorcery.container.gui.GuiDevolveCube;
import yuzunyannn.elementalsorcery.container.gui.GuiElementBoard;
import yuzunyannn.elementalsorcery.container.gui.GuiElementCraftingTable;
import yuzunyannn.elementalsorcery.container.gui.GuiElementInventoryStronger;
import yuzunyannn.elementalsorcery.container.gui.GuiElementReactor;
import yuzunyannn.elementalsorcery.container.gui.GuiElementTranslocator;
import yuzunyannn.elementalsorcery.container.gui.GuiElementWorkbench;
import yuzunyannn.elementalsorcery.container.gui.GuiElfApplyAddressPlate;
import yuzunyannn.elementalsorcery.container.gui.GuiElfSendParcel;
import yuzunyannn.elementalsorcery.container.gui.GuiElfTalk;
import yuzunyannn.elementalsorcery.container.gui.GuiElfTrade;
import yuzunyannn.elementalsorcery.container.gui.GuiElfTreeElevator;
import yuzunyannn.elementalsorcery.container.gui.GuiFairyCube;
import yuzunyannn.elementalsorcery.container.gui.GuiHearth;
import yuzunyannn.elementalsorcery.container.gui.GuiItemStructureCraft;
import yuzunyannn.elementalsorcery.container.gui.GuiMDAbsorbBox;
import yuzunyannn.elementalsorcery.container.gui.GuiMDDeconstructBox;
import yuzunyannn.elementalsorcery.container.gui.GuiMDHearth;
import yuzunyannn.elementalsorcery.container.gui.GuiMDInfusion;
import yuzunyannn.elementalsorcery.container.gui.GuiMDMagicGen;
import yuzunyannn.elementalsorcery.container.gui.GuiMDMagicSolidify;
import yuzunyannn.elementalsorcery.container.gui.GuiMDMagiclization;
import yuzunyannn.elementalsorcery.container.gui.GuiMDRubbleRepair;
import yuzunyannn.elementalsorcery.container.gui.GuiMantraShitf;
import yuzunyannn.elementalsorcery.container.gui.GuiParchment;
import yuzunyannn.elementalsorcery.container.gui.GuiQuest;
import yuzunyannn.elementalsorcery.container.gui.GuiResearch;
import yuzunyannn.elementalsorcery.container.gui.GuiRiteManual;
import yuzunyannn.elementalsorcery.container.gui.GuiSimple;
import yuzunyannn.elementalsorcery.container.gui.GuiSmeltBox;
import yuzunyannn.elementalsorcery.container.gui.GuiSupremeTable;
import yuzunyannn.elementalsorcery.container.gui.GuiTranscribeInjection;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class ESGuiHandler implements IGuiHandler {

	public static final int GUI_HEARTH = 1;
	public static final int GUI_SMELT_BOX = 2;
	public static final int GUI_NONE1 = 3;
	public static final int GUI_ELEMENT_WORKBENCH = 4;
	public static final int GUI_NONE2 = 5;
	public static final int GUI_PARCHMENT = 6;
	public static final int GUI_ELEMENT_CRAFTING_TABLE = 7;
	public static final int GUI_ANALYSIS_ALTAR = 8;
	public static final int GUI_SUPREME_TABLE = 9;
	public static final int GUI_INVENTORY_WORKBENCH = 10;
	public static final int GUI_RITE_MANUAL = 11;
	public static final int GUI_ITEM_STRUCTURE_CRAFT = 12;
	public static final int GUI_TRANSCRIBE_INJECTION = 13;
	public static final int GUI_RESEARCHER = 14;
	public static final int GUI_ELEMENT_BOARD = 15;
	public static final int GUI_ELEMENT_TRANSLOCATOR = 16;
	public static final int GUI_DEVOLVE_CUBE = 17;
	public static final int GUI_ELEMENT_INVENTORY_STRONGER = 18;
	public static final int GUI_ELEMENT_REACTOR = 19;

	public static final int GUI_MD_MAGIC_GEN = 21;
	public static final int GUI_MD_HEARTH = 22;
	public static final int GUI_MD_RUBBLE_REPAIR = 23;
	public static final int GUI_MD_INFUSION = 24;
	public static final int GUI_MD_MAGIC_SOLIDIFY = 25;
	public static final int GUI_MD_ABSORB_BOX = 26;
	public static final int GUI_MD_MAGICLIZATION = 27;
	public static final int GUI_MD_DECONSTRUCTBOX = 28;

	public static final int GUI_FAIRY_CUBE = 38;
	public static final int GUI_QUEST = 39;
	public static final int GUI_ELF_TALK = 40;
	public static final int GUI_ELF_TRADE = 41;
	public static final int GUI_ELF_SEND_PARCEL = 42;
	public static final int GUI_ELF_APPLY_ADDRESS_PLATE = 43;

	// 切换只有客户端存在
	public static final int GUI_MANTRA_SHITF = 60;
	public static final int GUI_ELF_TREE_ELEVATOR = 61;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		try {
			BlockPos pos = new BlockPos(x, y, z);
			TileEntity tileEntity = world.getTileEntity(pos);
			switch (ID) {
			case GUI_HEARTH:
				return new ContainerHearth(player, tileEntity);
			case GUI_SMELT_BOX:
				return new ContainerSmeltBox(player, tileEntity);
			case GUI_ELEMENT_WORKBENCH:
				return new ContainerElementWorkbench(player.inventory, world, pos);
			case GUI_PARCHMENT:
				return new ContainerParchment(player);
			case GUI_ELEMENT_CRAFTING_TABLE:
				return new ContainerElementCraftingTable(player, tileEntity);
			case GUI_ANALYSIS_ALTAR:
				return new ContainerAnalysisAltar(player, tileEntity);
			case GUI_SUPREME_TABLE:
				return new ContainerSupremeTable(player, tileEntity);
			case GUI_INVENTORY_WORKBENCH:
				return new ContainerWorkbenchWithInventory(player, tileEntity);
			case GUI_RITE_MANUAL:
				return new ContainerRiteManual(player);
			case GUI_ITEM_STRUCTURE_CRAFT:
				return new ContainerItemStructureCraft(player, tileEntity);
			case GUI_TRANSCRIBE_INJECTION:
				return new ContainerTranscribeInjection(player, tileEntity);
			case GUI_RESEARCHER:
				return new ContainerResearch(player, pos);
			case GUI_ELEMENT_BOARD:
				return new ContainerElementBoard(player);
			case GUI_ELEMENT_TRANSLOCATOR:
				return new ContainerElementTranslocator(player, tileEntity);
			case GUI_DEVOLVE_CUBE:
				return new ContainerDevolveCube(player, tileEntity);
			case GUI_ELEMENT_INVENTORY_STRONGER:
				return new ContainerElementInventoryStronger(player, tileEntity);
			case GUI_ELEMENT_REACTOR:
				return new ContainerElementReactor(player, tileEntity);

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
			case GUI_MD_ABSORB_BOX:
				return new ContainerMDAbsorbBox(player, tileEntity);
			case GUI_MD_MAGICLIZATION:
				return new ContainerMDMagiclization(player, tileEntity);
			case GUI_MD_DECONSTRUCTBOX:
				return new ContainerMDDeconstructBox(player, tileEntity);
			case GUI_FAIRY_CUBE:
				return new ContainerFairyCube(player);
			case GUI_QUEST:
				return new ContainerQuest(player);
			case GUI_ELF_TALK:
				return new ContainerElfTalk(player);
			case GUI_ELF_TRADE:
				return new ContainerElfTrade(player);
			case GUI_ELF_SEND_PARCEL:
				return new ContainerElfSendParcel(player);
			case GUI_ELF_APPLY_ADDRESS_PLATE:
				return new ContainerElfApplyAddressPlate(player);
			default:
				return null;
			}
		} catch (Exception e) {
			ElementalSorcery.logger.warn("ui创建出现异常", e);
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		try {
			BlockPos pos = new BlockPos(x, y, z);
			TileEntity tileEntity = world.getTileEntity(pos);
			switch (ID) {
			case GUI_HEARTH:
				return new GuiHearth(new ContainerHearth(player, tileEntity));
			case GUI_SMELT_BOX:
				return new GuiSmeltBox(new ContainerSmeltBox(player, tileEntity));
			case GUI_ELEMENT_WORKBENCH:
				return new GuiElementWorkbench(new ContainerElementWorkbench(player.inventory, world, pos),
						player.inventory);
			case GUI_PARCHMENT:
				return new GuiParchment(new ContainerParchment(player));
			case GUI_ELEMENT_CRAFTING_TABLE:
				return new GuiElementCraftingTable(new ContainerElementCraftingTable(player, tileEntity),
						player.inventory);
			case GUI_ANALYSIS_ALTAR:
				return new GuiAnalysisAltar(new ContainerAnalysisAltar(player, tileEntity));
			case GUI_SUPREME_TABLE:
				return new GuiSupremeTable(new ContainerSupremeTable(player, tileEntity));
			case GUI_INVENTORY_WORKBENCH:
				return new GuiSimple(new ContainerWorkbenchWithInventory(player, tileEntity), player.inventory,
						"tile.supremeTable.name", TEXTURE_CRAFTING_TABLE, 0x1a1a45);
			case GUI_RITE_MANUAL:
				return new GuiRiteManual(new ContainerRiteManual(player));
			case GUI_ITEM_STRUCTURE_CRAFT:
				return new GuiItemStructureCraft(new ContainerItemStructureCraft(player, tileEntity));
			case GUI_TRANSCRIBE_INJECTION:
				return new GuiTranscribeInjection(new ContainerTranscribeInjection(player, tileEntity));
			case GUI_RESEARCHER:
				return new GuiResearch(player, pos);
			case GUI_ELEMENT_BOARD:
				return new GuiElementBoard(new ContainerElementBoard(player));
			case GUI_ELEMENT_TRANSLOCATOR:
				return new GuiElementTranslocator(new ContainerElementTranslocator(player, tileEntity));
			case GUI_DEVOLVE_CUBE:
				return new GuiDevolveCube(new ContainerDevolveCube(player, tileEntity));
			case GUI_ELEMENT_INVENTORY_STRONGER:
				return new GuiElementInventoryStronger(new ContainerElementInventoryStronger(player, tileEntity));
			case GUI_ELEMENT_REACTOR:
				return new GuiElementReactor(new ContainerElementReactor(player, tileEntity));

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
			case GUI_MD_ABSORB_BOX:
				return new GuiMDAbsorbBox(new ContainerMDAbsorbBox(player, tileEntity), player.inventory);
			case GUI_MD_MAGICLIZATION:
				return new GuiMDMagiclization(new ContainerMDMagiclization(player, tileEntity), player.inventory);
			case GUI_MD_DECONSTRUCTBOX:
				return new GuiMDDeconstructBox(new ContainerMDDeconstructBox(player, tileEntity), player.inventory);
			case GUI_FAIRY_CUBE:
				return new GuiFairyCube(new ContainerFairyCube(player));
			case GUI_QUEST:
				return new GuiQuest(player);
			case GUI_ELF_TALK:
				return new GuiElfTalk(player);
			case GUI_ELF_TRADE:
				return new GuiElfTrade(player);
			case GUI_ELF_SEND_PARCEL:
				return new GuiElfSendParcel(player);
			case GUI_ELF_APPLY_ADDRESS_PLATE:
				return new GuiElfApplyAddressPlate(player);
			case GUI_MANTRA_SHITF:
				return new GuiMantraShitf(player);
			case GUI_ELF_TREE_ELEVATOR:
				return new GuiElfTreeElevator(player, new BlockPos(x, y, z));
			default:
				return null;
			}
		} catch (Exception e) {
			ElementalSorcery.logger.warn("ui创建出现异常", e);
			return null;
		}
	}

	public static final ResourceLocation TEXTURE_CRAFTING_TABLE = TextHelper
			.toESResourceLocation("textures/gui/container/crafting_table_altar.png");

}
