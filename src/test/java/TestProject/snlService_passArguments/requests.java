package TestProject.snlService_passArguments;

import java.io.BufferedReader;
import java.io.DataOutputStream;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class requests {
	HttpURLConnection con ;
	
	public static void main(String[] args) throws Exception {
		requests req=new requests("basicAuth" , "http://10.0.1.86/snl/rest/v2/board/new.json");
		req.sendGet();
	}
	
	

	public requests(String authType,String url) throws FileNotFoundException, IOException, OAuthSystemException, OAuthProblemException, ParseException
	{
		
		FileReader file=new FileReader("resources/data.json");
		
		JSONParser parser=new JSONParser();
		JSONObject jsonObject=(JSONObject) parser.parse(file);
		ReadProperty property=new ReadProperty();
		
		URL obj = new URL(url);
		 con = (HttpURLConnection) obj.openConnection();
		
		if(authType.equals("noAuth"))
		{
			System.out.println("dont need to provide any authentication");
			JSONObject authObj=(JSONObject) jsonObject.get("noAuth");
			String version=(String) authObj.get("version");
			System.out.println(version);
		}
		else if (authType.equals("basicAuth"))
		{
			JSONObject authObj=(JSONObject) jsonObject.get("basicAuth");
			String version=authObj.get("version").toString();
			String username=authObj.get("username").toString();
			System.out.println(username);
			String password=authObj.get("password").toString();
			System.out.println(password);
			String encoded = Base64.getEncoder().encodeToString((username+":"+password).getBytes(StandardCharsets.UTF_8));  //Java 8
			con.setRequestProperty("Authorization", "Basic "+encoded);
		}
		else if(authType.equals("oAuth2"))
		{
			JSONObject authObj=(JSONObject) jsonObject.get("oAuth2");
			String version=(String) authObj.get("version");
			String client_id=(String) authObj.get("client_id");
			String client_secret=(String) authObj.get("client_secret");
			String authurl=(String) authObj.get("authurl");
			String tokenurl=(String) authObj.get("tokenurl");
			
			OAuthClient client = new OAuthClient(new URLConnectionClient());

			OAuthClientRequest request = OAuthClientRequest.tokenLocation(tokenurl)
					.setGrantType(GrantType.CLIENT_CREDENTIALS).setClientId(client_id).setClientSecret(client_secret)
					.buildQueryMessage();

			 String token = client.accessToken(request, OAuthJSONAccessTokenResponse.class).getAccessToken();

			System.out.println(token);
		}
	}

	// HTTP GET request
	public String sendGet() throws Exception {

		/*URL obj = new URL(url);
		 con = (HttpURLConnection) obj.openConnection();*/

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		// con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		if (responseCode == 404) {
			return "not found";
		} else {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());

			return response.toString();
		}

	}

	// HTTP POST request

	public String sendPost(String url, String param) throws Exception {

		/*URL obj = new URL(url);
		 con = (HttpURLConnection) obj.openConnection();*/

		// add reuqest header
		con.setRequestMethod("POST");

		// Send post request
		con.setDoOutput(true);
		if (param == null) {

		} else {
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(param);
			wr.flush();
			wr.close();
		}

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + param);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();

	}

	public String putrequest(String url, String data) throws IOException {
		/*URL url1 = new URL(url);
		con = (HttpURLConnection) url1.openConnection();*/
		con.setDoOutput(true);
		con.setRequestMethod("PUT");

		if (data == null) {
			System.out.println("no data");
		} else {
			OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
			out.write(data);
			out.close();
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		con.getInputStream();
		return response.toString();
	}

	public String deleteRequest(String url) throws IOException {
		try {

			/*URL url1 = new URL(url);

			con = (HttpURLConnection) url1.openConnection();*/
			con.setDoOutput(true);
			con.setRequestMethod("DELETE");
			con.connect();
			if (con.getResponseCode() == 500) {
				return "not exist";
			}

			else {

				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}

				return response.toString();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}

}
