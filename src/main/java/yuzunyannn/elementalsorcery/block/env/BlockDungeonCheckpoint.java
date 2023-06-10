package yuzunyannn.elementalsorcery.block.env;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.container.gui.GuiDungeonMap;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoom;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoomSpecialThing;
import yuzunyannn.elementalsorcery.dungeon.DungeonWorld;
import yuzunyannn.elementalsorcery.dungeon.IDungeonSpecialThing;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraEnderTeleport;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class BlockDungeonCheckpoint extends Block implements IDungeonSpecialThing {

	public BlockDungeonCheckpoint() {
		super(Material.ROCK);
		this.setSoundType(SoundType.STONE);
		this.setTranslationKey("dungeonCheckpoint");
		this.setHardness(64f);
		this.setLightLevel(1);
		useNeighborBrightness = true;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
	}

	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);
		this.onSpecialThingBuild(worldIn, pos, new DungeonAreaRoomSpecialThing(this));
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		this.onSpecialThingRemove(worldIn, pos);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		// 被沉默
		if (EntityHelper.checkSilent(playerIn, SilentLevel.PHENOMENON)) return false;

		if (worldIn.isRemote) return true;

		// check
		DungeonWorld dw = DungeonWorld.getDungeonWorld(worldIn);
		DungeonAreaRoom room = dw.getAreaRoom(pos);
		if (room == null) return true;

		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_DUNGEON_MAP, worldIn, pos.getX(), pos.getY(),
				pos.getZ());

		return true;
	}

	@Override
	public boolean hasClickHandle() {
		return true;
	}

	@Override
	public void executeClick(World world, BlockPos pos, EntityPlayer player, DungeonAreaRoom room,
			@Nullable BlockPos fromSpecialPos) {
		if (world.isRemote) return;

		Vec3d to = null;

		if (fromSpecialPos != null) {
			Vec3d playerVec = player.getPositionVector();
			Vec3d currPos = new Vec3d(fromSpecialPos);
			to = playerVec.subtract(currPos).add(new Vec3d(pos));
		}

		if (to == null) {
			BlockPos toPos = WorldHelper.tryFindPlaceToSpawn(world, world.rand, pos.up(3), 3);
			if (toPos != null) to = new Vec3d(toPos).add(0.5, 1, 0.5);
			else to = new Vec3d(pos).add(0.5, 1, 0.5);
		}

		MantraEnderTeleport.playEnderTeleportEffect(world, player, to);
		MantraEnderTeleport.doEnderTeleport(world, player, to);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTitle(DungeonAreaRoom room, boolean isHover) {
		if (isHover) return I18n.format("info.dungeon.teleport.title.click");
		return I18n.format("info.dungeon.teleport.title");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderMiniIcon(Minecraft mc, float x, float y, float size) {
		mc.getTextureManager().bindTexture(GuiDungeonMap.TEXTURE);
		RenderFriend.drawTextureRectInCenter(x, y, size, size, 0, 56, 8, 8, 256, 256);
	}
}
