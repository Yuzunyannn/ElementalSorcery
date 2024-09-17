package yuzunyannn.elementalsorcery.util.element;

import net.minecraft.tileentity.TileEntity;
import yuzunyannn.elementalsorcery.api.crafting.IDataSensitivity;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.ICastable;

public class ElementInventoryMonitor {

	long lastHash;

	public int checkChange(IElementInventory eInv) {
		long hash = eInv.contentHashCode();
		if (lastHash != hash) {
			long old = lastHash;
			lastHash = hash;
			if (hash == 0) return 0;
			if (old == 0) return 1;
			return 2;
		}
		return -1;
	}

	public static class TileDataSensitivity implements IDataSensitivity, ICastable {
		final TileEntity tile;

		public TileDataSensitivity(TileEntity tile) {
			this.tile = tile;
		}

		@Override
		public void markDirty() {
			tile.markDirty();
		}

		@Override
		public void applyUse() {
		}

		@Override
		public <T> T cast(Class<?> to) {
			if (to.isAssignableFrom(tile.getClass())) return (T) tile;
			return null;
		}
	}

	public static IDataSensitivity sensor(TileEntity tile) {
		return new TileDataSensitivity(tile);
	}

	public static IDataSensitivity sensor(TileEntity tile, Runnable updater) {
		return new TileDataSensitivity(tile) {
			@Override
			public void markDirty() {
				updater.run();
				tile.markDirty();
			}
		};

	}

}
