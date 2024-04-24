package com.cowrycode.cassandradataloader.author;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class AuthorServiceImpl implements AuthorService {
    @Autowired
    AuthorResository authorResository;

    @Value("${datadump.location.author}")
    private String authorDumpLocation;

    @Value("${datadump.location.works}")
    private String worksDumpLocation;

    @Override
    public boolean loadData() {
        try {
            initAuthors();
        }catch (Exception e){
            return false;
        }
        return true;
    }

    private void initAuthors() throws IOException{
        Path path = Paths.get(authorDumpLocation);
        Stream<String> lines = Files.lines(path);
        lines.forEach(line ->{
           String jsonString =  line.substring(line.indexOf("{"));

            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                Author author = new Author();
                author.setName(jsonObject.optString("name"));
                author.setPersonalName(jsonObject.optString("personal_name"));
                author.setId(jsonObject.optString("key").replace("/author/",""));

                authorResository.save(author);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
    }
    private void initWorks(){

    }
}
