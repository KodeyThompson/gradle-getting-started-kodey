package com.heroku.java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@SpringBootApplication
@Controller
public class GettingStartedApplication {
    private final DataSource dataSource;

    @Autowired
    public GettingStartedApplication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/database")
    public String database(Map<String, Object> model) {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            // Keep original table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
            statement.executeUpdate("INSERT INTO ticks VALUES (now())");

            ResultSet resultSet = statement.executeQuery("SELECT tick FROM ticks");
            List<String> output = new ArrayList<>();

            while (resultSet.next()) {
                // Add random string to output
                output.add("Read from DB: " + resultSet.getTimestamp("tick") +
                           " / " + getRandomString());
            }

            model.put("records", output);
            return "database";

        } catch (Throwable t) {
            model.put("message", t.getMessage());
            return "error";
        }
    }

    // Random string generator
    private String getRandomString() {
        int length = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SpringApplication.run(GettingStartedApplication.class, args);
    }
}