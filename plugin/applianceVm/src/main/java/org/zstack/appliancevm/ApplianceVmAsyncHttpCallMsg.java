package org.zstack.appliancevm;

import org.zstack.header.message.NeedReplyMessage;
import org.zstack.header.vm.VmInstanceMessage;
import org.zstack.utils.gson.JSONObjectUtil;

import java.util.concurrent.TimeUnit;

/**
 * Created by frank on 8/22/2015.
 */
public class ApplianceVmAsyncHttpCallMsg extends NeedReplyMessage implements VmInstanceMessage {
    private String vmInstanceUuid;
    private String path;
    private String command;
    private long commandTimeout = -1;
    private boolean checkStatus;
    private String commandClassName;

    public String getCommandClassName() {
        return commandClassName;
    }

    @Override
    public long getTimeout() {
        return getCommandTimeout() + TimeUnit.SECONDS.toMillis(30);
    }

    @Override
    public String getVmInstanceUuid() {
        return vmInstanceUuid;
    }

    public void setVmInstanceUuid(String vmInstanceUuid) {
        this.vmInstanceUuid = vmInstanceUuid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(Object cmd) {
        setCommandByString(JSONObjectUtil.toJsonString(cmd));
        commandClassName = cmd.getClass().getName();
    }

    public void setCommandByString(String command) {
        this.command = command;
    }

    public long getCommandTimeout() {
        assert commandTimeout != -1 : "commandTimeout is not set";
        return commandTimeout;
    }

    public void setCommandTimeout(long commandTimeout) {
        this.commandTimeout = commandTimeout;
    }

    public boolean isCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(boolean checkStatus) {
        this.checkStatus = checkStatus;
    }
}
