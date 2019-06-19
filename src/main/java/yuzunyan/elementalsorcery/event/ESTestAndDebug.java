package yuzunyan.elementalsorcery.event;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ESTestAndDebug {

	public ESTestAndDebug() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	static BlockPos pos = new BlockPos(0, 0, 0);

	@SubscribeEvent
	public void click(PlayerInteractEvent event) {
//		if (event.getEntityPlayer().isSneaking()) {
//			pos = event.getPos();
//		} else {
//			BlockPos newpos = event.getPos().subtract(pos);
//			System.out.println(newpos);
//			// if (event.getWorld().isRemote) {
//			// EntityBat bat = new EntityBat(event.getWorld());
//			// bat.setPosition(event.getEntityPlayer().posX + 0.5,
//			// event.getEntityPlayer().posY +0.5,
//			// event.getEntityPlayer().posZ + 0.5);
//			// PathFinder finder = new PathFinder(new FlyingNodeProcessor());
//			// Path path = finder.findPath(event.getWorld(), bat, pos, 32);
//			// if (path != null) {
//			// for (int i = 0; i < path.getCurrentPathLength(); i++) {
//			// PathPoint p = path.getPathPointFromIndex(i);
//			// event.getWorld().setBlockState(new BlockPos(p.x, p.y, p.z),
//			// Blocks.FIRE.getDefaultState());
//			// }
//			// }
//			// }
//		}

		if (event.getWorld().isRemote)
			return;

		// IBlockState state = event.getWorld().getBlockState(event.getPos());
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
				try {
					float n = Float.parseFloat(args[1]);
					sender.sendMessage(new TextComponentString(Float.toString(n)));
					switch (args[0].charAt(0)) {
					case 's':
						// ItemSpellbook.s = n;
						break;
					}
				} catch (Exception ex) {

				}
			} else
				throw new WrongUsageException("No!!!!");
		}
	}
}
