package com.example.walkindoorrestapi.entities.DTO;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;

import java.io.IOException;

public class GeometrySerializer extends StdSerializer<Geometry> {

    public GeometrySerializer() {
        this(null);
    }

    public GeometrySerializer(Class<Geometry> t) {
        super(t);
    }

    @Override
    public void serialize(Geometry geometry, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        WKTWriter writer = new WKTWriter();
        jsonGenerator.writeString(writer.write(geometry));
    }
}

