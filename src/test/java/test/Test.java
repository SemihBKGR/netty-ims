package test;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Properties;

public class Test {

    @org.junit.jupiter.api.Test
    public void asD(){
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29093");
        Admin admin = Admin.create(properties);
        var r = admin.createTopics(Collections.singleton(new NewTopic("asd", 1, (short) 1)));
    }

}
