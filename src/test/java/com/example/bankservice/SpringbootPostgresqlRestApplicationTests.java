/*package com.example.springbootpostgresqlrest;

import com.example.springbootpostgresqlrest.repository.ContactRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // qick fix for solving autoincrement problem, but not a best solution
class SpringbootPostgresqlRestApplicationTests {

    private final String baseUrl = "http://localhost:8080/contacts/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ContactRepository repository;

    @AfterEach
    public void setUp() throws SQLException {
        SpringbootPostgresqlRestApplicationTests.resetAutoIncrementColumns(applicationContext, "contacts");
    }

    private static String getResetSqlTemplate(ApplicationContext applicationContext) {
        Environment environment = applicationContext.getBean(Environment.class);
        return environment.getRequiredProperty("test.reset.sql.template");
    }

    public static void resetAutoIncrementColumns(ApplicationContext applicationContext,
                                                 String... tableNames) throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        String resetSqlTemplate = getResetSqlTemplate(applicationContext);
        try (Connection dbConnection = dataSource.getConnection()) {
            for (String resetSqlArgument: tableNames) {
                try (Statement statement = dbConnection.createStatement()) {
                    String resetSql = String.format(resetSqlTemplate, resetSqlArgument);
                    statement.execute(resetSql);
                }
            }
        }
    }

    @Test
    void getEmptyList() throws Exception {

        mockMvc.perform(get(baseUrl)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]", true));
    }

    @Test
    void addDeleteAddObject() throws Exception {

        String requestBody = "{\"name\":\"Henry\"}";
        String response = "{\"name\":\"Henry\",\"id\":1}";

        MockHttpServletRequestBuilder request = post(baseUrl).contentType("application/json").content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(response, true));

        request = delete(baseUrl + "1").contentType("application/json");

        mockMvc.perform(request)
                .andExpect(status().isOk());

        request = post(baseUrl).contentType("application/json").content(requestBody);
        response = "{\"name\":\"Henry\",\"id\":2}";

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(response, true));

        request = delete(baseUrl + "2").contentType("application/json");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void updateObject() throws Exception {
        String requestBody = "{\"name\":\"Henry\"}";
        String response = "{\"name\":\"Henry\",\"id\":1}";

        MockHttpServletRequestBuilder request = post(baseUrl).contentType("application/json").content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(response, true));

        requestBody = "{\"name\":\"Miroslav\"}";
        response = "{\"name\":\"Miroslav\",\"id\":1}";

        request = put(baseUrl + "1").contentType("application/json").content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));

        request = delete(baseUrl + "1").contentType("application/json");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void getObjectList() throws Exception {

        String requestBody = "{\"name\":\"Henry\"}";
        String response = "{\"name\":\"Henry\",\"id\":1}";

        MockHttpServletRequestBuilder request = post(baseUrl).contentType("application/json").content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(response, true));

        requestBody = "{\"name\":\"Jezebel\"}";
        response = "{\"name\":\"Jezebel\",\"id\":2}";

        request = post(baseUrl).contentType("application/json").content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(response, true));

        requestBody = "{\"name\":\"Vladimir\"}";
        response = "{\"name\":\"Vladimir\",\"id\":3}";

        request = post(baseUrl).contentType("application/json").content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(response, true));

        request = delete(baseUrl + "1").contentType("application/json");

        mockMvc.perform(request)
                .andExpect(status().isOk());

        request = delete(baseUrl + "2").contentType("application/json");

        mockMvc.perform(request)
                .andExpect(status().isOk());

        request = delete(baseUrl + "3").contentType("application/json");

        mockMvc.perform(request)
                .andExpect(status().isOk());

    }
}*/
