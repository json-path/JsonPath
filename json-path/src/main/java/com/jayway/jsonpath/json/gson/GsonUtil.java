package com.jayway.jsonpath.json.gson;

import com.google.gson.JsonNull;

import com.jayway.jsonpath.json.JsonArray;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.JsonFactory;
import com.jayway.jsonpath.json.JsonObject;
import com.jayway.jsonpath.json.ParentReference;

public class GsonUtil {

	private static final GsonJsonFactory CREATE_JSON_ARRAY = new GsonJsonFactory();

	public static JsonElement convertUp(com.google.gson.JsonElement object,
			ParentReference parent) throws JsonException {
		JsonElement je = convertUp(object);
		je.setParentReference(parent);

		return je;

	}

	public static JsonElement convertUp(com.google.gson.JsonElement object)
			throws JsonException {
		if (object.isJsonArray())
			return new GsonJsonArray((com.google.gson.JsonArray) object);
		else if (object.isJsonObject())
			return new GsonJsonObject((com.google.gson.JsonObject) object);
		else if (object.isJsonPrimitive())
			return new GsonJsonPrimitive(object.getAsJsonPrimitive());
		else
			return new GsonJsonPrimitive(null);
	}

	public static GsonJsonArray removeGsonArrayElement(int index,
			GsonJsonArray jsonArray) throws JsonException {
		return setGsonArrayElement(index, null, jsonArray);
	}

	public static GsonJsonArray setGsonArrayElement(int index,
			JsonElement payload, GsonJsonArray jsonArray) throws JsonException {

		JsonElement arrayParent = jsonArray.getParentReference().getParent();
		if (arrayParent == null) {
			return modifyArray(index, payload, jsonArray);
		}
		if (!arrayParent.isContainer())
			throw new JsonException(); // WTF?!

		GsonJsonArray ja = modifyArray(index, payload, jsonArray);

		if (arrayParent.isJsonArray()) {
			int i = ja.getParentReference().getIndex();
			return setGsonArrayElement(i, ja, (GsonJsonArray) arrayParent
					.toJsonArray());
		} else { // it's an object
			JsonObject obj = arrayParent.toJsonObject();
			obj.put(jsonArray.getParentReference().getField(), ja);

			return ja;
		}

	}

	private static GsonJsonArray modifyArray(int index, JsonElement payload,
			GsonJsonArray jsonArray) {
		GsonJsonArray out = CREATE_JSON_ARRAY.createJsonArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			if (index == i) {
				if (payload != null)
					out.add(payload);
			} else {
				out.add(jsonArray.get(i));
			}
		}
		out.setParentReference(jsonArray.getParentReference());
		return out;
	}

}
