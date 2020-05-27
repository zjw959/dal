package room;

import message.SMessageFactory;

public abstract class AbstractRoom {
    protected long id;
    protected long createTime = System.currentTimeMillis();
    protected int processorId;
    protected volatile boolean isDestroy;

    public abstract void tick();

    public long getId() {
        return this.id;
    }
    
    public long getCreateTime() {
        return this.createTime;
    }

    public int getProcessorId() {
        return processorId;
    }

    public boolean isDestroy() {
        return isDestroy;
    }
    
    public void setDestroy(boolean isDestroy) {
        this.isDestroy = isDestroy;
    }
    
    abstract public SMessageFactory getFactory();
}
