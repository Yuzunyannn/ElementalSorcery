package yuzunyannn.elementalsorcery.event;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Random;

import org.apache.commons.compress.utils.IOUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.block.BlockMagicTorch;
import yuzunyannn.elementalsorcery.building.ArcInfo;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.building.BuildingSaveData;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemMagicRuler;
import yuzunyannn.elementalsorcery.tile.TileStela;
import yuzunyannn.elementalsorcery.worldgen.VillageESHall.VillageCreationHandler;

public class ESTestAndDebug {

	public ESTestAndDebug() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	static BlockPos pos = new BlockPos(0, 0, 0);

	@SubscribeEvent
	public void click(PlayerInteractEvent event) {
		if (!event.getWorld().isRemote) {
			BlockPos pos = event.getPos();
			IBlockState state = event.getWorld().getBlockState(pos);
			//System.out.println(state.getBlockHardness(event.getWorld(), pos));
//			TileEntity tile = event.getWorld().getTileEntity(pos);
//			if (tile instanceof TileStela) {
//				((TileStela) tile).doOnce();
//			}
//			if (event.getWorld().getBlockState(pos).getBlock() == ESInitInstance.BLOCKS.MAGIC_TORCH) {
//				event.getWorld().setBlockState(pos,
//						event.getWorld().getBlockState(pos).withProperty(BlockMagicTorch.LIT, true));
//			}
			return;
		}

//		System.out.println("Server ArcInfo");
		BlockPos pos = event.getPos();
		IBlockState state = event.getWorld().getBlockState(pos);
//		System.out.println(state);
		if (event.getEntityPlayer().isSneaking()) {
			ESTestAndDebug.pos = event.getPos().up();
		}

	}

	public static class DebugCmd extends CommandBase {

		@Override
		public String getName() {
			return "esd";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/esd xxx";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0) {

			} else if (args.length == 1) {
				if (args[0].equals("rb")) {
					System.out.println("开始记录");
					Entity entity = sender.getCommandSenderEntity();
					if (entity instanceof EntityPlayer) {
						ItemStack ruler = ((EntityPlayer) entity).getHeldItem(EnumHand.OFF_HAND);
						ItemStack ar = ((EntityPlayer) entity).getHeldItem(EnumHand.MAIN_HAND);
						Building building = Building.createBuilding(sender.getEntityWorld(), EnumFacing.NORTH,
								ItemMagicRuler.getRulerPos(ruler, true), ItemMagicRuler.getRulerPos(ruler, false));
						building.setAuthor(sender.getName());
						BuildingLib.instance.addBuilding(building);
						ArcInfo.initArcInfoToItem(ar, building.getKeyName());
						System.out.println("记录完成！");
					}
				} else if (args[0].equals("sb")) {
					System.out.println("开始存储");
					Entity entity = sender.getCommandSenderEntity();
					ItemStack ruler = ((EntityPlayer) entity).getHeldItem(EnumHand.OFF_HAND);
					if (ItemMagicRuler.getRulerPos(ruler, true) == null
							|| ItemMagicRuler.getRulerPos(ruler, false) == null)
						ruler = ((EntityPlayer) entity).getHeldItem(EnumHand.MAIN_HAND);
					if (ItemMagicRuler.getRulerPos(ruler, true) == null
							|| ItemMagicRuler.getRulerPos(ruler, false) == null)
						throw new WrongUsageException("保存建筑失败，请将魔力标尺放在手上");
					Building building = Building.createBuilding(sender.getEntityWorld(), EnumFacing.NORTH,
							ItemMagicRuler.getRulerPos(ruler, true), ItemMagicRuler.getRulerPos(ruler, false));
					building.setAuthor(sender.getName());
					BuildingSaveData.debugSetKeyName(building);
					File file = ElementalSorcery.data.getFile("building/debug",
							BuildingSaveData.randomKeyName(entity.getName()));
					OutputStream output = null;
					try {
						output = new FileOutputStream(file);
						CompressedStreamTools.writeCompressed(building.serializeNBT(), output);
						sender.sendMessage(new TextComponentString("建筑储存在了:" + file.getPath()));
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						IOUtils.closeQuietly(output);
					}

				} else if (args[0].equals("doit")) {
					BlockPos pos = ESTestAndDebug.pos;
					VillageCreationHandler h = new VillageCreationHandler();
					StructureVillagePieces.Village v = h.buildComponent(null, null,
							new LinkedList<StructureComponent>(), new Random(), pos.getX(), pos.getY(), pos.getZ(),
							EnumFacing.NORTH, 0);
					try {
						Field field = StructureVillagePieces.Village.class.getDeclaredField("averageGroundLvl");
						field.setAccessible(true);
						field.setInt(v, pos.getY());
					} catch (Exception e) {
						e.printStackTrace();
					}
					v.addComponentParts(sender.getEntityWorld(), new Random(), v.getBoundingBox());
				} else
					throw new WrongUsageException("ES dubg 指令无效，随便使用可能会导致崩溃");
			} else
				throw new WrongUsageException("ES dubg 指令无效，随便使用可能会导致崩溃");
		}
	}
}
