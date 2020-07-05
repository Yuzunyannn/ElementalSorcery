package yuzunyannn.elementalsorcery.entity.elf;

import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.elf.AutoName;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerInventory;

public class EntityElf extends EntityElfBase {

	protected IInventory inventory = new ItemStackHandlerInventory(16);

	public EntityElf(World worldIn) {
		super(worldIn);
		if (world.isRemote) return;
		if (this.rand.nextInt(5) == 0) this.setProfession(ElfProfession.SCHOLAR);
		else if (this.rand.nextInt(5) == 0) this.setProfession(ElfProfession.CRAZY);
		this.setCustomNameTag(AutoName.getRandomName());
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(4, new EntityAIMoveToLookBlock(this));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1));
		this.tasks.addTask(7, new EntityAILookBlock(this).setMaxDistance(4).setChance(0.5f));
		this.tasks.addTask(7, new EntityAIMoveToEntityItem(this).setMaxDistance(6));
		this.tasks.addTask(8, new EntityAIHarvestBlock(this));
		this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(10, new EntityAILookIdle(this));

		// this.setFlyMode(true);
	}

	@Override
	protected ItemStack pickupItem(ItemStack stack) {
		return ItemStack.EMPTY;
	}

	@Override
	public void tryHarvestBlock(BlockPos pos) {
		this.swingArm(EnumHand.MAIN_HAND);
		world.destroyBlock(pos, true);
	}
}
