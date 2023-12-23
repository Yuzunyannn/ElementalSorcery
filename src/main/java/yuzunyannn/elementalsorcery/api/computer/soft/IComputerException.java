package yuzunyannn.elementalsorcery.api.computer.soft;

public interface IComputerException {

	static IComputerException easy(String msg) {
		return new ComputerExceptionOnlyMsg(msg);
	}

	static class ComputerExceptionOnlyMsg implements IComputerException {
		public ComputerExceptionOnlyMsg(String msg) {
		}
	}

}
