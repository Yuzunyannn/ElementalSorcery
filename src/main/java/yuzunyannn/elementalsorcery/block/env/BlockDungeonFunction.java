package yuzunyannn.elementalsorcery.block.env;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.gfunc.GameFunc;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.tile.dungeon.TileDungeonFunction;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class BlockDungeonFunction extends Block implements ITileEntityProvider {

	public class ItemDFB extends ItemBlock {

		public ItemDFB() {
			super(BlockDungeonFunction.this);
		}

		@Override
		public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
				EnumFacing facing, float hitX, float hitY, float hitZ) {
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity != null && hand == EnumHand.OFF_HAND) {
				NBTTagCompound tileData = tileEntity.getTileData();
				if (trySet(player, json -> {
					tileData.setString("_dungeon_func_config_", json.toString());
					tileEntity.markDirty();
				}, false)) return EnumActionResult.SUCCESS;
			}
			return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		}

	}

	public ItemBlock getItemBlock() {
		return new ItemDFB();
	}

	public BlockDungeonFunction() {
		super(Material.ROCK);
		this.setTranslationKey("dungeonFunction");
		this.setSoundType(SoundType.STONE);
		this.setHardness(-1);
		this.setResistance(6000000.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDungeonFunction();
	}

	protected boolean trySet(EntityPlayer playerIn, TileDungeonFunction tile, boolean needFailMsg) {
		return trySet(playerIn, json -> {
			tile.setConfig(json);
			tile.markDirty();
		}, needFailMsg);
	}

	protected static boolean trySet(EntityPlayer playerIn, Consumer<JsonObject> configSetter, boolean needFailMsg) {
		Style badStyle = new Style().setColor(TextFormatting.RED);
		Style niceStyle = new Style().setColor(TextFormatting.GOLD);

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable trans = clipboard.getContents(null);
		if (trans == null) {
			if (!needFailMsg) return false;
			playerIn.sendMessage(new TextComponentString("no thing copied!").setStyle(badStyle));
			return false;
		}
		try {
			String moreInfo = "";
			JsonObject json = null;
			if (trans.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				List<File> files = (List<File>) trans.getTransferData(DataFlavor.javaFileListFlavor);
				File file = files.get(0);
				String path = file.getAbsolutePath();
				path = path.replace("\\", "/");
				String pPath = "src/main/resources/assets/elementalsorcery/dungeon/";
				int pIndex = path.indexOf(pPath);
				if (pIndex != -1) {
					String filePath = path.substring(pIndex + pPath.length());
					json = new JsonObject();
					json.set("/assets", ESAPI.MODID + ":dungeon/" + filePath);
					moreInfo = "from project dungeon/" + filePath;
				} else {
					json = (JsonObject) JsonObject.parser(file);
					moreInfo = "from file " + path;
				}
			} else {
				String text = (String) trans.getTransferData(DataFlavor.stringFlavor);
				if (text.isEmpty()) throw new RuntimeException();
				json = (JsonObject) JsonObject.parser(text);
				moreInfo = "from copied string.";
			}
			if (json == null) throw new RuntimeException();
			configSetter.accept(json);
			playerIn.sendMessage(new TextComponentString("set Config success! " + moreInfo).setStyle(niceStyle));
			return true;
		} catch (Exception e) {
			if (!needFailMsg) return false;
			playerIn.sendMessage(new TextComponentString("copied data is not json!").setStyle(badStyle));
			ESAPI.logger.info("copied data is not json!", e);
			return false;
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return true;
		Style badStyle = new Style().setColor(TextFormatting.RED);
//		Style niceStyle = new Style().setColor(TextFormatting.GOLD);
		TileDungeonFunction tile = BlockHelper.getTileEntity(worldIn, pos, TileDungeonFunction.class);
		if (tile == null) {
			playerIn.sendMessage(new TextComponentString("tile is miss!").setStyle(badStyle));
			return true;
		}

		if (!playerIn.isSneaking()) {
			Style style = new Style().setColor(TextFormatting.AQUA).setBold(true);
			GameFunc func = tile.createTextDungeonFunc();
			if (func == GameFunc.NOTHING)
				playerIn.sendMessage(new TextComponentString("cannot create dungeon function").setStyle(badStyle));
			else {
				func.setSeed(RandomHelper.rand.nextLong());
				String assets = tile.getAssetsPath();
				if (assets != null) {
					Style style2 = new Style().setColor(TextFormatting.GOLD).setBold(true);
					playerIn.sendMessage(new TextComponentString("assets: " + assets).setStyle(style2));
				}
				playerIn.sendMessage(new TextComponentString(func.toString()).setStyle(style));
				ItemStack stack = playerIn.getHeldItem(hand);
				if (stack.getItem() == Items.WOODEN_AXE || stack.getItem() == Items.STONE_AXE) {
					GameFuncExecuteContext context = new GameFuncExecuteContext();
					context.setSrcObj(worldIn, pos);
					context.doExecute(func);
				}
			}
			return true;
		}

		trySet(playerIn, tile, true);

		return true;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		if (worldIn.isRemote) return;
		TileDungeonFunction tile = BlockHelper.getTileEntity(worldIn, pos, TileDungeonFunction.class);
		if (placer instanceof EntityPlayer && tile != null) {
			trySet((EntityPlayer) placer, tile, false);
		}
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
		return false;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
	}

}
