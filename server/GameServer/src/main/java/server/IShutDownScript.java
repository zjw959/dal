package server;

import script.IScript;

public interface IShutDownScript extends IScript {
    public void rest();

    public void stop();
}
