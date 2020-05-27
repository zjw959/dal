package net;

import io.netty.channel.ChannelHandlerContext;
import net.kcp.KcpOnUdp;
import room.FightRoom;

public class Session {
    private boolean isKcp;
    private KcpOnUdp kcpOnUdp;
    private ChannelHandlerContext ctx;
    private FightRoom fightRoom;

    public boolean isKcp() {
        return isKcp;
    }

    public void setKcp(boolean isKcp) {
        this.isKcp = isKcp;
    }

    public KcpOnUdp getKcpOnUdp() {
        return kcpOnUdp;
    }

    public void setKcpOnUdp(KcpOnUdp kcpOnUdp) {
        this.kcpOnUdp = kcpOnUdp;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public FightRoom getFightRoom() {
        return fightRoom;
    }

    public void setFightRoom(FightRoom fightRoom) {
        this.fightRoom = fightRoom;
    }
}
