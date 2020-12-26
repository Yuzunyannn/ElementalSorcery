package yuzunyannn.elementalsorcery.entity.mob;

import java.util.Random;

public enum RelicZombieType {
	WARRIOR(0, 0xff0000, 1.5f),
	WIZARD(1, 0x0000ff, 8),
	PRIEST(2, 0x00ff00, 8);

	final int color;
	final int id;
	final float attackDistance;

	RelicZombieType(int id, int color, float attackDistance) {
		this.id = id;
		this.color = color;
		this.attackDistance = attackDistance;
	}

	public int getId() {
		return id;
	}

	public int getColor() {
		return color;
	}

	public float getAttackDistance() {
		return attackDistance;
	}

	static RelicZombieType getTypeFromId(int id) {
		for (RelicZombieType type : RelicZombieType.values()) {
			if (type.id == id) return type;
		}
		return WARRIOR;
	}

	static RelicZombieType randomType(Random rand) {
		RelicZombieType[] types = RelicZombieType.values();
		return types[rand.nextInt(types.length)];
	}
}
