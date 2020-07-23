import org.jglrxavpok.json2java.types.NullElement;
import org.jglrxavpok.json2java.types.ObjectElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleTests {

    @BeforeEach
    public void init() {

    }

    @Test
    public void mergeWithDifferentProperties() {
        ObjectElement objA = new ObjectElement();
        ObjectElement objB = new ObjectElement();

        objA.getProperties().put("test", new NullElement());
        objB.getProperties().put("test2", new NullElement());

        ObjectElement result = objA.merge(objB);
        assertEquals(2, result.getProperties().size());
        assertTrue(result.getProperties().containsKey("test"));
        assertTrue(result.getProperties().containsKey("test2"));
    }

    @AfterEach
    public void cleanup() {

    }
}
