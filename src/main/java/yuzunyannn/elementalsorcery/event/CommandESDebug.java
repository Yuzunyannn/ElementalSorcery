package yuzunyannn.elementalsorcery.event;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.building.ArcInfo;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.building.BuildingSaveData;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.elf.edifice.GenElfEdifice;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.Quests;
import yuzunyannn.elementalsorcery.elf.research.ResearchRecipeManagement;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;
import yuzunyannn.elementalsorcery.entity.EntityMagicMelting;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.item.ItemMagicRuler;
import yuzunyannn.elementalsorcery.item.ItemQuest;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class CommandESDebug {

	public static final String[] autoTips = new String[] { "reflush", "saveBuilding", "recordBuilding", "buildTest",
			"portalTest", "showInfo", "blockMoveTest", "smeltingTest", "reloadeTexture", "quest" };

	/** debug 测试内容，不进行本地化 */
	static void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		switch (args[0]) {
		// 重新加载材质
		case "reloadeTexture": {
			String path = args[1];
			if (path.indexOf(':') == -1) reloadTexture(new ResourceLocation(ElementalSorcery.MODID, path));
			else reloadTexture(new ResourceLocation(path));
			sender.sendMessage(new TextComponentString("这条是方便的测试指令，小心线程出问题呀"));
		}
			return;
		// 刷新数据
		case "reflush": {
			Side side = Side.CLIENT;
			try {
				Minecraft.getMinecraft();
			} catch (Throwable e) {
				side = Side.SERVER;
			}
			try {
				ElementMap.reflush();
				sender.sendMessage(new TextComponentString("元素映射数据刷新成功!"));
				Pages.init(side);
				sender.sendMessage(new TextComponentString("教程羊皮卷数据刷新成功!"));
				ResearchRecipeManagement.reload();
				sender.sendMessage(new TextComponentString("研究合成表刷新成功!"));
			} catch (Exception e) {
				ElementalSorcery.logger.warn("刷新数据出现异常！", e);
				sender.sendMessage(new TextComponentString("刷新数据出现异常！Refresh data exception!"));
			}
		}
			return;
		case "quest": {
			Quest quest = Quests.createExploreQuests5(null, (EntityPlayer) sender.getCommandSenderEntity());
			ItemHelper.addItemStackToPlayer((EntityPlayer) sender.getCommandSenderEntity(),
					ItemQuest.createQuest(quest));
			return;
		}
		// 保存手中的建筑
		case "saveBuilding": {
			Entity entity = sender.getCommandSenderEntity();
			ItemStack ruler = ((EntityPlayer) entity).getHeldItem(EnumHand.OFF_HAND);
			if (ItemMagicRuler.getRulerPos(ruler, true) == null || ItemMagicRuler.getRulerPos(ruler, false) == null)
				ruler = ((EntityPlayer) entity).getHeldItem(EnumHand.MAIN_HAND);
			if (ItemMagicRuler.getRulerPos(ruler, true) == null || ItemMagicRuler.getRulerPos(ruler, false) == null)
				throw new WrongUsageException("保存建筑失败，请将魔力标尺放在手上");
			Building building = Building.createBuilding(sender.getEntityWorld(), EnumFacing.NORTH,
					ItemMagicRuler.getRulerPos(ruler, true), ItemMagicRuler.getRulerPos(ruler, false));
			building.setAuthor(sender.getName());
			BuildingSaveData.debugSetKeyName(building);
			File file = ElementalSorcery.data.getFile("building/debug",
					BuildingSaveData.randomKeyName(entity.getName()));
			try (OutputStream output = new FileOutputStream(file)) {
				CompressedStreamTools.writeCompressed(building.serializeNBT(), output);
				sender.sendMessage(new TextComponentString("建筑储存在了:" + file.getPath()));
			} catch (IOException e) {
				ElementalSorcery.logger.warn("debug指令错误", e);
			}
		}
			return;
		// 记录建筑数据
		case "recordBuilding": {
			EntityLivingBase entity = (EntityLivingBase) sender.getCommandSenderEntity();
			ItemStack ruler = entity.getHeldItem(EnumHand.OFF_HAND);
			ItemStack ar = entity.getHeldItem(EnumHand.MAIN_HAND);
			Building building = Building.createBuilding(sender.getEntityWorld(), EnumFacing.NORTH,
					ItemMagicRuler.getRulerPos(ruler, true), ItemMagicRuler.getRulerPos(ruler, false));
			building.setAuthor(sender.getName());
			BuildingLib.instance.addBuilding(building);
			ArcInfo.initArcInfoToItem(ar, building.getKeyName());
			sender.sendMessage(new TextComponentString("记录完成！"));
		}
			return;
		default:
			EntityLivingBase entity = (EntityLivingBase) sender.getCommandSenderEntity();
			RayTraceResult rtr = WorldHelper.getLookAtBlock(entity.world, entity, 64);
			BlockPos pos = rtr != null ? rtr.getBlockPos() : null;
			switch (args[0]) {
			// 测试建筑
			case "buildTest": {
				GenElfEdifice g = new GenElfEdifice(true);
				g.genMainTreeEdifice(entity.world, pos, entity.world.rand);
				g.buildToTick(entity.world);
				// new WorldGenElfTree(true, 3).generate(entity.world, entity.world.rand, pos);
				/*
				 * VillageCreationHandler h = new VillageCreationHandler();
				 * StructureVillagePieces.Village v = h.buildComponent(null, null, new
				 * LinkedList<StructureComponent>(), new Random(), pos.getX(), pos.getY(),
				 * pos.getZ(), EnumFacing.NORTH, 0); try { Field field =
				 * StructureVillagePieces.Village.class.getDeclaredField("averageGroundLvl") ;
				 * field.setAccessible(true); field.setInt(v, pos.getY()); } catch (Exception e)
				 * { ElementalSorcery.logger.warn("debug指令错误", e); }
				 * v.addComponentParts(sender.getEntityWorld(), new Random(),
				 * v.getBoundingBox());
				 */
			}
				return;
			case "smeltingTest": {
				EntityMagicMelting e = new EntityMagicMelting(entity.world, pos.up(),
						new ItemStack(Blocks.DIAMOND_ORE));
				entity.world.spawnEntity(e);
			}
				return;
			case "blockMoveTest": {
				EntityBlockMove blockMove = new EntityBlockMove(entity.world, pos,
						entity.getPositionVector().addVector(0, 1, 0));
				blockMove.setColor(0xff0000);
				blockMove.setFlag(EntityBlockMove.FLAG_FORCE_DROP, true);
				entity.world.setBlockToAir(pos);
				entity.world.spawnEntity(blockMove);
			}
				return;
			// 测试传送门
			case "portalTest": {
				pos = pos.up();
				World world = server.getWorld(0);
				EntityPortal.createPortal(sender.getEntityWorld(), pos, world, new BlockPos(754, 65, 766));
			}
				return;
			// 展示一些信息
			case "showInfo": {
				World world = server.getEntityWorld();
				ItemStack stack = ItemStack.EMPTY;
				Item item = Items.AIR;
				Block block = Blocks.AIR;
				IBlockState state = block.getDefaultState();
				if (pos == null) {
					stack = entity.getHeldItem(EnumHand.MAIN_HAND);
					item = stack.getItem();
					block = Block.getBlockFromItem(item);
				} else {
					state = entity.world.getBlockState(pos);
					block = state.getBlock();
					item = Item.getItemFromBlock(block);
					stack = new ItemStack(block, 1, block.getMetaFromState(state));
				}
				Function<String, Void> show = (str) -> {
					sender.sendMessage(new TextComponentString(TextFormatting.AQUA + str));
					return null;
				};
				show.apply("------------------以下是debug信息------------------");
				show.apply(stack.getItem().getRegistryName().toString());
				if (block != Blocks.AIR) {
					show.apply("hardness:" + block.getBlockHardness(state, world, pos));
					show.apply("blockstate:" + state);
				} else {

				}
			}
				return;
			default:
				break;
			}
			break;
		}
		throw new CommandException("ES dubug 指令无效，随便使用debug指令可能会导致崩溃");
	}

	@SideOnly(Side.CLIENT)
	static public void reloadTexture(ResourceLocation path) {
		EventClient.addTickTask(() -> {
			Minecraft mc = Minecraft.getMinecraft();
			TextureManager tm = mc.getTextureManager();
			tm.deleteTexture(path);
			tm.bindTexture(path);
			return ITickTask.END;
		});
	}

}
