package yuzunyannn.elementalsorcery.api.computer.soft;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.util.NBTTag;

public interface IComputerException {

	static public NBTTagCompound serialize(IComputerException exception) {
		NBTTagCompound nbt = new NBTTagCompound();
		if (exception instanceof ComputerExceptionOnlyMsg) {
			nbt.setByte("type", (byte) 0);
			nbt.setString("msg", exception.toString());
		} else if (exception instanceof ComputerExceptionJavaTransparent) {
			nbt.setByte("type", (byte) 0);
			nbt.setString("msg", exception.toString());
		} else {
			nbt.setByte("type", (byte) 0);
			nbt.setString("msg", exception.toString());
		}
		return nbt;
	}

	static public IComputerException deserialize(NBTTagCompound tag) {
		if (tag.hasKey("type", NBTTag.TAG_NUMBER)) {
			int type = tag.getInteger("type");
			if (type == 0) return IComputerException.easy(tag.getString("msg"));
		}
		if (tag.isEmpty()) return null;
		return IComputerException.easy(tag.toString());
	}

	static IComputerException easy(String msg) {
		return new ComputerExceptionOnlyMsg(msg);
	}

	static IComputerException easy(Throwable e) {
		return new ComputerExceptionJavaTransparent(e);
	}

	static IComputerException easy(String msg, Throwable e) {
		return new ComputerExceptionJavaTransparent(e);
	}

	static class ComputerExceptionOnlyMsg implements IComputerException {
		final String msg;

		public ComputerExceptionOnlyMsg(String msg) {
			this.msg = msg;
		}

		@Override
		public String toString() {
			return this.msg;
		}
	}

	static class ComputerExceptionJavaTransparent implements IComputerException {
		final public Throwable e;

		public ComputerExceptionJavaTransparent(Throwable e) {
			this.e = e;
		}

		@Override
		public String toString() {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		}
	}

}
