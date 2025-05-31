package net.runelite.client.plugins.openrl.api.commons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class HttpUtil
{
	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);
	private static final Gson GSON = new GsonBuilder().create();
	private static final int connectTimeOut = 5000;
	private static final int readTimeOut = 5000;

	public static <T> T readJson(String url, Class<T> expectedType)
	{
		try
		{
			final URL httpUrl = new URL(url);
			final URLConnection conn = httpUrl.openConnection();

			conn.setConnectTimeout(connectTimeOut);
			conn.setReadTimeout(readTimeOut);

			try (InputStream stream = conn.getInputStream())
			{
				return GSON.fromJson(new String(stream.readAllBytes()), expectedType);
			}
			catch (IOException e)
			{
				log.error("Failed to read data", e);
				return null;
			}
		}
		catch (IOException e)
		{
			log.error("Failed to connect to url", e);
			return null;
		}
	}

	public static byte[] readBytes(String url)
	{
		try
		{
			final URL httpUrl = new URL(url);
			final URLConnection conn = httpUrl.openConnection();

			conn.setConnectTimeout(connectTimeOut);
			conn.setReadTimeout(readTimeOut);

			try (InputStream stream = conn.getInputStream())
			{
				return stream.readAllBytes();
			}
			catch (IOException e)
			{
				log.error("Failed to read data", e);
				return null;
			}
		}
		catch (IOException e)
		{
			log.error("Failed to connect to url", e);
			return null;
		}
	}
}