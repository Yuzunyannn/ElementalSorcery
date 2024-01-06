package yuzunyannn.elementalsorcery.api.computer;

public interface ISyncWatcher {

	boolean isLeave();

	<T> T getDetectObject(Class<T> cls);

	<T> void setDetectObject(T obj);

}
