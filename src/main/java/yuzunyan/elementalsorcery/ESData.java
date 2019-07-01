package yuzunyan.elementalsorcery;

import java.io.File;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ESData {

	private File file;

	public ESData(FMLPreInitializationEvent event) {
		file = event.getSourceFile().getParentFile().getParentFile();
		file = new File(file.getPath() + "/ElementalSorcery/");
		file.mkdirs();
	}

	public File getESFile(String filepath, String filename) {
		File file = new File(this.file.getPath() + "/" + filepath);
		file.mkdirs();
		return new File(file.getPath() + "/" + filename);
	}
}
