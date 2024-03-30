package yuzunyannn.elementalsorcery.api.util.target;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import yuzunyannn.elementalsorcery.api.IGetItemStack;

public abstract class CapabilityObjectRef implements ICapabilityProvider {

	public static final int TAG_INVALID = 0;
	public static final int TAG_PLAYER = 1;
	public static final int TAG_PLAYER_ITEM = 2;
	public static final int TAG_ENTITY = 3;
	public static final int TAG_ENTITY_ITEM = 4;
	public static final int TAG_TILE = 5;
	
	public static CapabilityObjectRef of() {
		return new CORInvalid();
	}

	public static CapabilityObjectRef of(EntityPlayer player, int slot) {
		return new CORPlayerItem(player, slot);
	}

	public static CapabilityObjectRef of(EntityItem itemEntity) {
		return new CORItemEntity(itemEntity);
	}

	public static <T extends Entity & IGetItemStack> CapabilityObjectRef of(T itemEntity) {
		return new CORItemEntity(itemEntity);
	}

	public static CapabilityObjectRef of(TileEntity tileEntity) {
		return new CORTile(tileEntity);
	}

	public static interface ICapabilityRefStorage {

	}

	public static final ICapabilityRefStorage[] storages;

	static {
		storages = new ICapabilityRefStorage[0];
	}

	protected int worldId;

	public abstract boolean isValid();

	public abstract int tagId();

	public abstract void restore(World world);

	@Nonnull
	public ItemStack toItemStack() {
		return ItemStack.EMPTY;
	}

	@Nullable
	public TileEntity toTileEntity() {
		return null;
	}

	@Nullable
	public Entity toEntity() {
		return null;
	}

	public EntityLivingBase toEntityLiving() {
		Entity entity = toEntity();
		if (entity instanceof EntityLivingBase) return (EntityLivingBase) entity;
		return null;
	}

	public EntityPlayer toEntityPlayer() {
		Entity entity = toEntity();
		if (entity instanceof EntityPlayer) return (EntityPlayer) entity;
		return null;
	}

}
