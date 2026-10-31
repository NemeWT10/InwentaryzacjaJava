package pl.pollub.inwentaryzacja.client.serialization;

import pl.pollub.inwentaryzacja.client.model.Inventory;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.xml.XmlMapper;

public class InventorySerializer {

    private final ObjectMapper jsonMapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();
    private final ObjectMapper xmlMapper = XmlMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    public String toJson(Inventory inventory) {
        return jsonMapper.writeValueAsString(inventory);
    }

    public Inventory fromJson(String json) {
        return jsonMapper.readValue(json, Inventory.class);
    }

    public String toXml(Inventory inventory) {
        return xmlMapper.writeValueAsString(inventory);
    }

    public Inventory fromXml(String xml) {
        return xmlMapper.readValue(xml, Inventory.class);
    }
}
