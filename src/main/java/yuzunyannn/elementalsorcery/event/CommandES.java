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
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.building.ArcInfo;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.elf.edifice.BuilderWithInfo;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorHall;
import yuzunyannn.elementalsorcery.elf.edifice.ElfEdificeFloor;
import yuzunyannn.elementalsorcery.elf.edifice.FloorInfo;
import yuzunyannn.elementalsorcery.elf.edifice.GenElfEdifice;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.elf.research.AncientPaper;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper.EnumType;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.text.TextHelper;
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
		switch (args[0]) {
		case "build":// 建筑
		{
			if (args.length == 1) throw new WrongUsageException("commands.es.build.usage");
			Entity entity = sender.getCommandSenderEntity();
			this.cmdBuild(args[1], (EntityLivingBase) entity, server.getEntityWorld());
			return;
		}
		case "buildFloor": {
			if (args.length == 1) throw new WrongUsageException("commands.es.buildFloor.usage");
			Entity entity = sender.getCommandSenderEntity();
			this.cmdBuildFloor(args[1], (EntityLivingBase) entity, server.getEntityWorld());
			return;
		}
		case "page":
		// 页面
		{
			if (args.length == 1) throw new WrongUsageException("commands.es.page.usage");
			String idStr = args[1];
			if (!Pages.isVaild(idStr)) throw new CommandException("commands.es.page.fail");
			Entity entity = sender.getCommandSenderEntity();
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (Pages.BOOK.equals(idStr))
					player.inventory.addItemStackToInventory(new ItemStack(ESInit.ITEMS.MANUAL));
				else player.inventory.addItemStackToInventory(ItemParchment.getParchment(idStr));
			}
			return;
		}
		case "quest": {
			if (args.length == 1) throw new CommandException("commands.es.quest.usage");
			this.cmdQuest(Arrays.copyOfRange(args, 1, args.length), server, sender);
			return;
		}
		case "mantra": {
			if (args.length < 3) throw new CommandException("commands.es.mantra.usage");
			this.cmdMantra(Arrays.copyOfRange(args, 1, args.length), server, sender);
			return;
		}
		case "debug":
		// debug
		{
			if (args.length == 1) throw new CommandException("ES dubug 指令无效，随便使用debug指令可能会导致崩溃");
			CommandESDebug.execute(server, sender, Arrays.copyOfRange(args, 1, args.length));
			return;
		}
		default:
			throw new CommandException("commands.es.usage");
		}
	}

	// 自动补全
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			@Nullable BlockPos targetPos) {
		if (args.length == 1) {
			String[] names = { "build", "page", "debug", "buildFloor", "quest", "mantra" };
			return CommandBase.getListOfStringsMatchingLastWord(args, names);
		} else if (args.length >= 2) {
			switch (args[0]) {
			case "build": {
				Collection<Building> bs = BuildingLib.instance.getBuildingsFromLib();
				List<String> arrayList = new ArrayList<>(bs.size() + 1);
				for (Building b : bs) arrayList.add(b.getKeyName());
				arrayList.add("it");
				List<String> tips = getListOfStringsMatchingLastWord(args, arrayList);
				return tips;
			}
			case "buildFloor": {
				Collection<ResourceLocation> bs = ElfEdificeFloor.REGISTRY.getKeys();
				return getListOfStringsMatchingLastWord(args, bs);
			}
			case "page": {
				Set<String> set = Pages.getPageIds();
				String[] names = new String[set.size()];
				set.toArray(names);
				return getListOfStringsMatchingLastWord(args, names);
			}
			case "quest": {
				if (args.length > 2) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
				return getListOfStringsMatchingLastWord(args, "clear");
			}
			case "mantra": {
				if (args.length > 3) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
				if (args.length > 2) return getListOfStringsMatchingLastWord(args, Mantra.REGISTRY.getKeys());
				return getListOfStringsMatchingLastWord(args, "give", "remove", "add");
			}
			case "debug": {
				return getListOfStringsMatchingLastWord(args, CommandESDebug.autoTips);
			}
			default:
			}
		}
		return CommandBase.getListOfStringsMatchingLastWord(args);
	}

	private void cmdMantra(String[] args, MinecraftServer server, ICommandSender sender) throws CommandException {
		EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
		if (args.length > 2) player = getPlayer(server, sender, args[1]);
		ResourceLocation id = TextHelper.toESResourceLocation(args[1]);
		Mantra mantra = Mantra.REGISTRY.getValue(id);
		if (mantra == null) throw new CommandException("commands.es.mantra.notFound", id.toString());

		ItemStack grimoireStack = player.getHeldItem(EnumHand.MAIN_HAND);
		Grimoire grimoire = grimoireStack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		switch (args[0]) {
		case "give":
			AncientPaper ap = new AncientPaper();
			grimoireStack = new ItemStack(ESInit.ITEMS.ANCIENT_PAPER, 1, EnumType.NEW_WRITTEN.getMetadata());
			ap.setMantra(mantra).setStart(0).setEnd(100);
			ap.saveState(grimoireStack);
			ItemHelper.addItemStackToPlayer(player, grimoireStack);
			notifyCommandListener(sender, this, "commands.es.mantra.give", player.getName(), mantra.getTextComponent());
			return;
		case "remove":
			if (grimoire == null) {
				throw new CommandException("commands.es.notFound",
						grimoireStack.getTextComponent().setStyle(new Style().setColor(TextFormatting.RED)));
			}
			grimoire.loadState(grimoireStack);
			grimoire.remove(mantra);
			grimoire.saveState(grimoireStack);
			notifyCommandListener(sender, this, "commands.es.mantra.remove", player.getName(),
					mantra.getTextComponent());
			return;
		case "add":
			if (grimoire == null) {
				throw new CommandException("commands.es.notFound",
						grimoireStack.getTextComponent().setStyle(new Style().setColor(TextFormatting.RED)));
			}
			grimoire.loadState(grimoireStack);
			grimoire.add(mantra);
			grimoire.saveState(grimoireStack);
			notifyCommandListener(sender, this, "commands.es.mantra.add", player.getName(), mantra.getTextComponent());
			return;
		default:
			throw new CommandException("commands.es.mantra.usage");
		}

	}

	private void cmdQuest(String[] args, MinecraftServer server, ICommandSender sender) throws CommandException {
		switch (args[0]) {
		case "clear": {
			EntityLivingBase player = (EntityLivingBase) sender.getCommandSenderEntity();
			if (args.length > 1) player = getPlayer(server, sender, args[1]);
			IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
			if (adventurer != null) adventurer.removeAllQuest();
			notifyCommandListener(sender, this, "commands.es.quest.clear", player.getName());
			return;
		}
		default:
			throw new CommandException("commands.es.quest.usage");
		}
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
		TileElfTreeCore core = BlockHelper.getTileEntity(world, pos, TileElfTreeCore.class);
		if (core != null) {
			if (floor == EFloorHall.instance) throw new CommandException("commands.es.build.fail");
			if (core.getFloorCount() == 0) core.scheduleFloor(EFloorHall.instance);
			if (!core.scheduleFloor(floor)) throw new CommandException("commands.es.build.fail");
			core.finishAllBuildTask();
			return;
		}
		// 模拟建造
		FloorInfo info = new FloorInfo(floor, pos.up());
		BuilderWithInfo builder = new BuilderWithInfo(entity.world, info, GenElfEdifice.EDIFICE_SIZE, 60, pos.up(60));
		info.setFloorData(info.getType().createBuildData(builder, entity.getRNG()));
		info.getType().build(builder);
		builder.buildAll();
		info.getType().surprise(builder, entity.getRNG());
		info.getType().spawn(builder);
	}
}
