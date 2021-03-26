package org.mvnsearch.rsocket.proto.parser;

import org.apache.commons.lang3.StringUtils;

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
    private List<ProtoRpc> rpcList = new ArrayList<>();

    public ProtoService(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
            if (protoRpc.isServerStreaming() || protoRpc.isClientStreaming()) {
                builder.append("Flux<" + protoRpc.getReturnType() + ">");
            } else {
                builder.append("Mono<" + protoRpc.getReturnType() + ">");
            }
            builder.append(" " + protoRpc.getName() + " (");
            String paramName;
            if (protoRpc.isClientStreaming()) {
                builder.append("Flux<" + protoRpc.getParamType() + ">");
                paramName = StringUtils.uncapitalize(protoRpc.getParamType()) + "Flux";
            } else {
                builder.append(protoRpc.getParamType());
                paramName = StringUtils.uncapitalize(protoRpc.getParamType());
            }
            builder.append(" " + paramName + ");");
            methods.add(builder.toString());
        }
        return "package " + packageValue + ";"
                + lineSeparator + lineSeparator
                + String.join(lineSeparator, importantSentences)
                + lineSeparator + lineSeparator
                + "public interface " + name + " {"
                + lineSeparator + "  "
                + String.join(lineSeparator + "  ", methods)
                + lineSeparator
                + "}";
    }
}
