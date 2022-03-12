package yuzunyannn.elementalsorcery.item.tool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.item.IItemStronger;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.FireworkEffect;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemSoulKillerSword extends ItemSword implements IItemStronger {

	public ItemSoulKillerSword() {
		super(ToolMaterial.GOLD);
		this.setTranslationKey("soulKillerSword");
	}

	@Override
	public void onKillEntity(World world, ItemStack stack, EntityLivingBase deader, EntityLivingBase killer,
			DamageSource source) {
		if (world.isRemote) return;
		stack.setItemDamage(0);

		if (deader instanceof EntityCreature) {
			String key = EntityList.getEntityString(deader);
			if (key == null) return;
			NBTTagCompound map = stack.getOrCreateSubCompound("soulMap");
			map.setInteger(key, map.getInteger(key) + 1);

			NBTTagCompound effect = new NBTTagCompound();
			effect.setInteger("deader", deader.getEntityId());
			effect.setInteger("killer", killer.getEntityId());
			Effects.spawnEffect(world, Effects.ENTITY_SOUL, deader.getPosition(), effect);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		NBTTagCompound map = stack.getOrCreateSubCompound("soulMap");
		int count = 0;
		Set<String> removeSet = new HashSet<>();
		for (String key : map.getKeySet()) {
			count++;
			int c = map.getInteger(key) - 1;
			if (c <= 0) removeSet.add(key);
			else map.setInteger(key, c);
		}
		for (String key : removeSet) map.removeTag(key);

		if (count <= 0) return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);

		playerIn.heal(count * 0.5f);
		Vec3d pos = playerIn.getPositionVector();
		NBTTagCompound nbt = FireworkEffect.fastNBT(0, 1, 0.2f, new int[] { 0x3ad2f2, 0xff0000 },
				new int[] { 0xe0ffff });
		Effects.spawnEffect(worldIn, Effects.FIREWROK, pos.add(0, playerIn.height / 2, 0), nbt);

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity targetEntity) {
		if (player.world.isRemote) return false;
//		if (!targetEntity.canBeAttackedWithItem()) return false;
//		if (targetEntity.hitByEntity(player)) return false;

		String key = EntityList.getEntityString(targetEntity);
		if (key == null) return false;
		NBTTagCompound map = stack.getOrCreateSubCompound("soulMap");
		int count = map.getInteger(key);
		if (count <= 16) return false;

		float f = player.getCooledAttackStrength(0.5F);
		if (f != 1) return false;

		DamageSource ds = DamageSource.causeIndirectMagicDamage(player, player);
		float dmg = MathHelper.sqrt(count) - 4;

		Vec3d pos = targetEntity.getPositionVector().add(0, targetEntity.height / 2, 0);
		float size = MathHelper.clamp(count / 300, 1, 4);
		AxisAlignedBB AABB = WorldHelper.createAABB(pos, size, size, size);
		List<Entity> entities = player.world.getEntitiesWithinAABB(targetEntity.getClass(), AABB);
		for (Entity entity : entities) {
			if (entity == player) continue;
			Vec3d at = entity.getPositionVector().add(0, entity.height / 2, 0);
			float dmgRate = Math.min(1, 1 / (MathHelper.sqrt(pos.distanceTo(at) * 0.65f)));
			entity.attackEntityFrom(ds, dmgRate * dmg);
		}

		NBTTagCompound nbt = FireworkEffect.fastNBT(1, (int) size, 0.1f * size, new int[] { 0x3ad2f2 },
				new int[] { 0xe0ffff });
		Effects.spawnEffect(player.world, Effects.FIREWROK, pos.add(0, targetEntity.height / 2, 0), nbt);

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound map = stack.getOrCreateSubCompound("soulMap");
		for (String key : map.getKeySet()) {
			tooltip.add(TextFormatting.AQUA + I18n.format("entity." + key + ".name") + ":" + map.getInteger(key));
		}
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

}
