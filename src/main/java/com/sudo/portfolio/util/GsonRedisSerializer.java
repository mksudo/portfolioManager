package com.sudo.portfolio.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import com.google.gson.reflect.TypeToken;

public class GsonRedisSerializer<T> implements RedisSerializer<T> {

    private static class InstantTypeConverter
            implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
        @Override
        public JsonElement serialize(Instant src, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            return Instant.ofEpochMilli(json.getAsLong());
        }
    }

    Gson gson;

    public GsonRedisSerializer() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeConverter())
                .create();
    }

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        return gson.toJson(o).getBytes();
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        Type genericType = new TypeToken<T>() {}.getType();
        return gson.fromJson(new String(bytes, StandardCharsets.UTF_8), genericType);
    }
}
