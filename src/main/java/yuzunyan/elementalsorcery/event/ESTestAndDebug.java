package yuzunyan.elementalsorcery.event;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuzunyan.elementalsorcery.building.ArcInfo;
import yuzunyan.elementalsorcery.building.Building;
import yuzunyan.elementalsorcery.building.BuildingLib;
import yuzunyan.elementalsorcery.item.ItemMagicRuler;

public class ESTestAndDebug {

	public ESTestAndDebug() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	static BlockPos pos = new BlockPos(0, 0, 0);

	@SubscribeEvent
	public void click(PlayerInteractEvent event) {
		// ItemStack stack =
		// event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
		// System.out.println(stack);
		// if (event.getEntityPlayer().isSneaking()) {
		// pos = event.getPos();
		// } else {
		// BlockPos newpos = event.getPos().subtract(pos);
		// System.out.println(newpos);
		// }

		if (event.getWorld().isRemote)
			return;

		// IBlockState state = event.getWorld().getBlockState(event.getPos());
		// System.out.println(state.getBlock().getClass());
		// if (state.getBlock() instanceof BlockFlower) {
		// System.out.println(state);
		// }
		// if (state.getBlock() instanceof BlockStairs) {
		// state = state.getBlock().getActualState(state, event.getWorld(),
		// event.getPos());
		// System.out.println("Face:" + state.getValue(BlockStairs.FACING));
		// System.out.println("Half:" + state.getValue(BlockStairs.HALF));
		// System.out.println("Shape:" + state.getValue(BlockStairs.SHAPE));
		// }

		// try {
		// System.out.println(Blocks.QUARTZ_STAIRS.getClass().getName());
		// Field field =
		// Blocks.QUARTZ_STAIRS.getClass().getDeclaredField("modelState");
		// field.setAccessible(true);
		// IBlockState s = (IBlockState) field.get(Blocks.QUARTZ_STAIRS);
		// System.out.println(s);
		// } catch (NoSuchFieldException e) {
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// e.printStackTrace();
		// }

		//
		// IBlockState state = event.getWorld().getBlockState(event.getPos());
		// float hard = state.getBlock().getBlockHardness(state,
		// event.getWorld(), event.getPos());
		// String str = state.getBlock().getRegistryName() + "'s hardness:" +
		// hard;
		// System.out.println(str);

		// event.getEntityPlayer().sendMessage(new TextComponentString(str));

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

			} else if (args.length == 2) {
				Entity entity = sender.getCommandSenderEntity();
				if (entity instanceof EntityPlayer) {
					ItemStack ruler = ((EntityPlayer) entity).getHeldItem(EnumHand.OFF_HAND);
					ItemStack ar = ((EntityPlayer) entity).getHeldItem(EnumHand.MAIN_HAND);
					Building building = Building.createBuilding(sender.getEntityWorld(),
							ItemMagicRuler.getRulerPos(ruler, true), ItemMagicRuler.getRulerPos(ruler, false));
					building.setAuthor(sender.getName());
					BuildingLib.instance.addBuilding(building);
					ArcInfo.initArcInfoToItem(ar, building.getKeyName());
					System.out.println("Yes!");

				}
			} else
				throw new WrongUsageException("No!!!!");
		}
	}
}
