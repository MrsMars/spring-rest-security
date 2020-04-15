package com.aoher.controller;

import com.aoher.model.Book;
import com.aoher.repository.BookRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerRestTemplateTest {

    private static final Logger log = LoggerFactory.getLogger(BookControllerRestTemplateTest.class);
    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private BookRepository mockRepository;

    private Book book;

    @BeforeEach
    void init() {
        book = new Book(1L, "A Guide to the Bodhisattva Way of Life", "Santideva", new BigDecimal("15.41"));
        when(mockRepository.findById(book.getId())).thenReturn(Optional.of(book));
    }

    @Test
    void testFindOneSuccess() throws JSONException {
        String expected = MessageFormat.format(
                "'{'id:{0},name:\"{1}\",author:\"{2}\",price:{3}'}'",
                book.getId().intValue(), book.getName(), book.getAuthor(), book.getPrice().doubleValue()
        );
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("user", "password")
                .getForEntity("/books/" + book.getId().intValue(), String.class);

        printJSON(response);

        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    void testFindOne401() throws JSONException {
        String expected = String.format(
                "{\"status\":401,\"error\":\"Unauthorized\"" +
                ",\"message\":\"Unauthorized\"" +
                ",\"path\":\"/books/%d\"}", book.getId().intValue());

        ResponseEntity<String> response = restTemplate
                .getForEntity("/books/" + book.getId().intValue(), String.class);

        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    static void printJSON(Object object) {
        String result;
        try {
            result = om.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            log.info(result);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
