package yuzunyannn.elementalsorcery.entity.elf;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class EntityAILookBlock extends EntityAIBase {

	public static interface IWatch {
		boolean wath(World world, BlockPos pos, IBlockState state);
	}

	final EntityCreature entity;
	private float chance = 0.05f;
	private int tryTimes = 4;
	private int width = 2;
	private int height = 4;
	private int lookTime;
	private BlockPos lookPos;
	private IWatch watch = (world, pos, state) -> {
		return state.getBlock() == ESInitInstance.BLOCKS.ELF_FRUIT;
	};

	public EntityAILookBlock(EntityCreature entity) {
		this.entity = entity;
		this.setMutexBits(2);
		this.setMaxDistance(1);
	}

	public EntityAILookBlock setChance(float chance) {
		this.chance = chance;
		return this;
	}

	public EntityAILookBlock setMaxDistance(int width) {
		this.width = width * 2;
		return this;
	}

	public EntityAILookBlock setTryTimes(int tryTimes) {
		this.tryTimes = tryTimes;
		return this;
	}

	public EntityAILookBlock setWatch(IWatch watch) {
		if (watch == null) return this;
		this.watch = watch;
		return this;
	}

	@Override
	public boolean shouldExecute() {
		if (this.entity.getRNG().nextFloat() >= this.chance) return false;
		return (this.lookPos = this.findLookBlock()) != null;
	}

	@Override
	public boolean shouldContinueExecuting() {
		if (this.lookPos == null) return false;
		BlockPos pos = this.lookPos;
		IBlockState state = entity.world.getBlockState(pos);
		if (!this.watch.wath(entity.world, pos, state)) return false;
		return this.lookTime > 0;
	}

	@Override
	public void startExecuting() {
		this.lookTime = 40 + this.entity.getRNG().nextInt(40);
	}

	public void updateTask() {
		--this.lookTime;
		this.entity.getLookHelper().setLookPosition(lookPos.getX(), lookPos.getY(), lookPos.getZ(),
				(float) this.entity.getHorizontalFaceSpeed(), (float) this.entity.getVerticalFaceSpeed());
	}

	protected BlockPos findLookBlock() {
		BlockPos origin = entity.getPosition();
		for (int t = 0; t < tryTimes; t++) {
			int x = (int) (width - entity.getRNG().nextFloat() * width * 2);
			int z = (int) (width - entity.getRNG().nextFloat() * width * 2);
			int y = (int) (height - entity.getRNG().nextFloat() * height * 2);
			BlockPos pos = origin.add(x, y, z);
			IBlockState state = entity.world.getBlockState(pos);
			if (this.watch.wath(entity.world, pos, state)) return pos;
		}
		return null;
	}

	public static BlockPos getLookPos(EntityCreature entity) {
		EntityLookHelper look = entity.getLookHelper();
		return new BlockPos(look.getLookPosX(), look.getLookPosY(), look.getLookPosZ());
	}

}
