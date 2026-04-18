package com.example.security.until;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.bytebuddy.asm.Advice.Return;

public final class JsonUntil {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();
	public JsonUntil() {
		// TODO Auto-generated constructor stub
	}
	public static String toJon(Object object) {
		try {
			return OBJECT_MAPPER.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("khong the convert sang json",e);
		}
	}
	public static <T> T formJson(String json, Class<T> clazz){
		try {
			return OBJECT_MAPPER.readValue(json, clazz);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("khong the convert sang object", e);
		}
	}
}
