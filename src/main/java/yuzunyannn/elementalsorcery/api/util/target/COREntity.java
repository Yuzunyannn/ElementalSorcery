package yuzunyannn.elementalsorcery.api.util.target;

import java.lang.ref.WeakReference;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class COREntity extends CapabilityObjectRef {

	protected int id;

	protected WeakReference<Entity> ref;

	public COREntity(Entity entity) {
		id = entity.getEntityId();
		ref = new WeakReference(entity);
		worldId = entity.world.provider.getDimension();
	}

	@Override
	public void restore(World world) {
		ref = null;
		Entity entity = world.getEntityByID(id);
		if (entity == null) return;
		ref = new WeakReference(entity);
		worldId = entity.world.provider.getDimension();
	}

	@Override
	public boolean isValid() {
		if (_isValid()) return true;
		ref = null;
		return false;
	}

	private boolean _isValid() {
		Entity entity = toEntity();
		if (entity == null) return false;
		if (entity.isDead) return false;
		return true;
	}

	@Override
	public int tagId() {
		return TAG_ENTITY;
	}

	@Override
	public Entity toEntity() {
		if (ref == null) return null;
		return ref.get();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		Entity entity = toEntity();
		return entity == null ? false : entity.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		Entity entity = toEntity();
		return entity == null ? null : entity.getCapability(capability, facing);
	}

}
