package yuzunyannn.elementalsorcery.api.computer.soft;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.computer.exception.ComputerException;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;

public interface IComputerException {

	default boolean isGameException() {
		return false;
	}

	default Object getGameRenderObject() {
		return null;
	}

	static public NBTTagCompound serialize(IComputerException exception) {
		NBTSender sender = new NBTSender();
		if (exception.isGameException()) {
			sender.write("type", (byte) 1);
			sender.writeDisplay("robj", exception.getGameRenderObject());
		} else if (exception instanceof ComputerExceptionOnlyMsg) {
			sender.write("type", (byte) 0);
			sender.write("msg", exception.toString());
		} else if (exception instanceof ComputerExceptionJavaTransparent) {
			sender.write("type", (byte) 0);
			sender.write("msg", exception.toString());
		} else {
			sender.write("type", (byte) 0);
			sender.write("msg", exception.toString());
		}
		return sender.tag();
	}

	static public IComputerException deserialize(NBTTagCompound tag) {
		NBTSender sender = new NBTSender(tag);
		if (sender.has("type")) {
			int type = sender.nint("type");
			if (type == 0) return IComputerException.easy(sender.string("msg"));
			else if (type == 1) return new ComputerExceptionRenderObject(sender.display("robj"));
		}
		if (tag.isEmpty()) return null;
		return IComputerException.easy(tag.toString());
	}

	static IComputerException easy(String msg) {
		return new ComputerExceptionOnlyMsg(msg);
	}

	static IComputerException easy(Throwable e) {
		if (e instanceof ComputerException) return (IComputerException) e;
		return new ComputerExceptionJavaTransparent(e);
	}

	static IComputerException easy(String msg, Throwable e) {
		if (e instanceof ComputerException) return (IComputerException) e;
		return new ComputerExceptionJavaTransparent(e);
	}

	static class ComputerExceptionRenderObject implements IComputerException {
		final Object renderObj;

		public ComputerExceptionRenderObject(Object renderObj) {
			this.renderObj = renderObj;
		}

		@Override
		public String toString() {
			return String.valueOf(renderObj);
		}

		@Override
		public boolean isGameException() {
			return true;
		}

		@Override
		public Object getGameRenderObject() {
			return renderObj;
		}
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
