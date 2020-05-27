package logic.character;

/**
 * role character protobuff builder
 */
public class CharacterBuilder {

//    public static CharacterMsg.BaseCharacter.Builder builderPlayerBaseCharacterBuilder(
//            Player player) {
//        CharacterMsg.BaseCharacter.Builder builder = CharacterMsg.BaseCharacter.newBuilder();
//        builder.setRoleId(player.getRoleId());
//        builder.setRoleName(player.getRoleName());
//        builder.setLevel(player.getLevel());
//        builder.setVipLevel(player.getVipLevel());
//        builder.setExp(player.getExp());
//        builder.setCoin(player.getGold());
//        builder.setRmb(player.getRmb());
//        builder.setFightpower(player.getFightpower());
//
//        InfoManager info = player.getInfoManager();
//        builder.setStrength(info.getStrength());
//        builder.setArenaScore(info.getArenaScore());
//
//        List<AvatarPart> avatarParts = player.getAvatarManager().getAvatarBuilder();
//        builder.addAllAvatars(avatarParts);
//
//        builder.addAllAvatasSuits(player.getAvatarManager().getAvatarSuitSkills());
//        builder.addAllBattleSkills(player.getAvatarManager().getAvatarBattleSkills());
//
//        return builder;
//    }
//
//    public static CharacterMsg.RoleViewData.Builder builderRoleViewBuilder(
//            Player player) {
//        CharacterMsg.RoleViewData.Builder builder = CharacterMsg.RoleViewData.newBuilder();
//        builder.setRoleId(player.getRoleId());
//        builder.setRoleName(player.getRoleName());
//        builder.setLevel(player.getLevel());
//        builder.setVipLevel(player.getVipLevel());
//        builder.setFightpower(player.getFightpower());
//        builder.addAllBattleSkills(player.getAvatarManager().getAvatarBattleSkills());
//        List<HeroView> heroViews = player.getHeroManager().builderHeroViews();
//        builder.addAllHeroViews(heroViews);
//        builder.addAllHeroIds(player.getHeroManager().getHeroIds());
//        return builder;
//    }
}
