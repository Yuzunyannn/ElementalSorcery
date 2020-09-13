package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.init.ESImplRegister;

public class ElfEdificeFloor extends IForgeRegistryEntry.Impl<ElfEdificeFloor> {

	public static final ESImplRegister<ElfEdificeFloor> REGISTRY = new ESImplRegister(ElfEdificeFloor.class);

	/**
	 * 首次生成的时候，可以生成的随机数据，之后的build全部按照该数据进行生成
	 * 
	 * @return 返回通过生成的数据，如果返回null，会自动为该层创建出一个nbt来
	 */
	public NBTTagCompound getBuildData(IBuilder builder, Random rand) {
		return null;
	}

	/** 获取本层的高度，最低为2，至少要可以站下人 */
	public int getFloorHeight(IBuilder builder) {
		return 3;
	}

	/**
	 * 建造一个建筑，可能是翻修的时候也要用到<br/>
	 * 该函数里禁止进行随机，需要随机的内容在{@link ElfEdificeFloor#getBuildData(IBuilder,
	 * Random)中都应该随机完成}
	 * 
	 * @param builder 建造器，可以在这里做到类似于world的操作
	 **/
	public void build(IBuilder builder) {

	}

	/**
	 * 给这个楼层一些玩家可以拿到的东西，会在成功建设之后进行一次调用
	 * 
	 * @param rand 作为奖励等随机的指数
	 * 
	 * 
	 */
	public void surprise(IBuilder builder, Random rand) {

	}

	/** 在本楼层生成实体或一些其他东西 */
	public void spawn(IBuilder builder) {

	}

}
