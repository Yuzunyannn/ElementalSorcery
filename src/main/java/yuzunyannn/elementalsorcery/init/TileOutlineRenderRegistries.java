package yuzunyannn.elementalsorcery.init;

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import yuzunyannn.elementalsorcery.api.util.client.IRenderOutline;

public class TileOutlineRenderRegistries {

	public static final TileOutlineRenderRegistries instance = new TileOutlineRenderRegistries();

	private Map<Class<? extends TileEntity>, IRenderOutline<?>> map = new IdentityHashMap<>();

	public void register(Class<? extends TileEntity> tileClass, IRenderOutline<?> render) {
		if (map.containsKey(tileClass)) return;
		if (render == null) return;
		map.put(tileClass, render);
	}

	public IRenderOutline<?> getRenderOutline(Class<? extends TileEntity> tileClass) {
		return map.get(tileClass);
	}

	public IRenderOutline<?> getRenderOutline(TileEntity tile) {
		return map.get(tile.getClass());
	}

}
