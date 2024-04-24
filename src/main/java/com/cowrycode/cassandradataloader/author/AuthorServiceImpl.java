package com.cowrycode.cassandradataloader.author;

import com.cowrycode.cassandradataloader.book.Book;
import com.cowrycode.cassandradataloader.book.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AuthorServiceImpl implements AuthorService {
    @Autowired
    AuthorResository authorResository;

    @Autowired
    BookRepository bookRepository;

    @Value("${datadump.location.author}")
    private String authorDumpLocation;

    @Value("${datadump.location.works}")
    private String worksDumpLocation;

    @Override
    public boolean loadAuthorsData() {
        try {
            initAuthors();
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public boolean loadBookData() {
        try {
            initWorks();
        }catch (Exception e){
            e.printStackTrace();
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
    private void initWorks() throws IOException {
        Path path = Paths.get(worksDumpLocation);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        Stream<String> lines = Files.lines(path);
        lines.forEach(line ->{
            String jsonString =  line.substring(line.indexOf("{"));
            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                Book book = new Book();
                book.setId(jsonObject.getString("key").replace("/works/",""));
                book.setName(jsonObject.optString("title"));
                JSONObject descriptionObj = jsonObject.optJSONObject("description");
                if(descriptionObj != null ){
                    book.setDescription(descriptionObj.optString("value"));
                }
                JSONObject publishedObj = jsonObject.optJSONObject("created");
                if(publishedObj != null){
                    String dateStr = publishedObj.getString("value");
                    book.setPublishDate(LocalDate.parse(dateStr, dateTimeFormatter));
                }
                JSONArray coversJSONArray = jsonObject.optJSONArray("covers");
                if(coversJSONArray != null){
                    List<String> coverIds  = new ArrayList<>();
                    for(int i=0; i < coversJSONArray.length(); i++){
                        coverIds.add(coversJSONArray.getString(i));
                    }
                    book.setCoverIds(coverIds);
                }

                JSONArray authorsJSONArray = jsonObject.optJSONArray("authors");
                if(authorsJSONArray != null && coversJSONArray != null){
                    List<String> authorIds = new ArrayList<>();
                    for(int i =0; i < coversJSONArray.length(); i++){
                       String authorId = authorsJSONArray.getJSONObject(i).getJSONObject("author").getString("key")
                                .replace("/authors/","");
                       authorIds.add(authorId);
                    }
                    book.setAuthorIds(authorIds);
                    List<String> authorNames =  authorIds.stream().map(id-> authorResository.findById(id))
                            .map(optionalAuthor -> {
                                if(!optionalAuthor.isPresent()) return "Unknown Author";
                                return optionalAuthor.get().getName();
                            }).collect(Collectors.toList());
                    book.setAuthorNames(authorNames);
                    System.out.println("Saving book : " + book.getName());
                    bookRepository.save(book);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
    }
}
