package yuzunyannn.elementalsorcery.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.building.ArcInfo;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.elf.edifice.BuilderWithInfo;
import yuzunyannn.elementalsorcery.elf.edifice.ElfEdificeFloor;
import yuzunyannn.elementalsorcery.elf.edifice.FloorInfo;
import yuzunyannn.elementalsorcery.elf.edifice.GenElfEdifice;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class CommandES extends CommandBase {

	@Override
	public String getName() {
		return "es";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.es.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1) throw new CommandException("commands.es.usage");
		if (args[0].equals("build")) {
			// 建筑
			if (args.length == 1) throw new CommandException("commands.es.build.usage");
			Entity entity = sender.getCommandSenderEntity();
			this.cmdBuild(args[1], (EntityLivingBase) entity, server.getEntityWorld());
		} else if (args[0].equals("buildFloor")) {
			if (args.length == 1) throw new CommandException("commands.es.buildFloor.usage");
			Entity entity = sender.getCommandSenderEntity();
			this.cmdBuildFloor(args[1], (EntityLivingBase) entity, server.getEntityWorld());
		} else if (args[0].equals("page")) {
			// 页面
			if (args.length == 1) throw new CommandException("commands.es.page.usage");
			String idStr = args[1];
			if (!Pages.isVaild(idStr)) throw new CommandException("commands.es.page.fail");
			Entity entity = sender.getCommandSenderEntity();
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (Pages.BOOK.equals(idStr))
					player.inventory.addItemStackToInventory(new ItemStack(ESInitInstance.ITEMS.MANUAL));
				else player.inventory.addItemStackToInventory(ItemParchment.getParchment(idStr));
			}
		} else if ("debug".equals(args[0])) {
			// debug
			if (args.length == 1) throw new CommandException("ES dubug 指令无效，随便使用debug指令可能会导致崩溃");
			CommandESDebug.execute(server, sender, Arrays.copyOfRange(args, 1, args.length));
		} else throw new CommandException("commands.es.usage");
	}

	// 自动补全
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			@Nullable BlockPos targetPos) {
		if (args.length == 1) {
			String[] names = { "build", "page", "debug", "buildFloor" };
			return CommandBase.getListOfStringsMatchingLastWord(args, names);
		} else if (args.length == 2) {
			if (args[0].equals("build")) {
				Collection<Building> bs = BuildingLib.instance.getBuildingsFromLib();
				List<String> arrayList = new ArrayList<>(bs.size() + 1);
				for (Building b : bs) arrayList.add(b.getKeyName());
				arrayList.add("it");
				List<String> tips = CommandBase.getListOfStringsMatchingLastWord(args, arrayList);
				return tips;
			} else if (args[0].equals("buildFloor")) {
				Collection<ResourceLocation> bs = ElfEdificeFloor.REGISTRY.getKeys();
				return CommandBase.getListOfStringsMatchingLastWord(args, bs);
			} else if (args[0].equals("page")) {
				Set<String> set = Pages.getPageIds();
				String[] names = new String[set.size()];
				set.toArray(names);
				return CommandBase.getListOfStringsMatchingLastWord(args, names);
			} else if ("debug".equals(args[0])) {
				return CommandBase.getListOfStringsMatchingLastWord(args, CommandESDebug.autoTips);
			}
		}
		return null;
	}

	/** 建造建筑 */
	private void cmdBuild(String value, EntityLivingBase entity, World world) throws CommandException {
		Building building = null;
		BlockPos pos = null;
		if (value.toLowerCase().equals("it")) {
			ItemStack stack = entity.getHeldItemMainhand();
			ArcInfo info = new ArcInfo(stack, world.isRemote ? Side.CLIENT : Side.SERVER);
			if (!info.isValid()) throw new CommandException("commands.es.build.it.usage");
			building = info.building;
			pos = info.pos;
		} else {
			building = BuildingLib.instance.getBuilding(value);
			RayTraceResult result = WorldHelper.getLookAtBlock(world, entity, 128);
			if (result != null) pos = result.getBlockPos();
		}
		if (building == null || pos == null) throw new CommandException("commands.es.build.fail");
		Building.BuildingBlocks iter = building.getBuildingIterator();
		while (iter.next()) {
			world.setBlockState(iter.getPos().add(pos), iter.getState());
		}
	}

	private void cmdBuildFloor(String value, EntityLivingBase entity, World world) throws CommandException {
		BlockPos pos = null;
		ElfEdificeFloor floor = ElfEdificeFloor.REGISTRY.getValue(new ResourceLocation(value));
		RayTraceResult result = WorldHelper.getLookAtBlock(world, entity, 128);
		if (result != null) pos = result.getBlockPos();
		if (floor == null || pos == null) throw new CommandException("commands.es.build.fail");
		// 模拟建造
		FloorInfo info = new FloorInfo(floor, pos.up());
		BuilderWithInfo builder = new BuilderWithInfo(entity.world, info, GenElfEdifice.EDIFICE_SIZE, 60, pos.up(60));
		info.setFloorData(info.getType().getBuildData(builder, entity.getRNG()));
		info.getType().build(builder);
		builder.buildAll();
		info.getType().surprise(builder, entity.getRNG());
	}
}
