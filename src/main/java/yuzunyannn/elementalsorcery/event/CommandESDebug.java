package yuzunyannn.elementalsorcery.event;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.building.ArcInfo;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingBlocks;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.building.BuildingSaveData;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.dungeon.DungeonArea;
import yuzunyannn.elementalsorcery.dungeon.DungeonLib;
import yuzunyannn.elementalsorcery.dungeon.DungeonWorld;
import yuzunyannn.elementalsorcery.elf.quest.Quests;
import yuzunyannn.elementalsorcery.elf.research.ResearchRecipeManagement;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.item.tool.ItemMagicRuler;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;
import yuzunyannn.elementalsorcery.util.render.Shaders;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class CommandESDebug {

	private static boolean passpass = false;
	public static final String[] autoTips = new String[] { "reflush", "reflushLootTable", "buildTest", "portalTest",
			"showInfo", "blockMoveTest", "textTest", "reloadeTexture", "quest", "statistics", "statisticsHandle",
			"reloadShader", "jsonSchema" };

	/** debug 测试内容，不进行本地化 */
	static void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		switch (args[0]) {
		case "reloadShader": {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				try {
					Shaders.ElementSky.reload();
				} catch (Exception e) {
					ESAPI.logger.warn(e);
				}
			});
			return;
		}
		// 重新加载材质
		case "reloadeTexture": {
			String path = args[1];
			if (path.indexOf(':') == -1) reloadTexture(new ResourceLocation(ESAPI.MODID, path));
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
				DungeonLib.registerAllFunc();
				sender.sendMessage(new TextComponentString("地牢Func刷新成功!"));
			} catch (Exception e) {
				ESAPI.logger.warn("刷新数据出现异常！", e);
				sender.sendMessage(new TextComponentString("刷新数据出现异常！Refresh data exception!"));
			}
		}
			return;
		case "reflushLootTable": {
			EntityLivingBase entity = (EntityLivingBase) sender.getCommandSenderEntity();
			entity.world.getLootTableManager().reloadLootTables();
		}
			return;
		case "quest": {

			return;
		}
		default:
			EntityLivingBase entity = (EntityLivingBase) sender.getCommandSenderEntity();
			RayTraceResult rtr = WorldHelper.getLookAtBlock(entity.world, entity, 64);
			World world = entity.world;
			Random rand = world.rand;
			BlockPos pos = rtr != null ? rtr.getBlockPos() : null;
			switch (args[0]) {
			// 测试建筑
			case "buildTest":
				try {
					String name = args[1];
					if (name == null || name.isEmpty()) {
						System.out.println("你的id呢");
						return;
					}

					EntityPlayer executer = (EntityPlayer) sender.getCommandSenderEntity();
					ItemStack ruler = executer.getHeldItem(EnumHand.MAIN_HAND);
					BlockPos pos1 = ItemMagicRuler.getRulerPos(ruler, true);
					BlockPos pos2 = ItemMagicRuler.getRulerPos(ruler, false);
					if (pos1 == null || pos2 == null) throw new WrongUsageException("commands.es.building.recordFail");
					Building building = Building.createBuilding(sender.getEntityWorld(),
							executer.getHorizontalFacing().getOpposite(), pos1, pos2, true);
					building.setAuthor("yuzunyannn");
					building.setName(TextHelper.castToCamel(name));
					BuildingLib.instance.releaseAllSaveData();
					Method method = BuildingLib.class.getDeclaredMethod("addBuilding", BuildingSaveData.class);
					method.setAccessible(true);
					String path = "../src/main/resources/assets/elementalsorcery/structures/" + name + ".nbt";
					File file = new File(path);
					if (file.exists() && !passpass) {
						System.out.println("存在" + name + "了，请三四而后行！");
						passpass = true;
						return;
					}
					passpass = false;
					method.invoke(BuildingLib.instance, new BuildingSaveData(building, name, file) {
						{}
					});
					ItemHelper.addItemStackToPlayer(executer, ArcInfo.createArcInfoItem(building.getKeyName()));
					System.out.println("好了！");
					BuildingLib.instance.releaseAllSaveData();
//				GenElfEdifice g = new GenElfEdifice(true);
//				g.genMainTreeEdifice(entity.world, pos, entity.world.rand);
//				g.buildToTick(entity.world);
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
				} catch (Exception e) {
					ESAPI.logger.error("gg", e);
				}
				return;
			case "jsonSchema": {
				JsonObject obj = new JsonObject();
				obj.set("$schema", "http://json-schema.org/draft-07/schema#");
				JsonArray array = new JsonArray();
				obj.set("enum", array);
				for (ResourceLocation key : Item.REGISTRY.getKeys()) array.append(key.toString());
				obj.save(new File("../json schema/item_ids.json"), true);

			} {
				JsonObject obj = new JsonObject();
				obj.set("$schema", "http://json-schema.org/draft-07/schema#");
				JsonArray array = new JsonArray();
				obj.set("enum", array);
				for (ResourceLocation key : EntityList.getEntityNameList()) array.append(key.toString());
				obj.save(new File("../json schema/entity_ids.json"), true);

			}
				return;
			case "textTest": {

				EntityPlayerMP player = (EntityPlayerMP) entity;
					
				boolean isDebugBuild = false;
				DungeonWorld dw = DungeonWorld.getDungeonWorld(player.world);
				dw.debugClear();
				DungeonArea area = dw.newDungeon(pos);
				if (area.isFail()) System.out.println(area.getFailMsg());
				else {
					if (isDebugBuild) area.debugBuildDungeon(player.world);
					else area.startBuildRoom(player.world, 0, player);
				}

//				EntityPlayer fakePlayer = ESFakePlayer.get(player.getServerWorld());
//				player.attackEntityFrom(
//						DamageHelper.getDamageSource(new ElementStack(ESObjects.ELEMENTS.MAGIC), fakePlayer, null)
//								.setDamageAllowedInCreativeMode(),
//						999999);
//				System.out.println(EnumFacing.EAST.getDirectionVec());

//				final Vec3d at = new Vec3d(pos);
//				Random rand = entity.getRNG();

//				Vec3d look = entity.getLookVec();
//				for (int i = 0; i < 3; i++) {
//					AutoMantraConfig config = new EntityAutoMantra.AutoMantraConfig();
////					config.setMoveVec(new Vec3d(look.x, 0, look.z).normalize().scale(0.01));
//					config.setMoveVec(Vec3d.ZERO);
////					config.blockTrack = AutoMantraConfig.BLOCKTRACK_DIRECT_REVERSE;
////					config.setTarget(entity, 0.01);
////					config.excludeUser = false;
//					EntityAutoMantra mantra = new EntityAutoMantra(entity.world, config, entity,
//							ESObjects.MANTRAS.LASER, null);
//					mantra.setPosition(entity.posX + rand.nextGaussian() * 2, entity.posY + 3,
//							entity.posZ + rand.nextGaussian() * 2);
//					mantra.setSpellingTick(20 * 3);
//					mantra.setOrient(new Vec3d(0, -1, 0));
//					IElementInventory elementInv = mantra.getElementInventory();
//					elementInv.insertElement(new ElementStack(Element.REGISTRY.getRandomObject(rand), 9999, 20), false);
//					elementInv.insertElement(new ElementStack(ESObjects.ELEMENTS.FIRE, 9999, 20), false);
//					elementInv.insertElement(new ElementStack(ESObjects.ELEMENTS.WATER, 9999, 20), false);
//					elementInv.insertElement(new ElementStack(ESObjects.ELEMENTS.AIR, 9999, 20), false);
//					entity.world.spawnEntity(mantra);
//				}

				Minecraft.getMinecraft().addScheduledTask(() -> {
//					LamdaReference<Integer> ref = LamdaReference.of(0);
//					EventClient.addTickTask(() -> {
//						ref.set(ref.get() + 1);
//						if (ref.get() > 100) return ITickTask.END;
//						for (int i = 0; i < 10; i++) {
//							EffectSnow snow = new EffectSnow(Minecraft.getMinecraft().world,
//									at.add(Effect.rand.nextGaussian(), Effect.rand.nextGaussian() + 1, Effect.rand.nextGaussian()));
//							snow.xAccelerate = 0.1f;
//							snow.setDecay(Effect.rand.nextFloat() * 0.2 + 0.5);
//							Effect.addEffect(snow);
//						}
//						return ITickTask.SUCCESS;
//					});
//					EffectIceCrystalBomb effect = new EffectIceCrystalBomb(Minecraft.getMinecraft().world, at);
//					effect.setCondition(e -> true);
//					Effect.addEffect(effect);
//					EffectIceCrystalBomb.playEndBlastEffect(Minecraft.getMinecraft().world, at.add(0, 1, 0), true);
				});

//				int n = 0;
//				for (int i = 0; i < 100000; i++) {
//					ElementStack eStack1 = new ElementStack(ESObjects.ELEMENTS.FIRE, RandomHelper.rand.nextInt(10000),
//							RandomHelper.rand.nextInt(10000));
//					ElementStack eStack2 = new ElementStack(ESObjects.ELEMENTS.FIRE, RandomHelper.rand.nextInt(10000),
//							RandomHelper.rand.nextInt(10000));
//					ElementStack eStack3 = new ElementStack(ESObjects.ELEMENTS.FIRE, RandomHelper.rand.nextInt(10000),
//							RandomHelper.rand.nextInt(10000));
//
//					ElementStack old = eStack1.copy();
//					old.grow(eStack3);
//
//					eStack1.grow(eStack2);
//					eStack1.grow(eStack3);
//
//					eStack1.disgrow(eStack2);
//					if (old.getCount() != eStack1.getCount() || old.getPower() != eStack1.getPower()) {
//						n = n + old.getPower() - eStack1.getPower();
//					}
//				}
//				System.out.println(n / 100000.0);

//				MantraCrackOpen.attack(entity.world, pos, 16, entity, 0, true);

//				for (int i = 0; i < 32; i++) {
//					EntityItemGoods goods = new EntityItemGoods(entity.world,
//							new Vec3d(pos.up(5)).add(RandomHelper.rand.nextDouble() * 5, RandomHelper.rand.nextDouble(),
//									RandomHelper.rand.nextDouble() * 5),
//							new ItemStack(Item.REGISTRY.getRandomObject(RandomHelper.rand), 2));
//					goods.setPrice(RandomHelper.rand.nextInt(1000));
//					entity.world.spawnEntity(goods);
//				}
//
//				if (entity instanceof EntityPlayerMP) {
//					EntityPlayerMP player = (EntityPlayerMP) entity;
//					player.capabilities.allowFlying = true;
//					player.capabilities.isFlying = false;
//					player.sendPlayerAbilities();
//					player.setElytraFlying();
//				}

//				EffectFragmentCrackMove.spawnBoom(Minecraft.getMinecraft().world, new Vec3d(pos.up()), 0xffffff, 2);
//
//				EffectCylinderCrackBlast effect = new EffectCylinderCrackBlast(Minecraft.getMinecraft().world,
//						new Vec3d(pos.up()));
//				Effect.addEffect(effect);

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
//				World world = server.getWorld(0);
				EntityPortal.createPortal(sender.getEntityWorld(), pos, world, new BlockPos(754, 65, 766));
			}
				return;
			// 展示一些信息
			case "showInfo": {
//				World world = server.getEntityWorld();
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
//				World world = entity.getEntityWorld();
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
