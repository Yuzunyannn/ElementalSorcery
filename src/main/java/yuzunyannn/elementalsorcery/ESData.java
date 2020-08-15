package yuzunyannn.elementalsorcery;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import yuzunyannn.elementalsorcery.util.IOHelper;

public class ESData {

	private File file;

	public ESData(FMLPreInitializationEvent event) {
		file = event.getSourceFile().getParentFile().getParentFile();
		file = new File(file.getPath() + "/ElementalSorcery/");
		file.mkdirs();
	}

	/** 获取mod的存储路径下的某个目录下的文件 */
	public File getFile(String filepath, String filename) {
		File file = new File(this.file.getPath() + "/" + filepath);
		file.mkdirs();
		return new File(file.getPath() + "/" + filename);
	}

	public NBTTagCompound getNBTFromResource(ResourceLocation path) throws IOException {
		String rPath = "/assets/" + path.getResourceDomain() + "/" + path.getResourcePath();
		InputStream istream = null;
		NBTTagCompound nbt = null;
		try {
			istream = ESData.class.getResourceAsStream(rPath);
			nbt = CompressedStreamTools.readCompressed(istream);
		} finally {
			IOHelper.closeQuietly(istream);
		}
		return nbt;
	}

	/** 获取某个文件夹下的全部路径，注意检测后缀，jar下可能获取到目录 */
	static public String[] getFilesFromResource(ResourceLocation path) throws IOException {
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

	static private void getFileRecursion(String lastPath, List<String> paths, File root) {
		File[] files = root.listFiles();
		for (File file : files) {
			if (file.isDirectory()) getFileRecursion(lastPath + file.getName() + "/", paths, file);
			else paths.add(lastPath + file.getName());
		}
	}
}
