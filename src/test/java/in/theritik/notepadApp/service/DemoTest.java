package in.theritik.notepadApp.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DemoTest {

    @Disabled
    @Test
    public void add(){
        assertEquals(4, 2+2);
    }

    @ParameterizedTest
    @CsvSource({
        "1, 2, 3",
        "2, 3, 5",
        "3, 4, 9"
    })
    public void paramTest(int a, int b, int expected){
        assertEquals(expected, a+b);
    }
}
