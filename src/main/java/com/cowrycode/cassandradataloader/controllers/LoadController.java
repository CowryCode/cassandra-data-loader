package com.cowrycode.cassandradataloader.controllers;

import com.cowrycode.cassandradataloader.author.Author;
import com.cowrycode.cassandradataloader.author.AuthorResository;
import com.cowrycode.cassandradataloader.author.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/test")
public class LoadController {

    private  final  AuthorService authorService;

    public LoadController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<String> load(){
        boolean result = authorService.loadData();
        return new ResponseEntity<>("Loading process success = " + result, HttpStatus.OK);
    }
}
