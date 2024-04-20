package yuzunyannn.elementalsorcery.api.computer.soft;

import javax.annotation.Nullable;

public interface IComputerException {

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
		public ComputerExceptionOnlyMsg(String msg) {
		}
	}

	static class ComputerExceptionJavaTransparent implements IComputerException {
		final public Throwable e;

		public ComputerExceptionJavaTransparent(Throwable e) {
			this.e = e;
		}

		@Override
		public Throwable getOrigin() {
			return this.e;
		}
	}

	@Nullable
	default public Throwable getOrigin() {
		return null;
	}

}
