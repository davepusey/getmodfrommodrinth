package uk.davepusey.getmodfrommodrinth;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class Main 
{
    public static void main(String[] args)
    {
        Logger logger = LoggerFactory.getLogger(Main.class);
        
        if (args.length < 2)
        {
                logger.info("Usage: MC_VERSION MOD_SLUG ...");
        }
        else
        {
            String mcVersion = args[0];

            for (int i = 1; i < args.length; i++)
            {
                String modSlug = args[i];
                logger.info("Querying Modrinth API for " + modSlug);

                try
                {
                    URI url = new URI("https://api.modrinth.com/v2/project/" + modSlug
                    + "/version?loaders=[%22fabric%22]&game_versions=[%22" + mcVersion + "%22]");

                    String json = IOUtils.toString(url, StandardCharsets.UTF_8);
                    DocumentContext document = JsonPath.parse(json);

                    URL downloadUrl = new URI(document.read("$[0].files[0].url")).toURL();
                    String downloadFilename = document.read("$[0].files[0].filename");

                    logger.info("Downloading " + downloadFilename);

                    File downloadFile = new File("mods", downloadFilename);
                    FileUtils.copyURLToFile(downloadUrl, downloadFile);

                    logger.info("Done");
                }
                catch (IOException | URISyntaxException e)
                {
                    logger.error(e.toString());
                }
            }
        }
    }
}
