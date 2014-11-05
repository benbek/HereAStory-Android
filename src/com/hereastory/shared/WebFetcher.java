package com.hereastory.shared;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.hereastory.ui.OnResponseRetrieved;

import android.os.AsyncTask;

public class WebFetcher extends AsyncTask<String, Integer, String> {
	OnResponseRetrieved handler;

	public WebFetcher(OnResponseRetrieved handler) {
		this.handler = handler;
	}
	
	public static String getStringFromURL(String url) throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		HttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity httpEntity = httpResponse.getEntity();

		InputStream is = httpEntity.getContent();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;

		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();

		return sb.toString();
	}

	@Override
	protected String doInBackground(String... urls) {
		try {
			final String results = getStringFromURL(urls[0]);
			return results;
		} catch (IOException e) {
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(String response) {
		if (handler != null) {
			handler.onResponseRetrieved(response);
		}
	}
}
