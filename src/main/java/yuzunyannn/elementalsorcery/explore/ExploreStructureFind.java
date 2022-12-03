package yuzunyannn.elementalsorcery.explore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ExploreStructureFind implements IExploreHandle {

	public static final String VILLAGE = "Village";
	public static final String MANSION = "Mansion";
	public static final String TEMPLE = "Temple";
	public static final String OCEAN_MONUMENTS = "Monument";
	public static final String ABANDONED_MINE_SHAFT = "Mineshaft";
	public static final String ENDER_STRONGHOLD = "Stronghold";
	public static final String NETHER_FORTRESS = "Fortress";

	public static final Map<Integer, List<String>> STRUCTURE_MAP = new TreeMap<>();

	static {
		List<String> mainWorld = new ArrayList<String>();
		mainWorld.add("Temple");
		mainWorld.add("Stronghold");
		mainWorld.add("Mansion");
		mainWorld.add("Monument");
		mainWorld.add("Village");
		mainWorld.add("Mineshaft");
		STRUCTURE_MAP.put(DimensionType.OVERWORLD.getId(), mainWorld);
		List<String> hell = new ArrayList<String>();
		hell.add("Fortress");
		STRUCTURE_MAP.put(DimensionType.NETHER.getId(), hell);
	}

	static public List<String> getStructureList(World world) {
		List<String> list = STRUCTURE_MAP.get(world.provider.getDimension());
		if (list != null) return list;
		return STRUCTURE_MAP.get(DimensionType.OVERWORLD.getId());
	}

	@Override
	public boolean explore(NBTTagCompound data, World world, BlockPos pos, int level, IBlockState state,
			EntityLivingBase portrait) {
		if (level < 2 && state == null) return true;// 在方块模式下也会获取
		if (!data.hasKey("archi")) {
			ChunkProviderServer cp = ((WorldServer) world).getChunkProvider();
			String in = null;
			List<String> list = getStructureList(world);
			for (String str : list) {
				BlockPos to = cp.getNearestStructurePos(world, str, pos, false);
				if (to == null) continue;

				int length = 160;
				// 有方块的情况下，多判断一步
				if (state != null) {
					BlockPos at;
					// 加大y轴区间
					if (Math.abs(to.getY() - pos.getY()) <= 30) at = new BlockPos(pos.getX(), to.getY(), pos.getZ());
					else at = pos;
					if (cp.isInsideStructure(world, str, at)) {
						in = str;
						break;
					}
					switch (str) {
					case "Fortress":
						length = 90;
						break;
					case "Mineshaft":
						length = 64;
						break;
					default:
						length = 16;
						break;
					}
				}

				Vec3d x1 = new Vec3d(pos);
				Vec3d x2 = new Vec3d(to);
				if (x1.squareDistanceTo(x2) <= length * length) {
					in = str;
					break;
				}
			}
			if (in != null) data.setString("archi", in);
			else data.setString("archi", "");
			return false;
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addExploreInfo(NBTTagCompound data, List<String> tooltip) {
		String archi = data.getString("archi");
		if (archi == null) return;
		if (!archi.isEmpty()) {
			archi = ExploreStructureFind.getStructureI18nName(archi);
			tooltip.add(TextFormatting.GREEN + I18n.format("info.around.have", archi));
		}
	}

	@Override
	public boolean hasExplore(NBTTagCompound data) {
		return data.hasKey("archi");
	}

	public String getStructure(NBTTagCompound data) {
		return data.getString("archi");
	}

	@SideOnly(Side.CLIENT)
	public static String getStructureI18nName(String key) {
		key = String.format("info.mc.archi.%s.name", key);
		if (I18n.hasKey(key)) return I18n.format(key);
		return key;
	}

}
