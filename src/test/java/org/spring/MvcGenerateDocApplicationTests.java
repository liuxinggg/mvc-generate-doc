package org.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spring.config.CoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MvcGenerateDocApplicationTests {

    @Autowired
    CoreConfig coreConfig;

    @Test
    public void contextLoads() {
        System.out.println(coreConfig);
    }

}
