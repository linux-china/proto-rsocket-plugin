package org.mvnsearch.rsocket.proto.parser;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Proto service RPC
 *
 * @author linux_china
 */
public class ProtoRpc {
    private String comment = "";
    private String name;
    private String paramType;
    private String paramName;
    private String paramComment = "";
    private boolean clientStreaming;
    private String returnType;
    private boolean serverStreaming;
    private Set<String> importClasses = new HashSet<>();

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

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

    public String getParamName() {
        if (paramName != null) return paramName;
        if (isClientStreaming()) {
            return StringUtils.uncapitalize(getParamType()) + "Flux";
        } else {
            return StringUtils.uncapitalize(getParamType());
        }
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamComment() {
        return paramComment;
    }

    public void setParamComment(String paramComment) {
        this.paramComment = paramComment;
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
