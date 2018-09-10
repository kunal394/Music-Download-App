package com.ks.musicdownloader.songsprocessors.bandcamp;

import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.SongInfo;
import com.ks.musicdownloader.Utils.RegexUtils;
import com.ks.musicdownloader.songsprocessors.BaseParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class BandcampParser extends BaseParser {

    private static final String TAG = BandcampParser.class.getSimpleName();

    private ArtistInfo artistInfo;
    private int songsCount;

    public BandcampParser(String url, DownloadCallback downloadCallback) {
        super(url, downloadCallback);
        songsCount = 0;
    }

    @Override
    public ArtistInfo parseArtistInfo() throws IOException {
        Log.d(TAG, "parseArtistInfo()");
        artistInfo = new ArtistInfo();
        Document document = fetchDocumentFromUrl(getUrl());
        if (isArtistUrl()) {
            Log.d(TAG, "parseArtistInfo(): artist url found!");
            if (!setArtistNameForArtistUrl(document)) {
                Log.d(TAG, "parseArtistInfo() could not set artist name!");
                return Constants.DUMMY_ARTIST_INFO;
            }
            handleArtist(document);
        } else if (isAlbumUrl()) {
            Log.d(TAG, "parseArtistInfo() album url found!");
            if (!setArtistNameForTrackOrAlbum(document)) {
                Log.d(TAG, "parseArtistInfo() could not set artist name!");
                return Constants.DUMMY_ARTIST_INFO;
            }
            handleAlbum(document);
        } else if (isTrackUrl()) {
            Log.d(TAG, "parseArtistInfo() track url found!");
            if (!setArtistNameForTrackOrAlbum(document)) {
                Log.d(TAG, "parseArtistInfo() could not set artist name!");
                return Constants.DUMMY_ARTIST_INFO;
            }
            handleTrack(document);
        } else {
            // should never happen
            Log.d(TAG, "Unknown type url: " + getUrl());
            return Constants.DUMMY_ARTIST_INFO;
        }
        return artistInfo;
    }

    private boolean setArtistNameForArtistUrl(Document document) {
        String artistName = document.select(Constants.BANDCAMP_ARTIST_SELECTOR_FOR_ARTIST).text();
        if (Constants.EMPTY_STRING.equals(artistName)) {
            return false;
        }
        artistInfo.setArtist(artistName);
        return true;
    }

    private boolean setArtistNameForTrackOrAlbum(Document document) {
        String artistName = document.select(Constants.BANDCAMP_ARTIST_SELECTOR_FOR_ALBUM_AND_TRACK).text();
        if (Constants.EMPTY_STRING.equals(artistName)) {
            return false;
        }
        artistInfo.setArtist(artistName);
        return true;
    }

    private boolean isArtistUrl() {
        return RegexUtils.isRegexMatching(Constants.BANDCAMP_ARTIST_URL_REGEX, getUrl());
    }

    private boolean isAlbumUrl() {
        return RegexUtils.isRegexMatching(Constants.BANDCAMP_ALBUM_URL_REGEX, getUrl());
    }

    private boolean isTrackUrl() {
        return RegexUtils.isRegexMatching(Constants.BANDCAMP_TRACK_URL_REGEX, getUrl());
    }

    // get the list of album urls and call handleTrackOrAlbum with the document of every url
    private void handleArtist(Document document) throws IOException {
        Log.d(TAG, "handleArtist() start");
        String baseUrl = getBaseUrlForArtist();
        // get the list of album urls and call handleTrackOrAlbum with the document of every url
        Elements albumElements = document.select(Constants.BANDCAMP_ALBUM_LIST_SELECTOR).select("a");
        for (Element albumElement : albumElements) {
            String albumUrl = albumElement.attr("href");
            Document albumDocument = fetchDocumentFromUrl(baseUrl + albumUrl);
            Log.d(TAG, "handleArtist() calling handle album for album url: " + albumUrl);
            handleAlbum(albumDocument);
        }
    }

    private void handleAlbum(Document document) throws IOException {
        Log.d(TAG, "handleAlbum() start");
        String trackInfo = getTrackInfoForAlbum(document);
        if (Constants.EMPTY_STRING.equals(trackInfo)) {
            Log.d(TAG, "handleAlbum() no trackInfo found!");
            artistInfo = Constants.DUMMY_ARTIST_INFO;
            return;
        }
        trackInfo = "{" + trackInfo.replaceFirst(Constants.BANDCAMP_TRACK_INFO_KEY, "\"" + Constants.BANDCAMP_TRACK_INFO_KEY + "\"") + "}";
        String albumName = getTrackTitle(document);

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        ObjectMapper mapper = new ObjectMapper(factory);
        JsonNode rootNode = mapper.readTree(trackInfo);
        JsonNode trackInfoVals = rootNode.get(Constants.BANDCAMP_TRACK_INFO_KEY);
        List<SongInfo> songsInfo = new ArrayList<>();
        for (JsonNode trackInfoVal : trackInfoVals) {
            String title = String.valueOf(trackInfoVal.get(Constants.BANDCAMP_TITLE_KEY));
            String downloadUrl = String.valueOf(trackInfoVal.get(Constants.BANDCAMP_FILE_KEY).get(Constants.BANDCAMP_MP3_KEY));
            if (Constants.EMPTY_STRING.equals(downloadUrl) || Constants.NULL_STRING.equals(downloadUrl) ||
                    Constants.EMPTY_STRING.equals(title) || Constants.NULL_STRING.equals(title)) {
                continue;
            }
            SongInfo songInfo = new SongInfo(songsCount++, title, downloadUrl);
            songsInfo.add(songInfo);
        }
        artistInfo.addSongsInfoToAlbum(songsInfo, albumName);
    }

    private void handleTrack(Document document) {
        Log.d(TAG, "handleTrack() start");
        String trAlbumData = getTrAlbumData(document);
        String songTitle = getTrackTitle(document);
        String songUrl = getTrackUrl(trAlbumData);

        if (Constants.EMPTY_STRING.equals(songUrl) || Constants.EMPTY_STRING.equals(songTitle)) {
            Log.d(TAG, "handleTrack(): Empty name: " + songTitle + " or url: " + songUrl);
            return;
        }

        SongInfo songInfo = new SongInfo(songsCount++, songTitle, songUrl);
        artistInfo.addSongInfoToAlbum(songInfo, Constants.DUMMY_ALBUM_NAME);
    }

    private String getTrackInfoForAlbum(Document document) {
        String scriptData = document.getElementsByTag("script").toString().replaceAll("\\s", " ");
        return RegexUtils.getFirstRegexResult(Constants.BANDCAMP_TRACK_INFO_REGEX, scriptData);
    }

    private String getTrAlbumData(Document document) {
        String scriptData = document.getElementsByTag("script").toString().replaceAll("\\s", " ");
        return RegexUtils.getFirstRegexResult(Constants.BANDCAMP_TRALBUM_REGEX, scriptData);
    }

    private String getTrackTitle(Document document) {
        return document.select(Constants.BANDCAMP_TRACK_TITLE_SELECTOR).text();
    }

    private String getTrackUrl(String trAlbumData) {
        String downloadUrlMatch = RegexUtils.getFirstRegexResult(Constants.BANDCAMP_SONG_DOWNLOAD_URL_REGEX, trAlbumData);
        if (Constants.EMPTY_STRING.equals(downloadUrlMatch)) {
            Log.d(TAG, "getTrackUrl() got no match for track download url: " + downloadUrlMatch);
            return Constants.EMPTY_STRING;
        }
        String[] match = downloadUrlMatch.split(":");
        if (match.length != 2) {
            Log.d(TAG, "getTrackUrl() wrong match for track download url: " + downloadUrlMatch);
            return Constants.EMPTY_STRING;
        }
        return match[1];
    }

    private String getBaseUrlForArtist() {
        String artistUrl = getUrl();
        if (artistUrl.endsWith("/music")) {
            return artistUrl.replace("/music", "");
        }
        return artistUrl;
    }

    // TODO: 04-09-2018 Remove this 
//    public static void main(String[] args) throws IOException {
//        Document document = Jsoup.connect("https://ommosound.bandcamp.com/track/plutesc-in-aer").get();
//        String trAlbumData = getTrAlbumData(document);
////        parseData(trAlbumData);
//        Document document1 = Jsoup.connect("https://ommosound.bandcamp.com/album/merkaba-ep").get();
//        String trAlbumData1 = getTrAlbumData(document);
//        Document document2 = Jsoup.connect("https://naxatras.bandcamp.com/").get();
//        Document document3 = Jsoup.connect("https://naxatras.bandcamp.com/album/ii").get();
//        String trAlbumData2 = getTrAlbumData(document3);
//
//        if (trAlbumData.equals(trAlbumData1)) {
//            return;
//        }
//    }
}
