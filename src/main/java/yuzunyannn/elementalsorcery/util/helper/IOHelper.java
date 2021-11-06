package yuzunyannn.elementalsorcery.util.helper;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class IOHelper {

	public static void closeQuietly(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException ignored) {}
		}
	}

	public static JsonObject readJson(ResourceLocation path) throws IOException {
		return new JsonObject(path);
	}

	public static NBTTagCompound readNBT(ResourceLocation path) throws IOException {
		String rPath = "/assets/" + path.getResourceDomain() + "/" + path.getResourcePath();
		try (InputStream istream = ESData.class.getResourceAsStream(rPath)) {
			return CompressedStreamTools.readCompressed(istream);
		}
	}

	public static String readString(ResourceLocation path) throws IOException {
		String rPath = "/assets/" + path.getResourceDomain() + "/" + path.getResourcePath();
		StringBuilder buider = new StringBuilder();
		String line;
		try (InputStream istream = ESData.class.getResourceAsStream(rPath)) {
			BufferedReader br = new BufferedReader(new InputStreamReader(istream, "utf-8"));
			while ((line = br.readLine()) != null) buider.append(line);
		}
		return buider.toString();
	}

	/** 获取某个文件夹下的全部路径，注意检测后缀，jar下可能获取到目录 */
	public static String[] getFilesFromResource(ResourceLocation path) throws IOException {
		List<ModContainer> mods = Loader.instance().getModList();
		for (ModContainer mc : mods) {
			if (mc.getModId().equals(path.getResourceDomain())) {
				File file = mc.getSource();
				if (file.isDirectory()) {
					String rPath = "/assets/" + path.getResourceDomain() + "/" + path.getResourcePath();
					file = new File(file.getAbsolutePath() + rPath);
					List<String> list = new ArrayList<String>();
					getFileRecursion("", list, file);
					return list.toArray(new String[list.size()]);
				} else {
					String rPath = "assets/" + path.getResourceDomain() + "/";
					String iPath = path.getResourcePath();
					if (iPath.indexOf('/') == 0) iPath = iPath.substring(1);
					JarFile jar = null;
					try {
						jar = new JarFile(file);
						Enumeration<JarEntry> entrys = jar.entries();
						List<String> result = new ArrayList<String>();
						while (entrys.hasMoreElements()) {
							JarEntry jarEntry = entrys.nextElement();
							if (jarEntry.getName().startsWith(rPath)) {
								String name = jarEntry.getName();
								name = name.substring(rPath.length()).trim();
								if (name.isEmpty()) continue;
								if (name.startsWith(iPath)) {
									name = name.substring(iPath.length()).trim();
									if (name.indexOf('/') == 0) name = name.substring(1);
									if (name.isEmpty()) continue;
									result.add(name);
								}
							}
						}
						return result.toArray(new String[result.size()]);
					} finally {
						IOHelper.closeQuietly(jar);
					}
				}
			}
		}
		return new String[0];
	}

	/** 获取一个目录下的所有文件 */
	public static List<String> getFileRecursion(File root) {
		List<String> list = new ArrayList<String>();
		getFileRecursion(root.getPath() + "/", list, root);
		return list;
	}

	private static void getFileRecursion(String lastPath, List<String> paths, File root) {
		File[] files = root.listFiles();
		for (File file : files) {
			if (file.isDirectory()) getFileRecursion(lastPath + file.getName() + "/", paths, file);
			else paths.add(lastPath + file.getName());
		}
	}

}
