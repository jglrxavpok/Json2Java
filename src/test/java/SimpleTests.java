import org.jglrxavpok.json2java.types.NullElement;
import org.jglrxavpok.json2java.types.NumberElement;
import org.jglrxavpok.json2java.types.ObjectElement;
import org.jglrxavpok.json2java.types.UnionElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleTests {

    @Before
    public void init() {

    }

    @Test
    public void mergeWithDifferentProperties() {
        ObjectElement objA = new ObjectElement("a");
        ObjectElement objB = new ObjectElement("b");

        objA.getProperties().put("test", new NullElement());
        objB.getProperties().put("test2", new NullElement());

        ObjectElement result = objA.merge(objB);
        assertEquals(2, result.getProperties().size());
        assertTrue(result.getProperties().containsKey("test"));
        assertTrue(result.getProperties().containsKey("test2"));
    }

    @Test
    public void mergeWithSamePropertyType() {
        ObjectElement objA = new ObjectElement("a");
        ObjectElement objB = new ObjectElement("b");

        objA.getProperties().put("test", new NullElement());
        objB.getProperties().put("test", new NullElement());

        ObjectElement result = objA.merge(objB);
        assertEquals(1, result.getProperties().size());
        assertTrue(result.getProperties().containsKey("test"));
    }


    @Test
    public void mergeWithDifferentPropertyType() {
        ObjectElement objA = new ObjectElement("a");
        ObjectElement objB = new ObjectElement("b");

        objA.getProperties().put("test", new NullElement());
        objB.getProperties().put("test", new NumberElement(0));

        ObjectElement result = objA.merge(objB);
        assertEquals(1, result.getProperties().size());
        assertTrue(result.getProperties().containsKey("test"));
        assertTrue(result.getProperties().get("test") instanceof UnionElement);
    }

    @After
    public void cleanup() {

    }
}
