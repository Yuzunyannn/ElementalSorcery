package yuzunyannn.elementalsorcery.explore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.item.crystal.ItemNatureCrystal;

public class ExploreManagement {

	public static final ExploreManagement instance = new ExploreManagement();

	private List<IExploreHandle> explores = new ArrayList<>();

	public void add(IExploreHandle handle) {
		explores.add(handle);
	}

	public boolean explore(NBTTagCompound data, World world, BlockPos pos, int level, @Nullable IBlockState state,
			 @Nullable EntityLivingBase portrait) {
		for (IExploreHandle handle : explores) if (!handle.explore(data, world, pos, level, state,portrait)) return false;
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, List<String> tooltip) {
		NBTTagCompound data = ItemNatureCrystal.getData(stack);
		if (data == null) return;
		if (!Explores.BASE.hasExplore(data)) return;
		for (IExploreHandle handle : explores) handle.addExploreInfo(data, tooltip);
	}

	static public void registerAll() throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = Explores.class.getFields();
		for (Field field : fields) ExploreManagement.instance.add((IExploreHandle) field.get(Explores.class));
	}

}
