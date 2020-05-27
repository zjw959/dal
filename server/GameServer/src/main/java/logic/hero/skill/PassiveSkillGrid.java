package logic.hero.skill;

public class PassiveSkillGrid {
    private int id;
    private boolean isUnlock;
    private int skillId;

    public void create(int id) {
        this.id = id;
        this.isUnlock = false;
        this.skillId = 0;
    }

    public int getId() {
        return id;
    }

    public boolean isUnlock() {
        return isUnlock;
    }

    public void setUnlock(boolean isUnlock) {
        this.isUnlock = isUnlock;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }
}
