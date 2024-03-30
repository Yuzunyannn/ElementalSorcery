package yuzunyannn.elementalsorcery.item.prop;

import java.util.Random;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.item.IPlatformTickable;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemArrogantWool extends Item implements IPlatformTickable {

	public ItemArrogantWool() {
		this.setTranslationKey("arrogantWool");
	}

	@Override
	public boolean platformUpdate(World world, ItemStack stack, IWorldObject caster, NBTTagCompound runData,
			int tick) {
		if (world.isRemote) {
			if (tick % 10 == 0)
				randEffect(world, caster.getPosition(), 2, new int[] { 0xc19001, 0xecbb00, 0xfee624, 0xfff03c });
			return false;
		}
		if (tick % (20 * 10) != 0) return false;
		Random rand = world.rand;
		if (rand.nextDouble() > 0.05) return false;

		ItemStack gold = new ItemStack(Items.GOLD_INGOT);
		ItemHelper.dropItem(world, caster.getPosition().up(), gold);

		if (rand.nextDouble() <= 0.075) stack.shrink(1);

		return false;
	}

	@SideOnly(Side.CLIENT)
	public static void randEffect(World world, BlockPos center, float range, int[] colors) {
		Random rand = Effect.rand;
		Vec3d vec = new Vec3d(center).add(0.5 + rand.nextGaussian() * range, 0.5 + rand.nextFloat() * range / 2,
				0.5 + rand.nextGaussian() * range);
		EffectElementMove effect = new EffectElementMove(world, vec);
		effect.setColor(colors[rand.nextInt(colors.length)]);
		Vec3d speed = new Vec3d(center.getX() + 0.5, center.getY() + 1, center.getZ() + 0.5).subtract(vec);
		double dis = speed.length();
		speed = speed.normalize();
		effect.setVelocity(speed.scale(rand.nextDouble() * 0.07 + 0.05).scale(dis));
		effect.xDecay = effect.yDecay = effect.zDecay = 0.9;
		Effect.addEffect(effect);
	}

}
