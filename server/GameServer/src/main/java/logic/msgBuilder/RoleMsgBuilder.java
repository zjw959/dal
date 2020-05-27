package logic.msgBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.constant.DiscreteDataID;
import logic.constant.DiscreteDataKey;
import logic.item.bean.DressItem;
import logic.item.bean.RoomItem;
import logic.role.RoleManager;
import logic.role.bean.Role;
import logic.support.MessageUtils;

import org.game.protobuf.s2c.S2CRoleMsg;
import org.game.protobuf.s2c.S2CRoleMsg.RoleInfo;
import org.game.protobuf.s2c.S2CRoleMsg.RoleInfoList;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;

/**
 * @author : DengYing
 * @CreateDate : 2017年8月15日 下午4:21:17
 * @Description ：Please describe this document
 */
public class RoleMsgBuilder {

    @SuppressWarnings("rawtypes")
    public static RoleInfo.Builder createRoleInfo(ChangeType type, Player player, Role role) {
        RoleManager roleManager = player.getRoleManager();
        BagManager bagManager = player.getBagManager();
        boolean isUseRole = roleManager.getNowRoleCid() == role.getCid();
        RoleInfo.Builder builder = RoleInfo.newBuilder();
        builder.setCt(type);
        builder.setId(String.valueOf(role.getCid()));
        builder.setCid(role.getCid());
        builder.setFavor(role.getFavor());
        builder.setMood(role.getMood());
        builder.setStatus(isUseRole ? 1 : 0);
        builder.addAllUnlockGift(role.getUnlockGift());
        builder.addAllUnlockHobby(role.getUnlockHobby());
        builder.setFavorCriticalPoint(role.isCriticalPoint());
        builder.setRoleState(role.getState().getState());
        // 装备的时装
        DressItem dressitem = (DressItem) bagManager.getItemCopy(role.getDressId());
        if (dressitem != null) {
            builder.setDress(ItemMsgBuilder.createDressInfo(type, dressitem));
        }
        // 房间
        if(role.getRoomId() == 0) {
            DiscreteDataCfgBean discreteDataCfgBean = GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.ROLE);
            Map data = discreteDataCfgBean.getData();
            int roomId = (int) data.get(DiscreteDataKey.DEFAULT_ROOM);
            builder.setRoomId(roomId);
        } else {
            RoomItem roomitem = (RoomItem) bagManager.getItemCopy(role.getRoomId());
            if (roomitem != null) {
                builder.setRoomId(roomitem.getTemplateId());
            }
        }
        //喜欢道具或者食物
        if(role.getFavoriteIds()!=null && role.getFavoriteIds().size()>0)
        {
            for(Integer likeId:role.getFavoriteIds()){
                builder.addFavoriteIds(likeId);
            }
        }
       
        return builder;
    }

    public static RoleInfoList.Builder createRoleInfoList(ChangeType type, Player player) {
        RoleInfoList.Builder builder = RoleInfoList.newBuilder();
        Collection<Role> roles = player.getRoleManager().getRoles().values();
        for (Role role : roles) {
            builder.addRoles(createRoleInfo(type, player, role));
        }
        return builder;
    }

    public static void sendRoleMsg(Player player, ChangeType type, Role... roles) {
        RoleInfoList.Builder builder = RoleInfoList.newBuilder();
        for (Role role : roles) {
            if (role == null) {
                continue;
            }
            builder.addRoles(createRoleInfo(type, player, role));
        }
        MessageUtils.send(player, builder);
    }

    /**
     * 更新精灵心情值
     */
    public static S2CRoleMsg.UpdateRoleMood.Builder createRoleMoodList(List<Role> roles) {
        S2CRoleMsg.UpdateRoleMood.Builder builder = S2CRoleMsg.UpdateRoleMood.newBuilder();
        roles.forEach(role -> {
            S2CRoleMsg.RoleMoodInfo.Builder subBuilder = S2CRoleMsg.RoleMoodInfo.newBuilder();
            subBuilder.setRoleId(String.valueOf(role.getCid())).setMood(role.getMood());
            builder.addInfos(subBuilder);
        });
        return builder;
    }


}
