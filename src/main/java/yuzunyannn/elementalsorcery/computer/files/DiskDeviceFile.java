package yuzunyannn.elementalsorcery.computer.files;

import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceFile;
import yuzunyannn.elementalsorcery.api.util.NBTTag;

public class DiskDeviceFile implements IDeviceFile {

	protected static final int FLAG_NOEXISTENT = -1;
	protected static final int FLAG_ERROR_NODE = -2;
	protected static final int FLAG_BAD_NAME = -3;

	protected static final int FLAG_FILE = 1;
	protected static final int FLAG_DIRECT = 2;

	protected final NBTTagCompound root;
	protected final DeviceFilePath path;
	protected int rootIndex;
	protected boolean force;

	public DiskDeviceFile(DeviceFilePath path, IDisk disk) {
		this(path, disk.getContext(), 0);
	}

	public DiskDeviceFile(DeviceFilePath path, NBTTagCompound root, int rindex) {
		this.root = root;
		this.path = path;
		this.rootIndex = rindex;
	}

	@Override
	public boolean isDirectory() {
		refresh();
		return flag == FLAG_DIRECT;
	}

	@Override
	public boolean exists() {
		refresh();
		return flag > 0;
	}

	@Override
	public boolean delete() {
		if (path.length() <= 0) return false;
		if (!exists()) return false;
		rlcontext.removeTag(path.getName());
		return true;
	}

	@Override
	public DeviceFilePath getPath() {
		return path;
	}

	@Override
	public String getName() {
		return path.getName();
	}

	public boolean isCompliantName(String name) {
		if (name.charAt(0) == '*') return false;
		if (name.length() > 32) return false;
		return true;
	}

	@Override
	public Collection<IDeviceFile> list() {
		if (!isDirectory()) return null;
		LinkedList<IDeviceFile> children = new LinkedList<>();
		NBTTagCompound folder = path.length() > rootIndex ? rlcontext.getCompoundTag(path.getName()) : rlcontext;
		for (String key : folder.getKeySet()) {
			if (key.charAt(0) == '*') continue;
			children.add(new DiskDeviceFile(path.append(key), this.root, this.rootIndex));
		}
		return children;
	}

	@Override
	public IDeviceFile child(String key) {
		return new DiskDeviceFile(path.append(key), root, rootIndex);
	}

	protected int flag;
	protected NBTTagCompound rlcontext;

	protected NBTTagCompound mkdirs(int length) {
		rlcontext = this.root;
		length = Math.min(length, path.length());
		
		if (length <= rootIndex) {
			flag = FLAG_DIRECT;
			return rlcontext;
		}

		flag = FLAG_NOEXISTENT;

		for (int index = rootIndex; index < length; index++) {
			String p = path.get(index);

			if (p.isEmpty()) continue;

			if (!isCompliantName(p)) {
				flag = FLAG_BAD_NAME;
				return null;
			}

			if (!rlcontext.hasKey(p)) {
				NBTTagCompound next = new NBTTagCompound();
				next.setByte("*", (byte) 0);
				rlcontext.setTag(p, next);
				rlcontext = next;
				continue;
			}

			NBTBase tag = rlcontext.getTag(p);
			if (tag.getId() != NBTTag.TAG_COMPOUND) {
				flag = FLAG_ERROR_NODE;
				return null;
			}
			rlcontext = (NBTTagCompound) tag;

			boolean isDirect = rlcontext.hasKey("*");
			if (!isDirect) {
				flag = FLAG_ERROR_NODE;
				return null;
			}
		}

		return rlcontext;
	}

	protected void refresh() {
		rlcontext = root;
		int length = path.length();

		if (length <= rootIndex) {
			flag = FLAG_DIRECT;
			return;
		}

		flag = FLAG_NOEXISTENT;

		for (int index = rootIndex; index < length; index++) {
			String p = path.get(index);

			NBTTagCompound context;
			if (p.isEmpty()) {
				if (root == rlcontext) continue;
				context = rlcontext;
			} else {
				if (!rlcontext.hasKey(p)) {
					flag = FLAG_NOEXISTENT;
					break;
				}

				NBTBase tag = rlcontext.getTag(p);
				if (tag.getId() != NBTTag.TAG_COMPOUND) {
					flag = FLAG_ERROR_NODE;
					break;
				}

				context = (NBTTagCompound) tag;
			}

			boolean isDirect = context.hasKey("*");
			if (index < length - 1) {
				if (!isDirect) {
					flag = FLAG_ERROR_NODE;
					break;
				}
			} else {
				flag = isDirect ? FLAG_DIRECT : FLAG_FILE;
				break;
			}

			rlcontext = context;
		}
	}

	@Override
	public IDeviceStorage open() {
		NBTTagCompound nbt = mkdirs(path.length() - 1);
		if (nbt == null) return null;
		String name = path.getName();
		if (!isCompliantName(name)) return null;
		return new DiskDeviceStorage(this, nbt);
	}

	protected NBTTagCompound fmkdirs(int length) {
		rlcontext = root;
		length = Math.min(length, path.length());
		if (length <= 0) return rlcontext;

		for (int index = rootIndex; index < length; index++) {
			String p = path.get(index);

			if (p.isEmpty()) continue;

			if (!rlcontext.hasKey(p, NBTTag.TAG_COMPOUND)) {
				NBTTagCompound next = new NBTTagCompound();
				next.setByte("*", (byte) 0);
				rlcontext.setTag(p, next);
				rlcontext = next;
				continue;
			}

			rlcontext = rlcontext.getCompoundTag(p);
			rlcontext.setByte("*", (byte) 0);
		}

		return rlcontext;
	}

	public IDeviceStorage forceOpen() {
		return new DiskDeviceStorage(this, fmkdirs(path.length() - 1));
	}

	public void markDirty() {

	}
}
