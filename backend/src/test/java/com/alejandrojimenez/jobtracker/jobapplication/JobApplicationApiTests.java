package com.alejandrojimenez.jobtracker.jobapplication;

import com.alejandrojimenez.jobtracker.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Calls the API end to end against a real PostgreSQL started by Testcontainers (needs Docker).
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class JobApplicationApiTests {

    private static final String BASE = "/api/applications";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JobApplicationRepository repository;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        repository.deleteAll();
    }

    @Test
    void createsAnApplicationAndReturnsItsLocation() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("""
                        {"company": "  Globant ", "position": "Desarrollador Java Junior",
                         "jobUrl": "https://www.linkedin.com/jobs/view/123", "source": "LinkedIn", "notes": ""}
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(BASE + "/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.company").value("Globant"))
                .andExpect(jsonPath("$.status").value("SAVED"))
                .andExpect(jsonPath("$.notes").doesNotExist())
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void rejectsInvalidDataWithAMessagePerField() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("""
                        {"company": "", "position": "Backend", "jobUrl": "no-es-una-url"}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Datos no válidos"))
                .andExpect(jsonPath("$.errors.company").value("La empresa es obligatoria"))
                .andExpect(jsonPath("$.errors.jobUrl").value("El enlace de la oferta no es una URL válida"));
    }

    @Test
    void returns404ForAnUnknownApplication() throws Exception {
        mvc.perform(get(BASE + "/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No existe una postulación con id 999999"));
    }

    @Test
    void filtersByStatusAndText() throws Exception {
        create("Globant", "Desarrollador Java", "APPLIED");
        create("Rappi", "Backend Developer", "INTERVIEW");
        create("Bancolombia", "Desarrollador Java", "SAVED");

        mvc.perform(get(BASE).param("status", "APPLIED"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].company").value("Globant"));

        mvc.perform(get(BASE).param("q", "java"))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void updatesReplacesMovesAndDeletes() throws Exception {
        long id = create("Globant", "Desarrollador Java", "APPLIED");

        mvc.perform(put(BASE + "/" + id).contentType(MediaType.APPLICATION_JSON).content("""
                        {"company": "Globant", "position": "Desarrollador Java Semi Senior", "location": "Bogotá"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value("Desarrollador Java Semi Senior"))
                .andExpect(jsonPath("$.location").value("Bogotá"))
                // status was not sent, so it keeps the previous one
                .andExpect(jsonPath("$.status").value("APPLIED"));

        mvc.perform(patch(BASE + "/" + id + "/status").contentType(MediaType.APPLICATION_JSON).content("""
                        {"status": "OFFER"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OFFER"));

        mvc.perform(delete(BASE + "/" + id)).andExpect(status().isNoContent());
        mvc.perform(get(BASE + "/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void computesStats() throws Exception {
        create("A", "Dev", "SAVED");
        create("B", "Dev", "APPLIED");
        create("C", "Dev", "INTERVIEW");
        create("D", "Dev", "REJECTED");

        mvc.perform(get(BASE + "/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(4))
                .andExpect(jsonPath("$.sent").value(3))
                .andExpect(jsonPath("$.byStatus.OFFER").value(0))
                .andExpect(jsonPath("$.responseRate").value(2.0 / 3));
    }

    private long create(String company, String position, String status) throws Exception {
        MvcResult result = mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("""
                        {"company": "%s", "position": "%s", "status": "%s"}
                        """.formatted(company, position, status)))
                .andExpect(status().isCreated())
                .andReturn();
        String location = result.getResponse().getHeader("Location");
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }
}
