package mil.army.usace.hec.cumulus.client.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;
import mil.army.usace.hec.cumulus.client.model.Tag;
import org.junit.jupiter.api.Test;

class TestTagController extends TestController{

    @Test
    void testRetrieveAllTags() throws IOException {
        String resource = "cumulus/json/tags.json";
        launchMockServerWithResource(resource);

        List<Tag> tags = new TagController().retrieveAllTags(buildConnectionInfo());
        assertEquals(2, tags.size());
        Tag tag1 = tags.get(0);
        assertEquals("3fa85f64-5717-4562-b3fc-2c963f66afa6", tag1.getId());
        assertEquals("tag1", tag1.getName());
        assertEquals("test tag 1", tag1.getDescription());
        assertEquals("red", tag1.getColor());

        Tag tag2 = tags.get(1);
        assertEquals("4fa85f64-5717-4562-b3fc-2c963f66afa6", tag2.getId());
        assertEquals("tag2", tag2.getName());
        assertEquals("test tag 2", tag2.getDescription());
        assertEquals("blue", tag2.getColor());
    }

}
