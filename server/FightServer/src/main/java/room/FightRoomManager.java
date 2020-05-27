package room;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import logic.chasm.bean.TeamInfo;
import logic.chasm.bean.TeamMember;

public class FightRoomManager {
    private static final Logger LOG = Logger.getLogger(FightRoomManager.class);

    /** roomId对应的Room **/
    private static ConcurrentHashMap<Long, FightRoom> roomIdRooms = new ConcurrentHashMap<>();

    /**
     * roleId对应的Room 只是用来保证同一战斗服务器不会产生两个角色id都在的情况 但不能保证分布式
     **/
    public static ConcurrentHashMap<Integer, FightRoom> roleIdRooms = new ConcurrentHashMap<>();

    public static FightRoom getRoomByRoomId(Long roomId) {
        return roomIdRooms.get(roomId);
    }

    public static FightRoom getRoomByRoleId(int playerId) {
        return roleIdRooms.get(playerId);
    }

    public static boolean checkRoleIsInFighting(int playerId) {
        boolean roleInRoom = roleIdRooms.containsKey(playerId);
        if (roleInRoom) {
            LOG.error("roleInRoom, roleId:" + playerId);
        }
        return roleInRoom;
    }

    /**
     * 根据玩家roleId 查找房间Id
     * 
     * @param roleId
     * @return
     */
    public static long getRoomIdByRoleId(int playerId) {
        long roomId = 0;
        if (roleIdRooms.containsKey(playerId)) {
            roomId = roleIdRooms.get(playerId).getId();
        }
        return roomId;
    }

    /** 当前房间总数 **/
    public static int getRoomSize() {
        return roomIdRooms.size();
    }

    public static int getRoleSize() {
        return roleIdRooms.size();
    }
    /**
     * 注册room
     * 
     * @param room
     */
    public static void register(FightRoom room) {
        roomIdRooms.put(room.getId(), room);
        TeamInfo teamInfo = room.getTeamInfo();
        Map<Integer, TeamMember> teamMembers = teamInfo.getMembers();
        for(Map.Entry<Integer, TeamMember> entry : teamMembers.entrySet()) {
            TeamMember teamMember = entry.getValue();
            roleIdRooms.put(teamMember.getPid(), room);
        }
    }

    public static void removeRoom(FightRoom room) {
        roomIdRooms.remove(room.getId());
        TeamInfo teamInfo = room.getTeamInfo();
        Map<Integer, TeamMember> teamMembers = teamInfo.getMembers();
        for(Map.Entry<Integer, TeamMember> entry : teamMembers.entrySet()) {
            TeamMember teamMember = entry.getValue();
            roleIdRooms.remove(teamMember.getPid());
        }
    }
    
    public static JSONArray getAllFightRoomForJson() {
        JSONArray array = new JSONArray();
        for (FightRoom room : roomIdRooms.values()) {
            JSONObject roomJson = new JSONObject();
            roomJson.put("id", room.getId());
            roomJson.put("status", room.getStatus().getStatus());
            roomJson.put("roomJson", room.getStatusTime());
            array.add(roomJson);
        }
        return array;
    }

}
