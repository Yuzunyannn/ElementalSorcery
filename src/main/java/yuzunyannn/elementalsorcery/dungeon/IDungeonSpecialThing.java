package yuzunyannn.elementalsorcery.dungeon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IDungeonSpecialThing {

	@SideOnly(Side.CLIENT)
	default public void renderMiniIcon(Minecraft mc, float x, float y, float size) {

	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	default String getTitle(boolean isHover) {
		return "";
	}

	default boolean hasClickHandle() {
		return false;
	}

	default void executeClick(World world, BlockPos pos, EntityPlayer player, DungeonAreaRoom room,
			@Nullable BlockPos fromSpecialPos) {

	}

	default void onSpecialThingBuild(World world, BlockPos pos, DungeonAreaRoomSpecialThing thing) {
		if (world.isRemote) return;
		DungeonWorld dworld = DungeonWorld.getDungeonWorld(world);
		DungeonAreaRoom room = dworld.getAreaRoom(pos);
		if (room == null) return;
		room.setSpecialThing(pos, thing);
		dworld.markDirty(room);
	}

	default void onSpecialThingRemove(World world, BlockPos pos) {
		if (world.isRemote) return;
		DungeonWorld dworld = DungeonWorld.getDungeonWorld(world);
		DungeonAreaRoom room = dworld.getAreaRoom(pos);
		if (room == null) return;
		room.setSpecialThing(pos, null);
		dworld.markDirty(room);
	}

}
