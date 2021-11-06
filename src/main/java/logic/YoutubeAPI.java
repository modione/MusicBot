package logic;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class YoutubeAPI {
    // You need to set this value for your code to compile.
    // For example: ... DEVELOPER_KEY = "YOUR ACTUAL KEY";
    private static final String DEVELOPER_KEY = "AIzaSyAcb2x88CfAbEFxXpl8yIqcyuY0sfX16K0";

    private static final String APPLICATION_NAME = "my first project";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public static String getUploads(String ChannelName) throws GeneralSecurityException, IOException {
        YouTube youtubeService = getService();
        // Define and execute the API request
        YouTube.Channels.List request = youtubeService.channels()
                .list("snippet,contentDetails,statistics");
        ChannelListResponse response = request.setKey(DEVELOPER_KEY)
                .setForUsername(ChannelName)
                .setMaxResults(20L)
                .execute();
    return  (response.getItems().get(0).getContentDetails().getRelatedPlaylists().getUploads());
    }
    public static List<String> getVideos(String playlistName) throws GeneralSecurityException, IOException {
        YouTube youTube = getService();
        YouTube.PlaylistItems.List request = youTube.playlistItems().list("snippet,contentDetails");
        PlaylistItemListResponse response = request.setKey(DEVELOPER_KEY).setPlaylistId(playlistName).setMaxResults(30L).execute();
        List<String> stringsM = new ArrayList<>();
        for (PlaylistItem item : response.getItems()) {
            stringsM.add("youtube.com/watch?v="+item.getContentDetails().getVideoId());
        }
        return stringsM;
    }
    public static List<SearchResult> getVideosAsItem(String name) throws GeneralSecurityException, IOException {
        YouTube youTube = getService();
        YouTube.Search.List request = youTube.search().list("snippet");
        SearchListResponse response = request.setKey(DEVELOPER_KEY).setMaxResults(25L).setQ(name).setType("video").execute();
        return response.getItems();
    }
}