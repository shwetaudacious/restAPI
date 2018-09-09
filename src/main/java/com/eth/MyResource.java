package com.eth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Root resource (exposed at "myresource" path)
 */

@Path("v1")
public class MyResource {
    private static final String CALL_INFURA = "https://api.infura.io/v1/jsonrpc/mainnet/eth_getTransactionByHash?params=[%22";

	/**
     * Method handling HTTP GET requests.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("transaction/{id}")
	public String getIt(@PathParam("id") String id) {
		String output ="";
		String response="";
		String restURL="";
		try {
			restURL = CALL_INFURA+id+"%22]";
			URL url = new URL(restURL);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			System.out.println("restURL-- " + url);
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				response += output;
			}
			conn.disconnect();
			JSONParser parser = new JSONParser(); 
			JSONArray jsonArr = new JSONArray();
			JSONArray jsonArr1 = new JSONArray();
			JSONObject blockObj = new JSONObject();
			Map arr = new LinkedHashMap<String,String>();
			Map arr1 = new LinkedHashMap<String,String>();

			try {
			JSONObject json = (JSONObject) parser.parse(response);
			JSONObject resultBlock =(JSONObject) json.get("result");
			blockObj.put("blockHeight", resultBlock.get("blockNumber"));
			
			// out
			arr.put("address", resultBlock.get("to"));
			arr.put("value", resultBlock.get("value"));
			jsonArr.add(arr);
			blockObj.put("outs", jsonArr);
			
			// in
			arr1.put("address", resultBlock.get("from"));
			arr1.put("value", resultBlock.get("value"));
			jsonArr1.add(arr1);
			blockObj.put("ins", jsonArr1);
			
			blockObj.put("hash", id);
			
			blockObj.put("state", resultBlock.get("status"));
			
			if( resultBlock.get("contractAddress") == "" || resultBlock.get("contractAddress") == null)
			blockObj.put("depositType", "account");
			else blockObj.put("depositType", "contract");
			response = blockObj.toJSONString();

			} catch (ParseException e) {
				e.printStackTrace();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;

	}
}
