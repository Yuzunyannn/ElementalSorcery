package yuzunyannn.elementalsorcery.logics;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IElementInventoryModifiable;
import yuzunyannn.elementalsorcery.building.ArcInfo;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingBlocks;
import yuzunyannn.elementalsorcery.building.BuildingFace;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.dungeon.DungeonArea;
import yuzunyannn.elementalsorcery.dungeon.DungeonWorld;
import yuzunyannn.elementalsorcery.elf.edifice.BuilderWithInfo;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorHall;
import yuzunyannn.elementalsorcery.elf.edifice.ElfEdificeFloor;
import yuzunyannn.elementalsorcery.elf.edifice.FloorInfo;
import yuzunyannn.elementalsorcery.elf.edifice.GenElfEdifice;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.Quests;
import yuzunyannn.elementalsorcery.elf.research.AncientPaper;
import yuzunyannn.elementalsorcery.elf.research.Researcher;
import yuzunyannn.elementalsorcery.elf.research.Topics;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper.EnumType;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.item.ItemQuest;
import yuzunyannn.elementalsorcery.item.prop.ItemMantraGem;
import yuzunyannn.elementalsorcery.item.tool.ItemMagicRuler;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.ts.PocketWatch;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.GameHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
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
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		if (sender instanceof CommandBlockBaseLogic) return true;
		return super.checkPermission(server, sender);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1) throw new CommandException("commands.es.usage");
		switch (args[0]) {
		case "dungeon":// 地牢
		{
			if (args.length < 2) throw new WrongUsageException("commands.es.dungeon.usage");
			this.cmdDungeon(Arrays.copyOfRange(args, 1, args.length), server, sender);
			return;
		}
		case "build":// 建筑
		{
			if (args.length == 1) throw new WrongUsageException("commands.es.build.usage");
			checkUnsupportCommandBlock(sender);// 不支持命令方块
			Entity entity = sender.getCommandSenderEntity();
			this.cmdBuild(Arrays.copyOfRange(args, 1, args.length), (EntityLivingBase) entity, server.getEntityWorld());
			return;
		}
		case "buildFloor": {
			if (args.length == 1) throw new WrongUsageException("commands.es.buildFloor.usage");
			checkUnsupportCommandBlock(sender);// 不支持命令方块
			Entity entity = sender.getCommandSenderEntity();
			this.cmdBuildFloor(args[1], (EntityLivingBase) entity, server.getEntityWorld());
			return;
		}
		case "building":// 建筑操作
		{
			if (args.length < 2) throw new WrongUsageException("commands.es.building.usage");
			this.cmdBuilding(Arrays.copyOfRange(args, 1, args.length), server, sender);
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
					player.inventory.addItemStackToInventory(new ItemStack(ESObjects.ITEMS.MANUAL));
				else player.inventory.addItemStackToInventory(ItemParchment.getParchment(idStr));
			}
			return;
		}
		case "quest": {
			if (args.length < 2) throw new CommandException("commands.es.quest.usage");
			this.cmdQuest(Arrays.copyOfRange(args, 1, args.length), server, sender);
			return;
		}
		case "mantra": {
			if (args.length < 4) throw new CommandException("commands.es.mantra.usage");
			this.cmdMantra(Arrays.copyOfRange(args, 1, args.length), server, sender);
			return;
		}
		case "research": {
			if (args.length < 4) throw new CommandException("commands.es.research.usage");
			this.cmdResearch(Arrays.copyOfRange(args, 1, args.length), server, sender);
			return;
		}
		case "element": {
			if (args.length < 4) throw new CommandException("commands.es.element.usage");
			this.cmdElement(Arrays.copyOfRange(args, 1, args.length), server, sender);
			return;
		}
		case "edifice": {
			if (args.length < 2) throw new CommandException("commands.es.edifice.usage");
			this.cmdEdifice(Arrays.copyOfRange(args, 1, args.length), server, sender);
			return;
		}
		case "test": {
			if (args.length < 2)
				throw new CommandException("Don't use Test Command before you konw what you will to do");
			this.cmdTest(Arrays.copyOfRange(args, 1, args.length), server, sender);
			return;
		}
		case "debug":
		// debug
		{
			if (!ESAPI.isDevelop) throw new CommandException("Dubug Command only can be used in Develop!");
			if (args.length == 1) throw new CommandException("Dubug command error");
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
			String[] names = { "build", "page", "debug", "buildFloor", "quest", "mantra", "element", "building",
					"edifice", "research", "dungeon", "test" };
			return CommandBase.getListOfStringsMatchingLastWord(args, names);
		} else if (args.length >= 2) {
			switch (args[0]) {
			case "dungeon": {
				return getListOfStringsMatchingLastWord(args, "new", "list");
			}
			case "build": {
				List<String> arrayList = Buildings.getKeys();
				arrayList.add("it");
				List<String> tips = getListOfStringsMatchingLastWord(args, arrayList);
				return tips;
			}
			case "buildFloor": {
				Collection<ResourceLocation> bs = ElfEdificeFloor.REGISTRY.getKeys();
				return getListOfStringsMatchingLastWord(args, bs);
			}
			case "building": {
				if (args.length > 2) {
					if ("give".equals(args[1])) {
						if (args.length > 3)
							return CommandBase.getListOfStringsMatchingLastWord(args, Buildings.getKeys());
						return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
					}
					return CommandBase.getListOfStringsMatchingLastWord(args);
				}
				return getListOfStringsMatchingLastWord(args, "record", "release", "give");
			}
			case "page": {
				Set<String> set = Pages.getPageIds();
				String[] names = new String[set.size()];
				set.toArray(names);
				return getListOfStringsMatchingLastWord(args, names);
			}
			case "quest": {
				if (args.length > 3) return getListOfStringsMatchingLastWord(args, Quests.CREATOR.keySet());
				if (args.length > 2) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
				return getListOfStringsMatchingLastWord(args, "clear", "give", "fame", "debt");
			}
			case "mantra": {
				if (args.length > 3) return getListOfStringsMatchingLastWord(args, Mantra.REGISTRY.getKeys());
				if (args.length > 2) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
				return getListOfStringsMatchingLastWord(args, "give", "remove", "add");
			}
			case "element": {
				if (args.length > 4) return CommandBase.getListOfStringsMatchingLastWord(args);
				if (args.length > 3) return getListOfStringsMatchingLastWord(args, Element.REGISTRY.getKeys());
				if (args.length > 2) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
				return getListOfStringsMatchingLastWord(args, "give", "extract", "insert", "add");
			}
			case "research": {
				if (args.length > 3) return getListOfStringsMatchingLastWord(args, Topics.getDefaultTopics());
				if (args.length > 2) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
				return getListOfStringsMatchingLastWord(args, "add", "sub");
			}
			case "edifice": {
				return getListOfStringsMatchingLastWord(args, "store", "restore", "build", "fix");
			}
			case "test": {
				if (args.length == 2) return getListOfStringsMatchingLastWord(args, "timeStop");
			}
			case "debug": {
				return getListOfStringsMatchingLastWord(args, CommandESDebug.autoTips);
			}
			default:
			}
		}
		return CommandBase.getListOfStringsMatchingLastWord(args);
	}

	/** 检测过滤不支持命令方块的指令 */
	private void checkUnsupportCommandBlock(ICommandSender sender) throws CommandException {
		Entity player = sender.getCommandSenderEntity();
		if (player instanceof EntityPlayer) return;
		throw new WrongUsageException("commands.es.not.support.commandBlock");
	}

	// =======================-------> 咒文 <-------=======================
	private void cmdMantra(String[] args, MinecraftServer server, ICommandSender sender) throws CommandException {
		EntityPlayer player = getPlayer(server, sender, args[1]);
		ResourceLocation id = TextHelper.toESResourceLocation(args[2]);
		Mantra mantra = Mantra.REGISTRY.getValue(id);
		if (mantra == null) throw new CommandException("commands.es.notFound", id.toString());

		ItemStack grimoireStack = player.getHeldItem(EnumHand.MAIN_HAND);
		Grimoire grimoire = grimoireStack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		switch (args[0]) {
		case "give":
			AncientPaper ap = new AncientPaper();
			grimoireStack = new ItemStack(ESObjects.ITEMS.ANCIENT_PAPER, 1, EnumType.NEW_WRITTEN.getMetadata());
			ap.setMantra(mantra).setStart(0).setEnd(100);
			ap.saveState(grimoireStack);
			ItemHelper.addItemStackToPlayer(player, grimoireStack);
			notifyCommandListener(sender, this, "commands.es.mantra.give", player.getName(), mantra.getTextComponent());
			return;
		case "remove":
			if (grimoire == null)
				throw new CommandException("commands.es.notFound", new TextComponentTranslation("item.grimoire.name"));
			grimoire.loadState(grimoireStack);
			grimoire.remove(mantra);
			grimoire.saveState(grimoireStack);
			notifyCommandListener(sender, this, "commands.es.mantra.remove", player.getName(),
					mantra.getTextComponent());
			return;
		case "add":
			if (ItemMantraGem.isMantraGem(grimoireStack)) ItemMantraGem.setMantraToMantraGem(grimoireStack, mantra);
			else {
				if (grimoire == null) throw new CommandException("commands.es.notFound",
						new TextComponentTranslation("item.grimoire.name"));
				grimoire.loadState(grimoireStack);
				grimoire.add(mantra);
				grimoire.saveState(grimoireStack);
			}
			notifyCommandListener(sender, this, "commands.es.mantra.add", player.getName(), mantra.getTextComponent());
			return;
		default:
			throw new WrongUsageException("commands.es.mantra.usage");
		}

	}

	private IElementInventory getElementInventory(ItemStack stack) {
		IElementInventory inv = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		if (inv == null) inv = new ElementInventory();
		if (inv.hasState(stack)) inv.loadState(stack);
		return inv;
	}

	// =======================-------> 元素 <-------=======================
	private void cmdElement(String[] args, MinecraftServer server, ICommandSender sender) throws CommandException {
		EntityPlayer player = getPlayer(server, sender, args[1]);
		ResourceLocation id = TextHelper.toESResourceLocation(args[2]);
		Element element = Element.REGISTRY.getValue(id);
		if (element == null) throw new CommandException("commands.es.notFound", id.toString());

		int count = 1;
		int power = 100;
		if (args.length > 3) count = Integer.parseInt(args[3]);
		if (args.length > 4) power = Integer.parseInt(args[4]);

		ElementStack estack = new ElementStack(element, count, power);
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

		switch (args[0]) {
		case "give": {
			ItemStack elementCrystal = new ItemStack(ESObjects.ITEMS.ELEMENT_CRYSTAL);
			IElementInventory inv = ElementHelper.getElementInventory(elementCrystal);
			inv.insertElement(estack, false);
			inv.saveState(elementCrystal);
			ItemHelper.addItemStackToPlayer(player, elementCrystal);
			notifyCommandListener(sender, this, "commands.es.element.give", player.getName(),
					estack.getTextComponent());
		}
			return;
		case "insert": {
			if (stack.isEmpty()) throw new CommandException("commands.es.element.notFound");
			IElementInventory inv = this.getElementInventory(stack);
			inv.insertElement(estack, false);
			inv.saveState(stack);
			return;
		}

		case "extract": {
			if (stack.isEmpty()) throw new CommandException("commands.es.element.notFound");
			IElementInventory inv = this.getElementInventory(stack);
			inv.extractElement(estack, false);
			inv.saveState(stack);
			return;
		}

		case "add": {
			if (stack.isEmpty()) throw new CommandException("commands.es.element.notFound");
			IElementInventory inv = this.getElementInventory(stack);
			if (!inv.insertElement(estack, false)) {
				if (!(inv instanceof IElementInventoryModifiable))
					throw new CommandException("commands.es.element.notModifiable");
				ElementStack[] cache = new ElementStack[inv.getSlots()];
				for (int i = 0; i < cache.length; i++) cache[i] = inv.getStackInSlot(i);
				IElementInventoryModifiable modifiable = ((IElementInventoryModifiable) inv);
				modifiable.setSlots(cache.length + 1);
				for (int i = 0; i < cache.length; i++) modifiable.setStackInSlot(i, cache[i]);
				modifiable.setStackInSlot(cache.length, estack);
			}
			inv.saveState(stack);
			return;
		}
		default:
			throw new WrongUsageException("commands.es.element.usage");
		}

	}

	// =======================-------> 研究 <-------=======================
	private void cmdResearch(String[] args, MinecraftServer server, ICommandSender sender) throws CommandException {
		EntityPlayer player = getPlayer(server, sender, args[1]);
		String topic = args[2];
		int count = 1;
		if (args.length > 3) count = Integer.parseInt(args[3]);

		switch (args[0]) {
		case "add":
			break;
		case "sub":
			count = -count;
			break;
		default:
			throw new WrongUsageException("commands.es.research.usage");
		}

		Researcher researcher = new Researcher(player);
		if (count < 0) count = -Math.min(-count, researcher.getPoint(topic));
		researcher.grow(topic, count);
		researcher.save(player);
		notifyCommandListener(sender, this, "commands.es.research.add", player.getName(), Integer.toString(count),
				topic);

	}

	// =======================-------> 任务 <-------=======================
	private void cmdQuest(String[] args, MinecraftServer server, ICommandSender sender) throws CommandException {

		switch (args[0]) {
		case "clear": {
			EntityPlayer player = getPlayer(server, sender, args[1]);
			IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
			if (adventurer != null) adventurer.removeAllQuest();
			notifyCommandListener(sender, this, "commands.es.quest.clear", player.getName());
			return;
		}
		case "give": {
			if (args.length < 3) throw new WrongUsageException("commands.es.quest.give.usage");
			EntityPlayer player = getPlayer(server, sender, args[1]);
			ResourceLocation id = TextHelper.toESResourceLocation(args[2]);
			Quest quest = Quests.createQuest(id, player);
			if (quest == null) throw new CommandException("commands.es.notFound", id.toString());
			ItemHelper.addItemStackToPlayer(player, ItemQuest.createQuest(quest));
			return;
		}
		case "fame": {
			if (args.length < 3) throw new WrongUsageException("commands.es.quest.fame.usage");
			EntityPlayer player = getPlayer(server, sender, args[1]);
			float d = 0;
			try {
				d = Float.parseFloat(args[2]);
			} catch (NumberFormatException e) {
				throw new WrongUsageException("commands.es.quest.fame.usage");
			}
			IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
			if (adventurer == null) throw new CommandException("commands.es.notFound", "adventurer");
			adventurer.fame(d);
			notifyCommandListener(sender, this, "commands.es.quest.fame.change", player.getName(),
					adventurer.getFame());
			return;
		}
		case "debt": {
			if (args.length < 3) throw new WrongUsageException("commands.es.quest.debt.usage");
			EntityPlayer player = getPlayer(server, sender, args[1]);
			int d = 0;
			try {
				d = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				throw new WrongUsageException("commands.es.quest.debt.usage");
			}
			IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
			if (adventurer == null) throw new CommandException("commands.es.notFound", "adventurer");
			adventurer.incurDebts(d);
			notifyCommandListener(sender, this, "commands.es.quest.debt.change", player.getName(),
					adventurer.getDebts());
			return;
		}
		default:
			throw new WrongUsageException("commands.es.quest.usage");
		}
	}

	// =======================-------> 建造建筑 <-------=======================
	private void cmdBuild(String[] args, EntityLivingBase entity, World world) throws CommandException {
		String value = args[0];
		boolean genRuler = false;
		boolean clear = false;
		if (args.length > 1) {
			for (int i = 1; i < args.length; i++) {
				String cmd = args[i];
				if (cmd.indexOf("-r") != -1) genRuler = true;
				if (cmd.indexOf("-c") != -1) clear = true;
			}
		}
		Building building = null;
		BlockPos pos = null;
		if (value.toLowerCase().equals("it")) {
			ItemStack stack = entity.getHeldItemMainhand();
			ArcInfo info = new ArcInfo(stack, world.isRemote ? Side.CLIENT : Side.SERVER);
			if (!info.isValid()) throw new WrongUsageException("commands.es.build.it.usage");
			building = info.building;
			pos = info.pos;
		} else {
			building = BuildingLib.instance.getBuilding(value);
			RayTraceResult result = WorldHelper.getLookAtBlock(world, entity, 128);
			if (result != null) pos = result.getBlockPos().up();
		}
		if (building == null || pos == null) throw new CommandException("commands.es.build.fail");

		EnumFacing facing = entity.getHorizontalFacing().getOpposite();

		AxisAlignedBB aabb = building.getBox();
		aabb = BuildingFace.face(aabb, facing);
		BlockPos pos1 = new BlockPos(aabb.minX, aabb.minY, aabb.minZ).add(pos);
		BlockPos pos2 = new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ).add(pos);

		if (clear) {
			for (BlockPos at : BlockPos.getAllInBox(pos1, pos2)) world.setBlockToAir(at);
		}

		if (genRuler) {
			ItemStack stack = new ItemStack(ESObjects.ITEMS.MAGIC_RULER);
			ItemMagicRuler.setDimensionId(stack, world.provider.getDimension());
			ItemMagicRuler.setRulerPos(stack, pos1, false);
			ItemMagicRuler.setRulerPos(stack, pos2, true);
			NBTTagCompound nbttagcompound = stack.getOrCreateSubCompound("display");
			nbttagcompound.setString("Name", building.getName());
			stack.getTagCompound().setString("building_key", building.getKeyName());
			if (entity instanceof EntityPlayer) ItemHelper.addItemStackToPlayer((EntityPlayer) entity, stack);
			else ItemHelper.dropItem(world, pos, stack);
		}

		BuildingBlocks iter = building.getBuildingIterator();
		iter.setFace(facing);
		while (iter.next()) {
			BlockPos at = iter.getPos().add(pos);
			iter.buildState(world, at);
		}

	}

	// =======================-------> 楼层操作 <-------=======================
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

	// =======================-------> 建造操作 <-------=======================
	private void cmdBuilding(String[] args, MinecraftServer server, ICommandSender sender) throws CommandException {
		checkUnsupportCommandBlock(sender); // 不支持命令方块

		EntityPlayer executer = (EntityPlayer) sender.getCommandSenderEntity();
		EntityPlayer player = executer;

		switch (args[0]) {
		case "release":
			BuildingLib.instance.releaseAllSaveData();
			notifyCommandListener(sender, this, "commands.es.building.release");
			System.gc();
			break;
		case "recordNBT":
		case "test":
		case "record": {
			ItemStack ruler = executer.getHeldItem(EnumHand.MAIN_HAND);
			BlockPos pos1 = ItemMagicRuler.getRulerPos(ruler, true);
			BlockPos pos2 = ItemMagicRuler.getRulerPos(ruler, false);
			if (pos1 == null || pos2 == null) throw new WrongUsageException("commands.es.building.recordFail");
			Building building = Building.createBuilding(sender.getEntityWorld(),
					executer.getHorizontalFacing().getOpposite(), pos1, pos2, true);
			building.setAuthor(executer.getName());
			building.setName(executer.getName() + "'s building!");
			if ("test".equals(args[0])) {
				if (ESAPI.isDevelop) CommandESDebug.printBuildingPos(building);
				break;
			}
			if ("recordNBT".equals(args[0])) BuildingLib.instance.addBuilding(building, true);
			else BuildingLib.instance.addBuilding(building, false);
			ItemHelper.addItemStackToPlayer(player, ArcInfo.createArcInfoItem(building.getKeyName()));
			notifyCommandListener(sender, this, "commands.es.building.record", player.getName());
			break;
		}
		case "give": {
			if (args.length < 3) throw new WrongUsageException("commands.es.building.give.usage");
			player = getPlayer(server, sender, args[1]);
			String key = args[2];
			ItemHelper.addItemStackToPlayer(player, ArcInfo.createArcInfoItem(key));
			notifyCommandListener(sender, this, "commands.es.building.give", player.getName());
			break;
		}
		default:
			throw new WrongUsageException("commands.es.building.usage");
		}
	}

	// =======================-------> 地牢 <-------=======================
	private void cmdDungeon(String[] args, MinecraftServer server, ICommandSender sender) throws CommandException {
		String key = args[0];
		World world = sender.getEntityWorld();
		Entity entity = sender.getCommandSenderEntity();

		switch (key) {
		case "new": {
			BlockPos pos = sender.getPosition();
			if (entity != null) {
				RayTraceResult result = WorldHelper.getLookAtBlock(world, entity, 128);
				if (result != null) pos = result.getBlockPos().up();
			}
			DungeonWorld dw = DungeonWorld.getDungeonWorld(world);
			DungeonArea area = dw.newDungeon(pos);
			if (area.isFail()) throw new CommandException(area.getFailMsg());
			else {
				EntityPlayer player = null;
				if (entity instanceof EntityPlayer) player = (EntityPlayer) entity;
				area.startBuildRoom(world, 0, player);
				notifyCommandListener(sender, this, "Dungeon Id : " + area.getExcerpt().getId());
			}
			break;
		}
		case "list": {
			DungeonWorld dw = DungeonWorld.getDungeonWorld(world);
			notifyCommandListener(sender, this, "....");
			break;
		}
		default:
			throw new WrongUsageException("commands.es.dungeon.usage");
		}
	}

	// =======================-------> 精灵大厦 <-------=======================
	private void cmdEdifice(String[] args, MinecraftServer server, ICommandSender sender) throws CommandException {
		Entity executer = sender.getCommandSenderEntity();
		BlockPos ePos = sender.getPosition();
		World world = sender.getEntityWorld();

		switch (args[0]) {
		case "fix": {
			TileElfTreeCore core = findElfTreeCore(world, ePos);
			core.restore(core.store());
			notifyCommandListener(sender, this, "commands.es.edifice.restore.success");
			return;
		}
		case "store": {
			TileElfTreeCore core = findElfTreeCore(world, ePos);
			String data = core.store();
			ESAPI.logger.info("Edifice Store Data:" + data);
			GameHelper.clientRun(() -> {
				Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
				clip.setContents(new StringSelection(data), null);
			});
			notifyCommandListener(sender, this, "commands.es.edifice.store.success", data);
			return;
		}
		case "restore": {
			if (args.length < 2) throw new WrongUsageException("commands.es.edifice.restore.usage");
			String data = args[1];
			TileElfTreeCore core = findElfTreeCore(world, ePos);
			core.restore(data);
			notifyCommandListener(sender, this, "commands.es.edifice.restore.success");
			return;
		}
		case "build": {
			BlockPos pos = null;
			if (executer instanceof EntityLivingBase) {
				RayTraceResult result = WorldHelper.getLookAtBlock(world, (EntityLivingBase) executer, 128);
				if (result != null) pos = result.getBlockPos();
			} else pos = ePos;
			if (pos == null) throw new CommandException("commands.es.build.fail");

			GenElfEdifice g = new GenElfEdifice(true);
			g.genMainTreeEdifice(world, pos.down(), world.rand);
			g.clearAround(world, pos);
			g.buildToTick(world);
			if (args.length >= 2) g.setRestoreData(args[1]);
			return;
		}
		default:
			throw new WrongUsageException("commands.es.edifice.usage");
		}
	}

	private TileElfTreeCore findElfTreeCore(World world, BlockPos pos) throws WrongUsageException {
		BlockPos corePos = TileElfTreeCore.findTreeCoreFrom(world, pos);
		if (corePos == null) throw new WrongUsageException("commands.es.edifice.no.core");
		TileElfTreeCore core = BlockHelper.getTileEntity(world, corePos, TileElfTreeCore.class);
		if (core == null) throw new WrongUsageException("commands.es.edifice.no.core");
		return core;
	}

	private void cmdTest(String[] args, MinecraftServer server, ICommandSender sender) throws CommandException {
		String id = args[0];
		Entity executer = sender.getCommandSenderEntity();

		switch (id) {
		case "timeStop": {
			int tick = 20 * 8;
			EntityLivingBase living = null;
			try {
				tick = Integer.parseInt(args[1]);
				living = (EntityLivingBase) executer;
			} catch (Exception e) {}
			PocketWatch.stopWorld(executer.world, tick, living);
		}
			break;
		default:
			break;
		}
	}
}
