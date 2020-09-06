package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;

public abstract class ContainerElf extends Container {
	public final BlockPos pos;
	/** 交互的玩家 */
	public final EntityPlayer player;
	/** 交互的精灵，存在null的可能 */
	public EntityElfBase elf;
	/** 是否关闭 */
	protected boolean noEnd = true;

	public ContainerElf(EntityPlayer player) {
		this.player = player;
		NBTTagCompound nbt = ElementalSorcery.getPlayerData(player);
		Entity elf = null;
		if (player.world.isRemote) elf = null;
		else elf = (EntityElfBase) player.world.getEntityByID(nbt.getInteger("elfId"));
		nbt.removeTag("elfId");
		if (elf instanceof EntityElfBase) this.elf = (EntityElfBase) elf;
		this.pos = this.elf == null ? player.getPosition() : elf.getPosition();
		if (this.elf != null) this.elf.setTalker(player);
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		if (this.elf != null) this.elf.setTalker(null);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		boolean flag;
		if (elf == null) flag = playerIn.getDistanceSq(pos) <= 64;
		else flag = playerIn.getDistanceSq(this.elf) <= 64 && !elf.isDead;
		return noEnd && flag;
	}

	/** 更换ui */
	public void changeUI(int modGuiId) {
		noEnd = false;
		if (elf != null) {
			NBTTagCompound nbt = ElementalSorcery.getPlayerData(player);
			nbt.setInteger("elfId", elf.getEntityId());
		}
		BlockPos pos = player.getPosition();
		player.openGui(ElementalSorcery.instance, modGuiId, player.world, pos.getX(), pos.getY(), pos.getZ());
	}

}
