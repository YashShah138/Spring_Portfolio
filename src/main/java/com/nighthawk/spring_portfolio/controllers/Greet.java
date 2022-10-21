package com.nighthawk.spring_portfolio.controllers;
/* MVC code that shows defining a simple Model, calling View, and this file serving as Controller
 * Web Content with Spring MVCSpring Example: https://spring.io/guides/gs/serving-web-con
 */

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.json.simple.parser.ParseException;

import org.springframework.ui.Model;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Controller  // HTTP requests are handled as a controller, using the @Controller annotation
public class Greet {

    // @GetMapping handles GET request for /greet, maps it to greeting() method
    @GetMapping("/greet")
    // @RequestParam handles variables binding to frontend, defaults, etc
    public String greeting(@RequestParam(name="name", required=true, defaultValue="World") String name,
    @RequestParam(name="grade", required=true, defaultValue="0") Integer grade, Model model) {

        // model attributes are visible to Thymeleaf when HTML is "pre-processed"
        model.addAttribute("name", name);
        model.addAttribute("grade", grade.toString());

        // load HTML VIEW (greet.html)
        return "greet";

    }

    // GET request, no parameters
    @GetMapping("/starters/covid19")
    public String TopSongs(Model model) throws IOException, InterruptedException, ParseException {
        //online link https://rapidapi.com/Glavier/api/genius-song-lyrics1/

        //rapid api setup:
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://genius-song-lyrics1.p.rapidapi.com/songs/chart?time_period=day&chart_genre=all&per_page=10&page=1"))
            .header("X-RapidAPI-Key", "80e73128e0mshda8c95123266391p176951jsnbc06ff234f92")
            .header("X-RapidAPI-Host", "genius-song-lyrics1.p.rapidapi.com")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        //alternative #1: convert response.body() to java hash map
        var song = new ObjectMapper().readValue(response.body(), HashMap.class);

        //alternative #2: convert response.body() to JSON object
        Object obj = new JSONParser().parse(response.body());
        JSONObject jsonSong = (JSONObject) obj;

        //pass stats to view
        model.addAttribute("song", song);
        model.addAttribute("jsonSong", jsonSong);

        return "controllers/Greet";
    }

}