package com.cowrycode.cassandradataloader.controllers;

import com.cowrycode.cassandradataloader.author.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dataloader")
public class LoadController {

    private  final  AuthorService authorService;

    public LoadController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/loadauthors")
    public ResponseEntity<String> loadAuthors(){
        boolean result = authorService.loadAuthorsData();
        return new ResponseEntity<>("Authors Loading process completed = " + result, HttpStatus.OK);
    }

    @GetMapping("loadbooks")
    public ResponseEntity<String> loadBooks(){
        boolean result = authorService.loadBookData();
        return new ResponseEntity<>("Books Loading process completed = " + result, HttpStatus.OK);
    }
}
