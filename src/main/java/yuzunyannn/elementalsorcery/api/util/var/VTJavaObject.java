package yuzunyannn.elementalsorcery.api.util.var;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import yuzunyannn.elementalsorcery.api.ESAPI;

public class VTJavaObject implements IVariableType<Object> {

	@Override
	public Object newInstance(NBTBase base) {
		if (base instanceof NBTTagByteArray) {
			byte[] bytes = ((NBTTagByteArray) base).getByteArray();
			try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
				return objIn.readObject();
			} catch (Exception e) {}
		}
		return null;
	}

	@Override
	public NBTTagByteArray serializable(Object obj) {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		try (ObjectOutputStream objOut = new ObjectOutputStream(outstream)) {
			objOut.writeObject(obj);
			return new NBTTagByteArray(outstream.toByteArray());
		} catch (Exception e) {
			if (ESAPI.isDevelop) ESAPI.logger.warn("java obj 序列化失败", e);
		}
		return new NBTTagByteArray(new byte[0]);
	}
}
