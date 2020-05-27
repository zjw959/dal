package thread;

import java.util.ArrayList;
import java.util.List;

import room.FightRoom;

public class FightRoomProcessor extends AbstractRoomProcessor<FightRoom> {
    public FightRoomProcessor(int lineId) {
        super(FightRoomProcessor.class.getSimpleName() + "_" + lineId, lineId);
    }

    /**
     * 核心方法 用于整个房间的逻辑驱动
     */
    @Override
    protected void tick() {
        List<FightRoom> roomList = new ArrayList<>(this.rooms.values());
        int i = 0;
        for(i = 0; i < roomList.size(); i++) {
            FightRoom room = roomList.get(i);
            if (!room.isDestroy()) {
                room.tick();
            }
        }
    }

    @Override
    public Long getId(FightRoom room) {
        return Long.valueOf(room.getId());
    }
}
