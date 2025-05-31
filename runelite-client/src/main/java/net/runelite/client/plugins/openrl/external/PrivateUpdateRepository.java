package net.runelite.client.plugins.openrl.external;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.pf4j.update.FileDownloader;
import org.pf4j.update.PluginInfo;
import org.pf4j.update.util.LenientDateTypeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class PrivateUpdateRepository extends OPRLUpdateRepository
{
	private static final String DEFAULT_PLUGINS_JSON_FILENAME = "plugins.json";
	private static final Logger log = LoggerFactory.getLogger(org.pf4j.update.DefaultUpdateRepository.class);

	@Getter
	private final String token;

	public PrivateUpdateRepository(String id, URL url, String token)
	{
		this(id, url, DEFAULT_PLUGINS_JSON_FILENAME, token);
	}

	public PrivateUpdateRepository(String id, URL url, String pluginsJsonFileName, String token)
	{
		super(id, url, pluginsJsonFileName);
		this.token = token;
	}

	public void initPlugins()
	{
		Reader pluginsJsonReader;
		try
		{
			final URL pluginsUrl = new URL(getUrl(), getPluginsJsonFileName());
			final OkHttpClient okHttpClient = new OkHttpClient();
			final Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer " + token)
				.url(pluginsUrl)
				.get()
				.build();

			log.debug("Read plugins of '{}' repository from '{}'", getId(), pluginsUrl);
			try (Response response = okHttpClient.newCall(request).execute())
			{
				pluginsJsonReader = new InputStreamReader(Objects.requireNonNull(response.body()).byteStream());
				final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new LenientDateTypeAdapter()).create();
				final PluginInfo[] items = gson.fromJson(pluginsJsonReader, PluginInfo[].class);
				plugins = new HashMap<>(items.length);
				for (PluginInfo p : items)
				{
					for (PluginInfo.PluginRelease r : p.releases)
					{
						try
						{
							r.url = new URL(getUrl(), r.url).toString();
							if (r.date.getTime() == 0)
							{
								log.warn("Illegal release date when parsing {}@{}, setting to epoch", p.id, r.version);
							}
						}
						catch (MalformedURLException e)
						{
							log.warn("Skipping release {} of plugin {} due to failure to build valid absolute URL. Url was {}{}", r.version, p.id, getUrl(), r.url);
						}
					}
					p.setRepositoryId(getId());
					plugins.put(p.id, p);
				}
				log.debug("Found {} plugins in repository '{}'", plugins.size(), getId());
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			plugins = Collections.emptyMap();
		}
	}

	@Override
	public FileDownloader getFileDownloader()
	{
		return new PrivateFileDownloader(token);
	}
}