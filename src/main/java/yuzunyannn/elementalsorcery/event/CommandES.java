package yuzunyannn.elementalsorcery.event;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.building.ArcInfo;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.util.WorldHelper;

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
		if (args.length < 1)
			throw new CommandException("commands.es.usage");
		if (args[0].equals("build")) {
			if (args.length == 1)
				throw new CommandException("commands.es.build.usage");
			Entity entity = sender.getCommandSenderEntity();
			this.cmdBuild(args[1], (EntityLivingBase) entity, server.getEntityWorld());
		} else if (args[0].equals("page")) {
			if (args.length == 1)
				throw new CommandException("commands.es.page.usage");
			String id_str = args[1];
			int id = 0;
			try {
				id = Integer.parseInt(id_str);
			} catch (NumberFormatException e) {
				throw new CommandException("commands.es.page.usage");
			}
			if (!Pages.isVaild(id))
				throw new CommandException("commands.es.page.fail");
			Entity entity = sender.getCommandSenderEntity();
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (id == Pages.BOOK)
					player.inventory.addItemStackToInventory(new ItemStack(ESInitInstance.ITEMS.MANUAL));
				else
					player.inventory.addItemStackToInventory(ItemParchment.getParchment(id));
			}
		} else
			throw new CommandException("commands.es.usage");
	}

	// 自动补全
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			@Nullable BlockPos targetPos) {
		if (args.length == 1) {
			String[] names = { "build", "page" };
			return CommandBase.getListOfStringsMatchingLastWord(args, names);
		} else if (args.length == 2) {
			if (args[1].equals("build")) {
				// Set<String> names = BuildingLib.instance.getBuildingsName();
				// return CommandBase.getListOfStringsMatchingLastWord(args,
				// names);
			}
		}
		return null;
	}

	private void cmdBuild(String value, EntityLivingBase entity, World world) throws CommandException {
		Building building = null;
		BlockPos pos = null;
		if (value.toLowerCase().equals("it")) {
			ItemStack stack = entity.getHeldItemMainhand();
			ArcInfo info = new ArcInfo(stack, world.isRemote ? Side.CLIENT : Side.SERVER);
			if (!info.isValid())
				throw new CommandException("commands.es.build.it.usage");
			building = info.building;
			pos = info.pos;
		} else {
			building = BuildingLib.instance.getBuilding(value);
			RayTraceResult result = WorldHelper.getLookAtBlock(world, entity, 128);
			if (result != null)
				pos = result.getBlockPos();
		}
		if (building == null || pos == null)
			throw new CommandException("commands.es.build.fail.usage");
		Building.BuildingBlocks iter = building.getBuildingBlocks();
		while (iter.next()) {
			world.setBlockState(iter.getPos().add(pos), iter.getState());
		}
	}
}
