package com.heroku.java;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication
@Controller
public class GettingStartedApplication {
    private final DataSource dataSource;

    @Autowired
    public GettingStartedApplication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) {
        SpringApplication.run(GettingStartedApplication.class, args);
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Show database contents
    @GetMapping("/database")
    public String database(Map<String, Object> model) {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();

            // Create table if it doesn't exist
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS ticks (tick timestamp, random_string varchar(255))"
            );

            // Insert a new row with a random string
            statement.executeUpdate(
                "INSERT INTO ticks VALUES (now(), '" + getRandomString() + "')"
            );

            ResultSet resultSet = statement.executeQuery("SELECT tick, random_string FROM ticks");
            List<String> output = new ArrayList<>();

            while (resultSet.next()) {
                output.add("Read from DB: " + resultSet.getTimestamp("tick") +
                           " / " + resultSet.getString("random_string"));
            }

            model.put("records", output);
            return "database";

        } catch (Throwable t) {
            model.put("message", t.getMessage());
            return "error";
        }
    }

    // Show HTML form to submit a string
    @GetMapping("/dbinput")
    public String dbInputForm() {
        return "dbinput"; // This refers to dbinput.html template
    }

    // Handle form submission
    @PostMapping("/dbinput")
    public String dbInputSubmit(@RequestParam String userInput) {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();

            // Create table if it doesn't exist
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS ticks (tick timestamp, random_string varchar(255))"
            );

            // Insert the user-provided string
            statement.executeUpdate(
                "INSERT INTO ticks VALUES (now(), '" + userInput + "')"
            );

        } catch (Throwable t) {
            System.out.println("Error inserting user input: " + t.getMessage());
        }

        // Redirect to /database to see the results
        return "redirect:/database";
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
}