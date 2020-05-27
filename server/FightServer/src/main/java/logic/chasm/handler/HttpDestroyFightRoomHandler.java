package logic.chasm.handler;

import room.FightRoom;
import room.FightRoomManager;
import thread.BaseHandler;
import thread.FightRoomProcessor;
import thread.FightRoomProcessorManager;

public class HttpDestroyFightRoomHandler extends BaseHandler {
    private FightRoom room;

    public HttpDestroyFightRoomHandler(FightRoom room) {
        this.room = room;
    }
    
    @Override
    public void action() throws Exception {
        FightRoomManager.removeRoom(room);
        FightRoomProcessor roomProcessor = (FightRoomProcessor) FightRoomProcessorManager
                .getInstance().getRoomProcessor(room.getProcessorId());
        roomProcessor.removeRoom(room);
    }
}
