package logic.friend.bean;

import db.game.bean.PlayerDBBean;
import logic.character.bean.Player;
import logic.character.bean.Player.PlayerState;

/**
 * 角色view类
 */
public class FriendView {

    private int playerId;
    private String playerName;
    private int level;
    private long lastLoginTime;
    private boolean online;
    private int helpFightHeroCid;
    private int helpHeroFightPower;

    /**
     * 
     * @param roleBean
     * @param isView
     * @throws Exception
     */
    public FriendView(PlayerDBBean roleBean, boolean isView) throws Exception {
        Player _player = new Player(roleBean, isView);
        fromPlayer(_player);
    }

    private void fromPlayer(Player player) {
        if (player == null) {
            return;
        }
        setPlayerId(player.getPlayerId());
        setPlayerName(player.getPlayerName());
        setLevel(player.getLevel());
        setLastLoginTime(player.getLastLoginTime());
        setOnline(player.getState() == PlayerState.ONLINE);
        setHelpFightHeroCid(player.getHeroManager().getHelpFightHeroCid());
        setHelpHeroFightPower(player.getHeroManager().getHelpHeroFightPower());
    }

    public FriendView(Player player) {
        fromPlayer(player);
    }


    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }


    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }


    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getHelpFightHeroCid() {
        return helpFightHeroCid;
    }

    public void setHelpFightHeroCid(int helpFightHeroCid) {
        this.helpFightHeroCid = helpFightHeroCid;
    }

    public int getHelpHeroFightPower() {
        return helpHeroFightPower;
    }

    public void setHelpHeroFightPower(int helpHeroFightPower) {
        this.helpHeroFightPower = helpHeroFightPower;
    }

}
