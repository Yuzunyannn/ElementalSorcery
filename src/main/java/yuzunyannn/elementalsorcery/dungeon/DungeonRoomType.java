package yuzunyannn.elementalsorcery.dungeon;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.api.util.ESImplRegister;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;

public class DungeonRoomType extends IForgeRegistryEntry.Impl<DungeonRoomType> {

	public static final ESImplRegister<DungeonRoomType> REGISTRY = new ESImplRegister(ElfProfession.class);

	static public final AxisAlignedBB ZERO_BOX = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

	protected AxisAlignedBB buildingBox = ZERO_BOX;
	protected List<DungeonRoomDoor> doors = new ArrayList<>();

	/** 获取建筑的静态大小 */
	public AxisAlignedBB getBuildingBox() {
		return buildingBox;
	}

	/** 获取建筑的房间可通行门，返回的数据应该是唯一的，下标作为id */
	public List<DungeonRoomDoor> getDoors() {
		return doors;
	}

	/** 建造 */
	public void build(World world, DungeonArea area, DungeonAreaRoom room) {

	}

}
