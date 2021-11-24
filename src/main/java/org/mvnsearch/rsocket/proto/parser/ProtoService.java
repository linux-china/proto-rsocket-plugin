package org.mvnsearch.rsocket.proto.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * proto service
 *
 * @author linux_china
 */
public class ProtoService {
    private String name;
    private String comment;
    private List<ProtoRpc> rpcList = new ArrayList<>();

    public ProtoService(String name) {
        this.name = name;
    }

    public ProtoService(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public List<ProtoRpc> getRpcList() {
        return rpcList;
    }

    public void addRpc(ProtoRpc rpc) {
        this.rpcList.add(rpc);
    }

    public String toReactiveServiceDefinition(String packageValue) {
        String lineSeparator = System.lineSeparator();
        List<String> importantSentences = new ArrayList<>();
        importantSentences.add("import reactor.core.publisher.Flux;");
        importantSentences.add("import reactor.core.publisher.Mono;");
        Set<String> importedClasses = new HashSet<>();
        for (ProtoRpc protoRpc : rpcList) {
            importedClasses.addAll(protoRpc.getImportClasses());
        }
        for (String importedClass : importedClasses) {
            importantSentences.add("import " + importedClass + ";");
        }
        List<String> methods = new ArrayList<>();
        for (ProtoRpc protoRpc : rpcList) {
            StringBuilder builder = new StringBuilder();
            builder.append(javadocForRPC(protoRpc)).append("\r\n  ");
            if (protoRpc.isServerStreaming() || protoRpc.isClientStreaming()) {
                builder.append("Flux<" + protoRpc.getReturnType() + ">");
            } else {
                builder.append("Mono<" + protoRpc.getReturnType() + ">");
            }
            builder.append(" " + protoRpc.getName() + "(");
            if (protoRpc.isClientStreaming()) {
                builder.append("Flux<" + protoRpc.getParamType() + ">");
            } else {
                builder.append(protoRpc.getParamType());
            }
            builder.append(" " + protoRpc.getParamName() + ");");
            methods.add(builder.toString());
        }
        String serviceComment = "";
        if (this.comment != null && !this.comment.isEmpty()) {
            serviceComment = "/**" + lineSeparator + comment + lineSeparator + "*/" + lineSeparator;
        }
        return "package " + packageValue + ";"
                + lineSeparator + lineSeparator
                + String.join(lineSeparator, importantSentences)
                + lineSeparator + lineSeparator
                + serviceComment
                + "public interface " + name + " {"
                + lineSeparator + "  "
                + String.join(lineSeparator + "  ", methods)
                + lineSeparator
                + "}";
    }

    public String javadocForRPC(ProtoRpc protoRpc) {
        String javadoc;
        if (protoRpc.getComment().isEmpty()) {
            javadoc = "/** \r\n   * @param %s %s \r\n   */";
        } else {
            javadoc = "/** \r\n   * %s \r\n   * @param %s %s \r\n   */";
        }
        return String.format(javadoc, protoRpc.getComment(), protoRpc.getParamName(), protoRpc.getParamComment());
    }
}
