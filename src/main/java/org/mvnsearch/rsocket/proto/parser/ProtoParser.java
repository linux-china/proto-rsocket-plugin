package org.mvnsearch.rsocket.proto.parser;


import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * proto file parse
 *
 * @author linux_china
 */
public class ProtoParser {
    private static final String seperator = "\\s*=\\s*";
    private String data;
    private List<String> lines;
    private int pos = 0;

    public static ProtoFile parseUtf8(File file) throws IOException {
        String content = FileUtils.fileRead(file);
        return new ProtoParser(content).parse();
    }

    public ProtoParser(String data) throws IOException {
        this.data = data;
        this.lines = IOUtils.readLines(new StringReader(data));
    }

    public ProtoFile parse() {
        ProtoFile protoFile = new ProtoFile();
        String scopeName = "";
        String previousLine = null;
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.startsWith("syntax")) {
                protoFile.setSyntax(clean(line.substring(line.indexOf("=") + 1)));
            } else if (line.startsWith("package")) {
                protoFile.setPackageValue(clean(line.substring(line.indexOf(" ") + 1)));
            } else if (line.startsWith("option")) {
                String pair = line.trim().substring(7);
                String[] parts = pair.split(seperator, 2);
                protoFile.addOption(clean(parts[0]), clean(parts[1]));
            } else if (line.startsWith("service")) {
                String serviceName = clean(line.split("\\s+")[1]);
                protoFile.getServices().put(serviceName, new ProtoService(serviceName, extractCommentFromPreviousLine(previousLine)));
                scopeName = serviceName;
            } else if (line.startsWith("rpc")) {
                ProtoRpc protoRpc = parseRpc(line, previousLine);
                protoFile.getServices().get(scopeName).addRpc(protoRpc);
            } else if (rawLine.startsWith("}") && line.equals("}")) {
                scopeName = "";
            }
            previousLine = line;
        }
        return protoFile;
    }

    public static ProtoRpc parseRpc(String line, String previousLine) {
        //rpc findById (google.protobuf.Int32Value) returns (Account);  // find user by id
        ProtoRpc protoRpc = new ProtoRpc();
        // trailing comments check
        if (line.contains("//")) {
            String rpcComment = line.substring(line.indexOf("//") + 2).trim();
            protoRpc.setComment(rpcComment);
            line = line.substring(0, line.indexOf("//"));
        }
        String temp = line.substring(line.indexOf(" ") + 1).trim();
        String name = temp.substring(0, temp.indexOf(" "));
        protoRpc.setName(name);
        temp = temp.substring(temp.indexOf(" ") + 1).trim();
        String paramDeclare = temp.substring(1, temp.indexOf(")", 2)).trim();
        String paramComment = null;
        if (paramDeclare.contains("/*")) {
            paramComment = paramDeclare.substring(paramDeclare.indexOf("/*") + 2, paramDeclare.indexOf("*/")).trim();
            paramDeclare = paramDeclare.substring(0, paramDeclare.indexOf("/*"));
        }
        //param
        String[] paramParts = paramDeclare.split("\\s+");
        String paramType;
        if (paramParts[0].equals("stream")) {
            protoRpc.setClientStreaming(true);
            paramType = paramParts[1];
        } else {
            paramType = paramParts[0];
        }
        if (paramType.contains(".")) {
            protoRpc.addImportClass(paramType);
            protoRpc.setParamType(paramType.substring(paramType.lastIndexOf(".") + 1));
        } else {
            protoRpc.setParamType(paramType);
        }
        //get param name from comment
        if (paramComment != null) {
            String[] parts = paramComment.split("\\s+", 2);
            protoRpc.setParamName(parts[0]);
            if (parts.length > 1) {
                protoRpc.setParamComment(parts[1]);
            }
        }
        temp = temp.substring(temp.indexOf("returns") + 7).trim();
        String returnDeclare = temp.substring(1, temp.indexOf(")", 2));
        //returns
        String[] returnParts = returnDeclare.split("\\s+");
        String returnType;
        if (returnParts[0].equals("stream")) {
            protoRpc.setServerStreaming(true);
            returnType = returnParts[1];
        } else {
            returnType = returnParts[0];
        }
        if (returnType.contains(".")) {
            protoRpc.addImportClass(returnType);
            protoRpc.setReturnType(returnType.substring(returnType.lastIndexOf(".") + 1));
        } else {
            protoRpc.setReturnType(returnType);
        }
        // Leading comment check: /*, /**, //
        protoRpc.setComment(extractCommentFromPreviousLine(previousLine));
        return protoRpc;
    }

    public String clean(String text) {
        return text.replaceAll("[\\s\";]*", "");
    }

    private static String extractCommentFromPreviousLine(String previousLine) {
        if (previousLine != null && (previousLine.contains("/*") || previousLine.contains("//"))) {
            String comment;
            if (previousLine.contains("/*")) {
                comment = previousLine.substring(previousLine.indexOf("/*") + 2, previousLine.indexOf("*/")).trim();
            } else { // "//" comment style
                comment = previousLine.substring(previousLine.indexOf("//") + 2);
            }
            if (comment.startsWith("*") || comment.startsWith("/")) {
                comment = comment.substring(1).trim();
            }
            return comment;
        }
        return "";
    }
}
