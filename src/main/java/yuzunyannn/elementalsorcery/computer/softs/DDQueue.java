package yuzunyannn.elementalsorcery.computer.softs;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.nbt.NBTTagList;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataDetectable;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataRef;
import yuzunyannn.elementalsorcery.util.helper.INBTSS;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;

public class DDQueue<T extends INBTSS> implements IDataDetectable<Integer, NBTTagList> {

	int persistentSize;
	List<T> queue;
	Supplier<T> creator;
	public Consumer<T> onElementAdd;

	public void setPersistentSize(int persistentSize) {
		this.persistentSize = persistentSize;
	}

	public void setQueue(List<T> queue, Supplier<T> creator) {
		this.queue = queue;
		this.creator = creator;
	}

	@Override
	public NBTTagList detectChanges(IDataRef<Integer> templateRef) {
		if (queue == null) return null;
		int size = templateRef.get(0);
		if (size < persistentSize) {
			NBTTagList list = new NBTTagList();
			int currSize = persistentSize;
			ListIterator<T> iter = queue.listIterator(queue.size());
			while (iter.hasPrevious()) {
				T obj = iter.previous();
				NBTSender sender = new NBTSender();
				obj.writeUpdateData(sender);
				list.appendTag(sender.tag());
				if (--currSize == size) break;
			}
			templateRef.set(persistentSize);
			return list;
		}
		return null;
	}

	@Override
	public void mergeChanges(NBTTagList list) {
		if (queue == null) return;
		for (int i = list.tagCount() - 1; i >= 0; i--) {
			NBTSender sender = new NBTSender(list.getCompoundTagAt(i));
			T obj = creator.get();
			obj.readUpdateData(sender);
			queue.add(obj);
			if (onElementAdd != null) onElementAdd.accept(obj);
		}
	}

}
