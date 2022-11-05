package yuzunyannn.elementalsorcery.api.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;

public interface IHasMaster {
	
	@Nullable
	EntityLivingBase getMaster();

	boolean isOwnerless();

}
