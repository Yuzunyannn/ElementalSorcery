package yuzunyannn.elementalsorcery.api.util.target;

import java.lang.ref.WeakReference;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class COREntity extends CapabilityObjectRef {

	public static class Storage implements ICapabilityRefStorage<COREntity> {
		@Override
		public void write(ByteBuf buf, COREntity obj) {
			buf.writeInt(obj.id);
		}

		@Override
		public COREntity read(ByteBuf buf) {
			return new COREntity(buf.readInt());
		}
	}

	protected int id;

	protected WeakReference<Entity> ref;

	public COREntity(Entity entity) {
		id = entity.getEntityId();
		ref = new WeakReference(entity);
		worldId = entity.world.provider.getDimension();
	}

	protected COREntity(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(CapabilityObjectRef other) {
		return id == ((COREntity) other).id;
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
	public boolean checkReference() {
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
	public IWorldObject toWorldObject() {
		Entity entity = toEntity();
		return entity == null ? null : IWorldObject.of(entity);
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

	@Override
	public Object toDisplayObject() {
		Entity entity = toEntity();
		if (entity == null) return "Entity Lost";
		return "Entity: " + entity.getName();
	}
}
