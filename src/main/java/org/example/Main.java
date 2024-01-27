package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;

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

        // Читаем файл от Озона
        var content = Files.readString(Path.of("in.txt"));

        // Создаем записи о книгах
        var bookStrings = content.split("&quot;textAtom&quot;:\\{&quot;text&quot;:&quot;");
        bookStrings = ArrayUtils.remove(bookStrings, 0);

        // Создаем коллекцию книг по записям
        var books = Arrays.stream(bookStrings)
                .map(str -> str.split("&")[0])
                .map(str -> {
                    var titles = str.split(" \\| ");
                    return new Book(titles[0], titles.length > 1 ? titles[1] : "");
                })
                .collect(Collectors.toList());

        // Проставляем обложки книг
        for (int i =0; i < books.size(); i++) {
            var imageName = bookStrings[i].split("\\.jpg")[0];
            var strArray = imageName.split("u002");
            imageName = strArray[strArray.length - 1];
            if ("Fcover".equals(imageName)) {
                imageName = bookStrings[i].split("\\.jpg")[1];
                strArray = imageName.split("u002");
                imageName = strArray[strArray.length - 1];
            }
            books.get(i).setCoverImageName(imageName.split("F")[1]);
        }

        // Готовим json-писатель
        var objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var json = objectWriter.writeValueAsString(books);

        // Пишем json файл
        Files.writeString(Path.of("out.json"), json);
    }

    static class Book {
        String title;
        String authors;
        String coverImageName;

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

        public String getCoverImageName() {
            return coverImageName;
        }

        public void setCoverImageName(String coverImageName) {
            this.coverImageName = coverImageName;
        }
    }
}