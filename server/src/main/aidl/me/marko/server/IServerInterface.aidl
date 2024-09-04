// IServerInterface.aidl
package me.marko.server;

// 需要手写import
import me.marko.server.PsersonInfo;

interface IServerInterface {

    String getName();

    void sendDataInfo(in PersonInfo info);
}