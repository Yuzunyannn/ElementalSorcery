package yuzunyannn.elementalsorcery.entity;

import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityVest extends Entity {

	public Function<EntityVest, Boolean> canLife;

	public EntityVest(World worldIn) {
		super(worldIn);
	}

	public EntityVest(World worldIn, Function<EntityVest, Boolean> canLife) {
		super(worldIn);
		this.canLife = canLife;
	}

	@Override
	public void onUpdate() {
		if (canLife != null && canLife.apply(this)) return;
		if (world.isRemote) return;
		this.setDead();
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
	}

}
