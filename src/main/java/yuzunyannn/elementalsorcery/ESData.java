package yuzunyannn.elementalsorcery;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.utils.IOUtils;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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

	public NBTTagCompound getNBTForResource(String resourcePath) {
		InputStream istream = ESData.class.getResourceAsStream("/assets/elementalsorcery/" + resourcePath);
		NBTTagCompound nbt = null;
		try {
			nbt = CompressedStreamTools.readCompressed(istream);
		} catch (IOException e) {
			ElementalSorcery.logger.warn("无法读取NBT从：" + "/assets/elementalsorcery/" + resourcePath);
		}
		IOUtils.closeQuietly(istream);
		return nbt;
	}

	public NBTTagCompound getNBTForResourceWithException(String resourcePath) throws IOException {
		InputStream istream = ESData.class.getResourceAsStream("/assets/elementalsorcery/" + resourcePath);
		NBTTagCompound nbt = null;
		try {
			nbt = CompressedStreamTools.readCompressed(istream);
		} finally {
			IOUtils.closeQuietly(istream);
		}
		return nbt;
	}
}
