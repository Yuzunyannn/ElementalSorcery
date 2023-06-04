package yuzunyannn.elementalsorcery.dungeon;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.network.MessageDungeonSync;

@SideOnly(Side.CLIENT)
public class DungeonWorldClient extends DungeonWorld {

	static public final Map<World, DungeonWorldClient> worldClientMap = new WeakHashMap<>();

	public static DungeonWorldClient getDungeonWorld(World world) {
		if (world == null) return null;
		DungeonWorldClient worldClient = worldClientMap.get(world);
		if (worldClient != null) return worldClient;
		worldClientMap.put(world, worldClient = new DungeonWorldClient(world));
		return worldClient;
	}

	protected Map<Integer, DungeonArea> dungeonMap = new HashMap<>();

	public DungeonWorldClient(World world) {
		this.world = world;
	}

	@Override
	public DungeonArea getDungeon(int id) {
		if (dungeonMap.containsKey(id)) return dungeonMap.get(id);
		dungeonMap.put(id, null);
		ESNetwork.instance.sendToServer(new MessageDungeonSync(id));
		return null;
	}

	public DungeonArea getDungeonRaw(int id) {
		return dungeonMap.get(id);
	}

	@Override
	public DungeonArea newDungeon(BlockPos pos) {
		throw new RuntimeException("new dungeon is server only!");
	}

	@Override
	protected DungeonWorldLand getOrCreateWorldLand(int x, int z) {
		throw new RuntimeException("create world land only can be in server!");
	}

	@Override
	public DungeonWorldLand getWorldLand(int x, int z) {
		// DungeonPos pos = new DungeonPos(x, z);
		// if (wlMap.containsKey(pos)) return wlMap.get(pos);
		// wlMap.put(pos, null);
		// ESNetwork.instance.sendToServer(new MessageDungeonSync(pos));
		return null;
	}

	public void setWorldLand(DungeonPos pos, DungeonWorldLand land) {
		wlMap.put(pos, land);
	}

	public void setDungeonArea(int id, DungeonArea area) {
		this.dungeonMap.put(id, area);
	}

}
