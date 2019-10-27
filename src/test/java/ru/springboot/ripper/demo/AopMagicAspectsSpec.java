package ru.springboot.ripper.demo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.springboot.ripper.demo.AopMagicAspectsSpec.TestFrontendControllerConfiguraiton.ImportantLegacyService;
import ru.springboot.ripper.demo.AopMagicAspectsSpec.TestFrontendControllerConfiguraiton.ImportantService;
import ru.springboot.ripper.demo.AopMagicAspectsSpec.TestFrontendControllerConfiguraiton.LegacyService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringJUnitConfig
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = NONE,
        classes = AopMagicAspectsSpec.TestFrontendControllerConfiguraiton.class
)
public class AopMagicAspectsSpec {
    @Autowired ImportantService       importantService;
    @Autowired LegacyService          legacyService;
    @Autowired ImportantLegacyService importantLegacyService;

    @Test
    @DisplayName("Should print time when invoke method with @Benchmark annotation")
    void should_print_time() {
        //when
        importantService.verySeriousMethod();

        //then
        assertAll(
                () -> assertThat(outContent.toString()).contains("Call - serious method"),
                () -> assertThat(outContent.toString()).contains("*** verySeriousMethod ***"),
                () -> assertThat(outContent.toString()).contains("verySeriousMethod execution time — ")
        );
    }

    @Test
    @DisplayName("Should print warning message when call method with @Deprecated annotation")
    void should_print_warning() {
        //when
        legacyService.veryLegacyMethod();

        //then
        assertAll(
                () -> assertThat(outContent.toString()).contains("Call - legacy method"),
                () -> assertThat(outContent.toString()).contains("*** DEPRECATED ***")
        );
    }

    @Test
    @DisplayName("Should print warning message and execution time when call method with @Deprecated and @Benchmark annotation")
    void should_print_warning_and_print_time() {
        //when
        importantLegacyService.veryImportantLegacyMethod();

        //then
        assertAll(
                () -> assertThat(outContent.toString()).contains("Call - important legacy method"),
                () -> assertThat(outContent.toString()).contains("*** DEPRECATED ***"),
                () -> assertThat(outContent.toString()).contains("*** veryImportantLegacyMethod ***"),
                () -> assertThat(outContent.toString()).contains("veryImportantLegacyMethod execution time — ")
        );
    }

    @SpringBootApplication
    public static class TestFrontendControllerConfiguraiton {
        @Service
        public static class ImportantService {
            @Benchmark
            public void verySeriousMethod() {
                System.out.println("Call - serious method");
            }
        }

        @Service
        public static class LegacyService {
            @Deprecated
            public void veryLegacyMethod() {
                System.out.println("Call - legacy method");
            }
        }


        @Service
        public static class ImportantLegacyService {
            @Deprecated
            @Benchmark
            public void veryImportantLegacyMethod() {
                System.out.println("Call - important legacy method");
            }
        }
    }

    /**
     * For capture output
     */
    private final ByteArrayOutputStream outContent  = new ByteArrayOutputStream();
    private final PrintStream           originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

}
