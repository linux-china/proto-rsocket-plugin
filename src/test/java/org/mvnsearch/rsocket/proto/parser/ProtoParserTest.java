package org.mvnsearch.rsocket.proto.parser;

import org.junit.Test;

import java.io.File;

/**
 * Proto parser test
 *
 * @author linux_china
 */
public class ProtoParserTest {

    @Test
    public void testParse() throws Exception {
        File file = new File("src/test/resources/proto/account.proto");
        ProtoFile protoFile = ProtoParser.parseUtf8(file);
        for (ProtoService protoService : protoFile.getServices().values()) {
            System.out.println(protoService.toReactiveServiceDefinition(protoFile.getPackageValue()));
            System.out.println("====================================");
        }
    }

    @Test
    public void listAllProtoFiles() {
        File sourceDir = new File("src/test/resources/proto");
        File[] files = sourceDir.listFiles((dir, name) -> {
            return name.endsWith(".proto");
        });
        System.out.println(files);
    }

    @Test
    public void testRpc() {
        ProtoRpc protoRpc;
        String unaryRpc = "rpc findById (google.protobuf.Int32Value) returns (Account);";
        protoRpc = ProtoParser.parseRpc(unaryRpc,null);
        String serverStreaming = "rpc findById (google.protobuf.Int32Value) returns (stream Account);";
        protoRpc = ProtoParser.parseRpc(serverStreaming,null);
        String clientStreaming = "rpc findById (stream google.protobuf.Int32Value) returns (stream Account);";
        protoRpc = ProtoParser.parseRpc(clientStreaming,null);
        System.out.println(protoRpc);
    }

    @Test
    public void testReplace() {
        String packageName = "org.apache.demo";
        String dir = packageName.replaceAll("\\.", File.separator);
        System.out.println(dir);
    }
}
