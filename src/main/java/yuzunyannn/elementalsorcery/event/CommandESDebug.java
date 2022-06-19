package yuzunyannn.elementalsorcery.event;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingBlocks;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.elf.edifice.GenElfEdifice;
import yuzunyannn.elementalsorcery.elf.quest.Quests;
import yuzunyannn.elementalsorcery.elf.research.ResearchRecipeManagement;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;
import yuzunyannn.elementalsorcery.entity.EntityItemGoods;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.render.Shaders;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class CommandESDebug {

	public static final String[] autoTips = new String[] { "reflush", "buildTest", "portalTest", "showInfo",
			"blockMoveTest", "textTest", "reloadeTexture", "quest", "statistics", "statisticsHandle", "reloadShader" };

	/** debug 测试内容，不进行本地化 */
	static void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		switch (args[0]) {
		case "reloadShader": {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				try {
					Shaders.ErrorCode.reload();
				} catch (Exception e) {
					ElementalSorcery.logger.warn(e);
				}
			});
			return;
		}
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
				Quests.loadAll();
				sender.sendMessage(new TextComponentString("任务刷新成功!"));
			} catch (Exception e) {
				ElementalSorcery.logger.warn("刷新数据出现异常！", e);
				sender.sendMessage(new TextComponentString("刷新数据出现异常！Refresh data exception!"));
			}
		}
			return;
		case "quest": {

			return;
		}
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
			case "textTest": {

				for (int i = 0; i < 32; i++) {
					EntityItemGoods goods = new EntityItemGoods(entity.world,
							new Vec3d(pos.up(5)).add(RandomHelper.rand.nextDouble() * 5, RandomHelper.rand.nextDouble(),
									RandomHelper.rand.nextDouble() * 5),
							new ItemStack(Item.REGISTRY.getRandomObject(RandomHelper.rand), 2));
					goods.setPrice(RandomHelper.rand.nextInt(1000));
					entity.world.spawnEntity(goods);
				}

//				IFragmentMantraLauncher is = ESInit.MANTRAS.BLOCK_CRASH.getFragmentMantraLaunchers().get(0);
//				VariableSet content = new VariableSet();
//				content.set(FMantraBase.CHARGE, 99999.0);
//				is.cast(entity.world, pos, new WorldLocation(entity.world.provider.getDimension(), pos), content);

//				World world = Minecraft.getMinecraft().world;
//				Vec3d center = new Vec3d(pos).add(0.5, 4.5, 0.5);
//				Minecraft.getMinecraft().addScheduledTask(() -> {
//					for (int i = 0; i < 128; i++) {
//						float theta = Effect.rand.nextFloat() * 3.1415926f * 2;
//						double x = MathHelper.sin(theta) * (6 + Effect.rand.nextGaussian());
//						double z = MathHelper.cos(theta) * (6 + Effect.rand.nextGaussian());
//						Vec3d at = center.add(x, 0, z);
//						EffectFragmentMove f = new EffectFragmentMove(world, at);
//						f.motionY = -Effect.rand.nextFloat() * 1.5;
//						f.yAccelerate = Effect.rand.nextFloat() * 0.01;
//						f.yDecay = 0.6;
//						Effect.addEffect(f);
//					}
//				});

//				EffectBlockLine effect = new EffectBlockLine(Minecraft.getMinecraft().world,
//						new Vec3d(pos).add(0.5, 1.5, 0.5));
//				effect.color.setColor(0xffffff);
//				effect.toFacing = EnumFacing.SOUTH;
//				effect.flip = true;
//				effect.width = 0.5;
//				effect.distance = 2;
//				effect.motion = 0.1;
//				effect.maxLength = 0.5;
//				Effect.addEffect(effect);

//				EffectSphericalBlast effect = new EffectSphericalBlast(Minecraft.getMinecraft().world,
//						new Vec3d(pos).add(0.5, 1.5, 0.5), 5);
//				effect.lifeTime = (int) (80 * 0.6f);
//				effect.color.setColor(1, 0.5, 1);
//				Effect.addEffect(effect);
//
//				MantraElementWhirl.booom(entity.world, new Vec3d(pos).add(0.5, 1.5, 0.5),
//						ElementStack.magic(1000, 1000), entity);

//				MantraFireBall.fire(entity.world, entity, 32, true);

//				EffectBlockConfusion effect = new EffectBlockConfusion(Minecraft.getMinecraft().world,
//						new Vec3d(pos).add(0.5, 0.5, 0.5), entity.world.getBlockState(pos));
//				effect.setChangeTo(Blocks.DIAMOND_BLOCK.getDefaultState(), true);
//				Effect.addEffect(effect);

//				EffectBlockDisintegrate effect = new EffectBlockDisintegrate(Minecraft.getMinecraft().world,
//						new Vec3d(pos).add(0.5, 1.5, 0.5), entity.world.getBlockState(pos));
//				Effect.addEffect(effect);
//				final BlockPos at = pos;
//				EventServer.addTask(() -> {
//					EffectBlockDisintegrate e = new EffectBlockDisintegrate(Minecraft.getMinecraft().world,
//							new Vec3d(at).add(1.5, 1.5, 0.5), entity.world.getBlockState(at));
//					Effect.addEffect(e);
//				}, 10);
//
//				EffectElementCrackAttack effect = new EffectElementCrackAttack(Minecraft.getMinecraft().world,
//						new Vec3d(pos).add(0.5, 1.5, 0.5));
//				Effect.addEffect(effect);

//				entity.world.setBlockToAir(pos);
//				PocketWatch.stopWorld(entity.world, 20 * 10, entity);
//				ElementExplosion.doExplosion(entity.world, new Vec3d(pos),
//						new ElementStack(ESObjects.ELEMENTS.STAR, 100, 500), entity);
//				System.out.println(((EntityPlayer) entity).getLuck());

//				entity.world.setBlockState(pos, Blocks.CHEST.getDefaultState());
//				TileEntityChest chest = (TileEntityChest) entity.world.getTileEntity(pos);
//				chest.setLootTable(LootRegister.ES_VILLAGE_HALL, RandomHelper.rand.nextLong());
			}
				return;
			case "blockMoveTest": {
				EntityBlockMove blockMove = new EntityBlockMove(entity.world, pos, entity.getPosition().down());
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
				return;
			}

			case "statistics": {
				sender.sendMessage(new TextComponentString("手离开游戏，不要动"));
				DevelopStatistics.clearAll();
				World world = entity.getEntityWorld();
				BlockPos at = entity.getPosition();
				ChunkPos cp = new ChunkPos(at);
				DevelopStatistics.record(world, 90, cp);
			}
			case "statisticsHandle": {
				DevelopStatistics.clearAll();
				DevelopStatistics.handleResult();
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

	static public void printBuildingPos(Building building) {
		BuildingBlocks iter = building.getBuildingIterator();
		StringBuilder builder = new StringBuilder();
		while (iter.next()) {
			BlockPos pos = iter.getPos();
			builder.append(pos.getX()).append(',').append(pos.getY()).append(',').append(pos.getZ()).append(',');
		}
		StringSelection stringSelection = new StringSelection(builder.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		System.out.println(builder);
	}

}
