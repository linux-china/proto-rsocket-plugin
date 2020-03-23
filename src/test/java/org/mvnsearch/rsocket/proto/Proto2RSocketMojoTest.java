package org.mvnsearch.rsocket.proto;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Proto2RSocketMojo test
 *
 * @author linux_china
 */
public class Proto2RSocketMojoTest {
    private static Proto2RSocketMojo mojo = new Proto2RSocketMojo();

    @BeforeClass
    public static void setUp() {
        //MavenProject project = new MavenProject();
        mojo.output = "temp";
        mojo.source = "src/test/resources/proto";
    }

    @Test
    public void testGeneration() throws Exception {
        mojo.execute();
    }
}
