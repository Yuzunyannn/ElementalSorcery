package yuzunyannn.elementalsorcery.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IAcceptMagic;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;
import yuzunyannn.elementalsorcery.tile.md.TileMDResonantIncubator;

public class EntityResonantCrystal extends EntityThrowable {

	public static class Factory implements IRenderFactory<EntityResonantCrystal> {

		@Override
		public Render<? super EntityResonantCrystal> createRenderFor(RenderManager manager) {
			return new RenderSnowball<EntityResonantCrystal>(manager, ESInitInstance.ITEMS.RESONANT_CRYSTAL,
					Minecraft.getMinecraft().getRenderItem());
		}

	}

	public EntityResonantCrystal(World worldIn) {
		super(worldIn);
	}

	public EntityResonantCrystal(World worldIn, EntityLivingBase throwerIn) {
		super(worldIn, throwerIn);
	}

	public EntityResonantCrystal(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		Vec3d v3d = this.getPositionVector();
		if (!world.isAirBlock(this.getPosition())) {
			v3d = v3d.addVector(0, 1, 0);
			if (!world.isAirBlock(new BlockPos(v3d))) v3d = v3d.addVector(0, 1, 0);
		}
		if (id == 3) {
			NBTTagCompound nbt = EntityParticleEffect.fastNBT(0, 2, 0.375f, new int[] { 0xde680a, 0x9a551d },
					new int[] { 0xfdc078 });
			EntityParticleEffect.spawnParticleEffect(world, v3d, nbt);
		} else if (id == 4) {
			NBTTagCompound nbt = EntityParticleEffect.fastNBT(0, 2, 0.375f,
					new int[] { 0xde680a, TileMDBase.PARTICLE_COLOR[0] }, TileMDBase.PARTICLE_COLOR_FADE);
			EntityParticleEffect.spawnParticleEffect(world, v3d, nbt);
		} else if (id == 5) {
			NBTTagCompound nbt = EntityParticleEffect.fastNBT(0, 2, 0.375f, new int[] { 0xde680a, 0x096b18 },
					new int[] { 0x5ac37b });
			EntityParticleEffect.spawnParticleEffect(world, v3d, nbt);
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		int effectState = 3;
		if (result.entityHit != null) {
			int i = 12;
			result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (float) i);
		} else sp: {
			BlockPos pos = result.getBlockPos();
			if (pos == null) break sp;
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() == ESInitInstance.BLOCKS.LIFE_FLOWER) {
				world.scheduleUpdate(pos, state.getBlock(), 0);
				effectState = 5;
			}
			TileEntity tile = world.getTileEntity(pos);
			if (tile == null) break sp;
			if (tile instanceof TileMDResonantIncubator)
				((TileMDResonantIncubator) tile).resonance(rand.nextFloat() * 100);
			else if (tile instanceof IAcceptMagic) {
				((IAcceptMagic) tile).accpetMagic(ElementStack.magic(80, 50), this.getPosition(), result.sideHit);
				effectState = 4;
			} else if (tile.hasCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, result.sideHit)) {
				tile.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, result.sideHit)
						.insertElement(ElementStack.magic(80, 50), false);
				effectState = 4;
			}
		}
		if (!this.world.isRemote) {
			this.world.setEntityState(this, (byte) effectState);
			this.setDead();
		}
	}

}
