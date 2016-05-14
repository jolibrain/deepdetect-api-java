package com.deepdetect.api.request;

import static com.deepdetect.api.TestUtils.CREATE_SERVICE_RESPONSE_FILE;
import static com.deepdetect.api.TestUtils.getResourceAsString;
import static com.deepdetect.api.enums.MLType.SUPERVISED;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.deepdetect.api.enums.MLType;
import com.deepdetect.api.exceptions.DeepDetectException;
import com.deepdetect.api.response.CreateServiceResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.mockwebserver.MockResponse;

public class CreateServiceRequestTest extends AbstractRequestTest {

	private static final String DESCRIPTION = "example classification service";
	private static final String ML_LIB = "caffe";
	private static final String SERVICE_NAME = "myserv";
	private static final MLType ML_TYPE = SUPERVISED;
	private static JsonObject model;
	private static JsonObject input;
	private static JsonObject output;

	@Before
	public void internalSetUp() {

		model = new JsonObject();
		model.addProperty("repository", "/home/me/models/example");

		input = new JsonObject();
		input.addProperty("connector", "csv");

		output = new JsonObject();
		output.addProperty("template", "mlp");
		output.addProperty("nclasses", 9);
		output.addProperty("activation", "prelu");

		JsonArray array = new JsonArray();
		array.add(512);
		array.add(512);
		array.add(512);

		output.add("layers", array);
	}

	@Override
	public void testRequestRequiresBaseURL() throws DeepDetectException {
		CreateServiceRequest.newCreateServiceRequest() //
				.build().process();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateServiceRequestRequiresServiceName() throws DeepDetectException {
		CreateServiceRequest.newCreateServiceRequest() //
				.baseURL(baseUrl) //
				.description(DESCRIPTION) //
				.mllib(ML_LIB) //
				.type(ML_TYPE) //
				.input(input) //
				.model(model) //
				.output(output) //
				.build().process();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateServiceRequestRequiresMlType() throws DeepDetectException {
		CreateServiceRequest.newCreateServiceRequest() //
				.baseURL(baseUrl) //
				.name(SERVICE_NAME) //
				.description(DESCRIPTION) //
				.mllib(ML_LIB) //
				.input(input) //
				.model(model) //
				.output(output) //
				.build().process();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateServiceRequestRequiresModel() throws DeepDetectException {
		CreateServiceRequest.newCreateServiceRequest() //
				.baseURL(baseUrl) //
				.name(SERVICE_NAME) //
				.description(DESCRIPTION) //
				.mllib(ML_LIB) //
				.type(ML_TYPE) //
				.input(input) //
				.output(output) //
				.build().process();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateServiceRequestRequiresInput() throws DeepDetectException {
		CreateServiceRequest.newCreateServiceRequest() //
				.baseURL(baseUrl) //
				.name(SERVICE_NAME) //
				.description(DESCRIPTION) //
				.mllib(ML_LIB) //
				.type(ML_TYPE) //
				.model(model) //
				.output(output) //
				.build().process();
	}

	@Test
	public void testCreateServiceRequestReturnsExpectedResult() throws DeepDetectException, IOException {
		server.enqueue(new MockResponse().setBody(getResourceAsString(CREATE_SERVICE_RESPONSE_FILE)));

		CreateServiceResponse response = CreateServiceRequest.newCreateServiceRequest() //
				.baseURL(baseUrl) //
				.name(SERVICE_NAME) //
				.description(DESCRIPTION) //
				.mllib(ML_LIB) //
				.type(ML_TYPE) //
				.input(input) //
				.model(model) //
				.output(output) //
				.build().process();

		assertThat(response.getStatus().getCode(), is(201));
		assertThat(response.getStatus().getMessage(), is("Created"));
	}

}
