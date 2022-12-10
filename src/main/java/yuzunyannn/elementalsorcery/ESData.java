package yuzunyannn.elementalsorcery;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ESData {

	public static final String PLAYER_RESEARCHABLE = "cResearch";

	private File file;

	public ESData(FMLPreInitializationEvent event) {
		file = event.getSourceFile().getParentFile().getParentFile();
		file = new File(file.getPath() + "/Elemental Sorcery/");
		file.mkdirs();
	}

	public File getFolder() {
		return file;
	}

	/** 获取mod的存储路径下的某个目录下的文件 */
	public File getFile(String filepath, String filename) {
		File file = new File(this.file.getPath() + "/" + filepath);
		file.mkdirs();
		return new File(file.getPath() + "/" + filename);
	}

	/** 获取玩家数据集 */
	public static NBTTagCompound getPlayerNBT(EntityLivingBase player) {
		NBTTagCompound data = player.getEntityData();
		if (data.hasKey("ESData", 10)) return data.getCompoundTag("ESData");
		NBTTagCompound nbt = new NBTTagCompound();
		data.setTag("ESData", nbt);
		return nbt;
	}

	/** 记录ES玩家动态数据 */
	private static final Map<String, NBTTagCompound> userData = new HashMap<String, NBTTagCompound>();

	/** 获取玩家动态数据，不会储存 */
	public static NBTTagCompound getRuntimeData(EntityLivingBase player) {
		if (player instanceof EntityPlayer) return getRuntimeData(player.getName());
		return new NBTTagCompound();
	}

	public static void removeRuntimeData(EntityPlayer player) {
		removeRuntimeData(player.getName());
	}

	public static NBTTagCompound getRuntimeData(String username) {
		if (!userData.containsKey(username)) userData.put(username, new NBTTagCompound());
		return userData.get(username);
	}

	public static void removeRuntimeData(String username) {
		userData.remove(username);
	}

}
