package logic.item;

public enum GoodTypes {
    /** 金币 */
    GOLD(500001, 1000),
    /** 钻石 */
    DIAMOND(500002, 1000),
    /** 友情点 */
    FRIEND_SHIP_POINT(500003, 1000),
    /** 体力 */
    STAME(500004, 100),
    /** 玩家经验 */
    EXP(500005, 1000000);
    
    private final int id;
    private final int number;

    GoodTypes(int id, int number) {
        this.id = id;
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public static String getItemContext(int id) {
        for (GoodTypes elemt : GoodTypes.values()) {
            if (elemt.id == id) {
                return elemt.getId() + " " + elemt.number;
            }
        }
        return null;
    }

    public static String[] getItemContext() {
        String[] str = new String[GoodTypes.values().length];
        int index = 0;
        for (GoodTypes elemt : GoodTypes.values()) {
            str[index] = elemt.getId() + " " + elemt.number;
            index++;
        }
        return str;
    }

    public static void main(String[] args) {
        System.err.println(GoodTypes.GOLD.getId() + " " + GoodTypes.GOLD.number);

    }
}
