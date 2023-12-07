package org.myplaylist.myplaylist;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class MyplaylistApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyplaylistApplication.class, args);
    }

}
