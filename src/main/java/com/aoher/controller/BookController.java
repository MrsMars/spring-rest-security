package com.aoher.controller;

import com.aoher.error.BookNotFoundException;
import com.aoher.error.BookUnSupportedFieldPatchException;
import com.aoher.model.Book;
import com.aoher.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;

@RestController
@Validated
public class BookController {

    @Autowired
    private BookRepository repository;

    @GetMapping("/books")
    public List<Book> findAll() {
        return repository.findAll();
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public Book newBook(@Valid @RequestBody Book newBook) {
        return repository.save(newBook);
    }

    @GetMapping("/books/{id}")
    public Book findOne(@PathVariable @Min(1) Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @PutMapping("/books/{id}")
    public Book saveOrUpdate(@RequestBody Book newBook, @PathVariable Long id) {
        return repository.findById(id)
                .map(b -> {
                    b.setName(newBook.getName());
                    b.setAuthor(newBook.getAuthor());
                    b.setPrice(newBook.getPrice());
                    return repository.save(b);
                })
                .orElseGet(() -> {
                    newBook.setId(id);
                    return repository.save(newBook);
                });
    }

    @PatchMapping("/books/{id}")
    public Book patch(@RequestBody Map<String, String> update, @PathVariable Long id) {
        return repository.findById(id)
                .map(b -> {
                    String author = update.get("author");
                    if (StringUtils.isEmpty(author)) {
                        b.setAuthor(author);
                        return repository.save(b);
                    } else {
                        throw new BookUnSupportedFieldPatchException(update.keySet());
                    }
                })
                .orElseGet(() -> {
                    throw new BookNotFoundException(id);
                });
    }

    @DeleteMapping("/books/{id}")
    public void deleteBook(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
