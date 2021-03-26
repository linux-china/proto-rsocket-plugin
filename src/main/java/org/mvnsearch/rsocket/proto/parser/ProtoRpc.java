package org.mvnsearch.rsocket.proto.parser;

import java.util.HashSet;
import java.util.Set;

/**
 * Proto service RPC
 *
 * @author linux_china
 */
public class ProtoRpc {
    private String name;
    private String paramType;
    private boolean clientStreaming;
    private String returnType;
    private boolean serverStreaming;
    private Set<String> importClasses = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public boolean isClientStreaming() {
        return clientStreaming;
    }

    public void setClientStreaming(boolean clientStreaming) {
        this.clientStreaming = clientStreaming;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public boolean isServerStreaming() {
        return serverStreaming;
    }

    public void setServerStreaming(boolean serverStreaming) {
        this.serverStreaming = serverStreaming;
    }

    public void addImportClass(String importClass) {
        if (importClass.startsWith("google.protobuf.")) {
            importClass = "com." + importClass;
        }
        this.importClasses.add(importClass);
    }

    public Set<String> getImportClasses() {
        return importClasses;
    }
}
