package yuzunyannn.elementalsorcery.entity.fcube;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.elf.ElfTime;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class FCMAttack extends FairyCubeModule {

	@FairyCubeModuleRecipe
	public static boolean matchAndConsumeForCraft(World world, BlockPos pos, IElementInventory inv) {
		if (world.isRaining()) return false;
		Biome biome = world.getBiome(pos);
		ElfTime time = new ElfTime(world);
		if (biome.getRegistryName().getResourcePath().indexOf("desert") == -1) return false;
		if (!time.at(ElfTime.Period.DAY)) return false;

		return matchAndConsumeForCraft(world, pos, inv, ItemHelper.toList(Items.BLAZE_POWDER, 12),
				ElementHelper.toList(ESInit.ELEMENTS.FIRE, 150, 50));
	}

	public FCMAttack(EntityFairyCube fairyCube) {
		super(fairyCube);
		this.setPriority(PriorityType.EXECUTER);
		this.setStatusCount(2);
		this.setElementNeedPerExp(new ElementStack(ESInit.ELEMENTS.FIRE, 12, 75), 16);
	}

	public EntityLivingBase executeEntity;

	@Override
	public void onUpdate(EntityLivingBase master, IFairyCubeMaster fairyCubeMaster) {
		Behavior behaviorBase = fairyCubeMaster.getRecentBehavior(master);
		if (behaviorBase == null) return;
		if (master.isSneaking()) return;
		if (!behaviorBase.is("attack", "entity")) return;
		BehaviorAttack behavior = behaviorBase.to(BehaviorAttack.class);
		if (behavior == null) return;

		int status = this.getCurrStatus();
		executeEntity = behavior.getLivingTarget();
		if (executeEntity instanceof EntityFairyCube) {
			executeEntity = null;
			return;
		}
		if (executeEntity == null) return;

		fairyCube.setLookAt(executeEntity);
		fairyCube.doExecute(status == 2 ? 100 : 20);
	}

	public static float getPlunder(EntityFairyCube fairyCube) {
		float plunder = fairyCube.getAttribute("plunder");
		return plunder;
	}

	public List<EntityLivingBase> getTargets(EntityLivingBase master, Entity target, float range) {
		World world = target.world;
		AxisAlignedBB aabb = WorldHelper.createAABB(target.getPositionVector(), range, 4, 2);
		return world.getEntitiesWithinAABB(EntityLivingBase.class, aabb, e -> {
			if (e == master) return false;
			if (e == fairyCube) return false;
			if (EntityHelper.isSameTeam(master, e)) return false;
			if (target instanceof IMob) return e instanceof IMob;
			if (target instanceof EntityAnimal) return e instanceof EntityAnimal;
			return target.getClass() == e.getClass();
		});
	}

	@Override
	public void onStartExecute(EntityLivingBase master) {
		EntityLivingBase living = executeEntity;
		executeEntity = null;
		int status = this.getCurrStatus();

		int level = this.getLevelUsed();
		float damage = 1 + (float) Math.pow(level * 0.75, 1.2);

		if (status == 2) damage = damage * 3;
		DamageSource ds = DamageHelper.getMagicDamageSource(master, fairyCube);
		damage = fairyCube.getAttribute("attack:damage", damage);
		float range = fairyCube.getAttribute("attack:range");

		living.attackEntityFrom(ds, damage);
		if (range >= 1) {
			List<EntityLivingBase> targets = this.getTargets(master, living, range);
			for (EntityLivingBase target : targets) {
				double distance = Math.max(1, target.getDistance(living));
				damage = damage / (MathHelper.sqrt(distance) * 0.75f);
				target.attackEntityFrom(ds, damage);
			}
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("D", (byte) (status == 2 ? 100 : 20));
		nbt.setFloat("R", range);
		nbt.setInteger("E", living.getEntityId());
		this.sendToClient(nbt);
	}

	@Override
	public void onFailExecute() {
		executeEntity = null;
	}

	@SideOnly(Side.CLIENT)
	public void onRecv(NBTTagCompound nbt) {
		int[] colors = new int[] { 0xb5f4de, 0xea421c, 0xea861c, 0x5a1717 };
		fairyCube.doClientSwingArm(nbt.getInteger("D"), colors);
		Entity entity = fairyCube.world.getEntityByID(nbt.getInteger("E"));
		if (entity == null) return;
		float range = nbt.getFloat("R");
		if (range < 1) fairyCube.doClientEntityEffect(entity, colors);
		else {
			Vec3d center = entity.getPositionVector().addVector(0, entity.height / 3 * 2, 0);
			Random rand = fairyCube.getRNG();
			for (int i = 0; i < range * 20; i++) {
				EffectElementMove em = new EffectElementMove(entity.world, center);
				double v = range * 0.5;
				double d2 = rand.nextGaussian() * 0.2;
				double d0 = rand.nextGaussian() * v;
				double d1 = rand.nextGaussian() * v;
				em.setVelocity(d0, d2, d1);
				em.xDecay = em.zDecay = 0.5;
				em.yDecay = 0.85;
				em.setColor(colors[rand.nextInt(colors.length)]);
				Effect.addEffect(em);
			}
			EntityLivingBase master = fairyCube.getMaster();
			if (master == null) return;
			List<EntityLivingBase> targets = this.getTargets(master, entity, range);
			for (EntityLivingBase target : targets) fairyCube.doClientEntityEffect(target, colors);
		}
	}

	@Override
	public String getStatusUnlocalizedValue(int status) {
		if (status == 1) return "fairy.cube.attack.normal";
		if (status == 2) return "fairy.cube.attack.thump";
		return super.getStatusUnlocalizedValue(status);
	}

}
