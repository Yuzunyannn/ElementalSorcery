package yuzunyannn.elementalsorcery;

import java.io.File;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ESData {

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

}
