package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Разобрать страницу поиска с Озона в json с книгами
 */

public class Main {

    public static void main(String[] args) throws IOException {
        var content = Files.readString(Path.of("in.txt"));

        var booksStr = content.split("&quot;textAtom&quot;:\\{&quot;text&quot;:&quot;");
        var books = Arrays.stream(booksStr)
                .map(str -> str.split("&")[0])
                .map(str -> {
                    var titles = str.split(" \\| ");
                    return new Book(titles[0], titles.length > 1 ? titles[1] : "");
                })
                .collect(Collectors.toList());
        books.remove(0);

        var objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var json = objectWriter.writeValueAsString(books);

        Files.writeString(Path.of("out.txt"), json);
    }

    static class Book {
        String title;
        String authors;

        public Book(String title, String authors) {
            this.title = title;
            this.authors = authors;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthors() {
            return authors;
        }

        public void setAuthors(String authors) {
            this.authors = authors;
        }
    }
}