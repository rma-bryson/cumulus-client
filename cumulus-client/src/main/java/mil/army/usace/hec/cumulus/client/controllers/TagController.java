package mil.army.usace.hec.cumulus.client.controllers;

import java.io.IOException;
import java.util.List;
import mil.army.usace.hec.cumulus.client.model.CumulusObjectMapper;
import mil.army.usace.hec.cumulus.client.model.Tag;
import mil.army.usace.hec.cwms.http.client.ApiConnectionInfo;
import mil.army.usace.hec.cwms.http.client.HttpRequestBuilderImpl;
import mil.army.usace.hec.cwms.http.client.HttpRequestResponse;

public class TagController {

    private static final String TAGS_ENDPOINT = "tags";

    /**
     * Retrieve All Tags.
     *
     * @param apiConnectionInfo    - connection info
     * @return List of Tags
     * @throws IOException - thrown if retrieve failed
     */
    public List<Tag> retrieveAllTags(ApiConnectionInfo apiConnectionInfo) throws IOException {
        HttpRequestResponse response = new HttpRequestBuilderImpl(apiConnectionInfo, TAGS_ENDPOINT)
                .execute();
        return CumulusObjectMapper.mapJsonToListOfObjects(response.getBody(), Tag.class);
    }
}
