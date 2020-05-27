package logic.hero;

public interface ReqHeroOrder {
    /** 请求加入GM道具 */
    public static final int REQ_GM_ITEMS = 1;
    /** 请求加入升阶道具 */
    public static final int REQ_QUALITY_GM_ITEMS = 2;
    /** 请求合成英雄 */
    public static final int REQ_COMPOSE_HERO = 3;
    /** 请求进阶英雄 */
    public static final int REQ_UP_QUALITY_HERO = 4;
    /** 请求升级英雄 */
    public static final int REQ_UPGRADE_HERO = 5;
    /** 请求更换英雄皮肤 */
    public static final int REQ_CHANGE_HERO_SKIN = 6;
}
