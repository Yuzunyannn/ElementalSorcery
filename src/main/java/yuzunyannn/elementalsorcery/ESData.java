package yuzunyannn.elementalsorcery;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
		file = new File(file.getPath() + "/Elemental Sorcery/");
		file.mkdirs();
	}

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

	static public JsonObject getJsonFromResource(ResourceLocation path) throws IOException {
		String rPath = "/assets/" + path.getResourceDomain() + "/" + path.getResourcePath();
		InputStream istream = null;
		JsonObject obj = null;
		try {
			istream = ESData.class.getResourceAsStream(rPath);
			Gson gson = new Gson();
			obj = gson.fromJson(new InputStreamReader(istream), JsonObject.class);
		} finally {
			IOHelper.closeQuietly(istream);
		}
		return obj;
	}

	static public String[] getFilesFromResource(ResourceLocation path) throws IOException {
		List<ModContainer> mods = Loader.instance().getModList();
		for (ModContainer mc : mods) {
			if (mc.getModId().equals(path.getResourceDomain())) {
				File file = mc.getSource();
				if (file.isDirectory()) {
					String rPath = "/assets/" + path.getResourceDomain() + "/" + path.getResourcePath();
					file = new File(file.getAbsolutePath() + rPath);
					return file.list();
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
									if (name.isEmpty()) continue;
									if (name.equals("/")) continue;
									if (name.indexOf('/') == 0) name = name.substring(1);
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
}
