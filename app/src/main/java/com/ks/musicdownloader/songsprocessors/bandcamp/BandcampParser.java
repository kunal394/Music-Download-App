package com.ks.musicdownloader.songsprocessors.bandcamp;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ks.musicdownloader.Utils.LogUtils;
import com.ks.musicdownloader.Utils.RegexUtils;
import com.ks.musicdownloader.Utils.StringUtils;
import com.ks.musicdownloader.activity.common.ArtistInfo;
import com.ks.musicdownloader.activity.common.Constants;
import com.ks.musicdownloader.activity.common.SongInfo;
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
    private boolean defaultChecked;

    public BandcampParser(String url) {
        super(url);
        songsCount = 0;
    }

    @Override
    public ArtistInfo parseArtistInfo(Boolean defaultCheckedVal) throws IOException {
        this.defaultChecked = defaultCheckedVal;
        LogUtils.d(TAG, "parseArtistInfo()");
        artistInfo = new ArtistInfo();
        Document document = fetchDocumentFromUrl(getUrl());
        if (isArtistUrl(getUrl())) {
            LogUtils.d(TAG, "parseArtistInfo(): artist url found!");
            if (!setArtistNameForArtistUrl(document)) {
                LogUtils.d(TAG, "parseArtistInfo() could not set artist name!");
                return Constants.DUMMY_ARTIST_INFO;
            }
            handleArtist(document);
        } else if (isAlbumUrl(getUrl())) {
            LogUtils.d(TAG, "parseArtistInfo() album url found!");
            if (!setArtistNameForTrackOrAlbum(document)) {
                LogUtils.d(TAG, "parseArtistInfo() could not set artist name!");
                return Constants.DUMMY_ARTIST_INFO;
            }
            handleAlbum(document);
        } else if (isTrackUrl(getUrl())) {
            LogUtils.d(TAG, "parseArtistInfo() track url found!");
            if (!setArtistNameForTrackOrAlbum(document)) {
                LogUtils.d(TAG, "parseArtistInfo() could not set artist name!");
                return Constants.DUMMY_ARTIST_INFO;
            }
            handleTrack(document);
        } else {
            // should never happen
            LogUtils.d(TAG, "Unknown type url: " + getUrl());
            return Constants.DUMMY_ARTIST_INFO;
        }
        return artistInfo;
    }

    private boolean setArtistNameForArtistUrl(Document document) {
        String artistName = document.select(Constants.BANDCAMP_ARTIST_SELECTOR_FOR_ARTIST).text();
        if (StringUtils.isEmpty(artistName)) {
            return false;
        }
        artistInfo.setArtist(artistName);
        return true;
    }

    private boolean setArtistNameForTrackOrAlbum(Document document) {
        String artistName = document.select(Constants.BANDCAMP_ARTIST_SELECTOR_FOR_ALBUM_AND_TRACK).text();
        if (StringUtils.isEmpty(artistName)) {
            return false;
        }
        artistInfo.setArtist(artistName);
        return true;
    }

    private boolean isArtistUrl(String url) {
        return RegexUtils.isRegexMatching(Constants.BANDCAMP_ARTIST_URL_REGEX, url);
    }

    private boolean isAlbumUrl(String url) {
        return RegexUtils.isRegexMatching(Constants.BANDCAMP_ALBUM_URL_REGEX, url);
    }

    private boolean isTrackUrl(String url) {
        return RegexUtils.isRegexMatching(Constants.BANDCAMP_TRACK_URL_REGEX, url);
    }

    // get the list of album urls and call handleTrackOrAlbum with the document of every url
    private void handleArtist(Document document) throws IOException {
        LogUtils.d(TAG, "handleArtist() start");
        String baseUrl = getBaseUrlForArtist();
        Elements albumElements = document.select(Constants.BANDCAMP_ALBUM_LIST_SELECTOR).select("a");
        for (Element albumElement : albumElements) {
            String destUrl = albumElement.attr("href");
            destUrl = baseUrl + destUrl;
            Document destDocument = fetchDocumentFromUrl(destUrl);
            LogUtils.d(TAG, "handleArtist() calling handle album for album url: " + destUrl);
            if (isAlbumUrl(destUrl)) {
                handleAlbum(destDocument);
            } else if (isTrackUrl(destUrl)) {
                handleTrack(destDocument);
            } else {
                LogUtils.d(TAG, "handleArtist(): This should not happen. No type found for url: " + destUrl);
            }
        }
    }

    private void handleAlbum(Document document) throws IOException {
        LogUtils.d(TAG, "handleAlbum() start");
        String albumName = document.select(Constants.BANDCAMP_TRACK_TITLE_SELECTOR).text();
        parseTrackInfo(document, albumName);
    }

    private void handleTrack(Document document) throws IOException {
        LogUtils.d(TAG, "handleTrack() start");
        parseTrackInfo(document, Constants.SINGLES_ALBUM);
    }

    private void parseTrackInfo(Document document, String albumName) throws IOException {
        LogUtils.d(TAG, "parseTrackInfo()");
        String trackInfo = getTrackInfoForAlbum(document);
        if (StringUtils.isEmpty(trackInfo)) {
            LogUtils.d(TAG, "handleAlbum() no trackInfo found!");
            artistInfo = Constants.DUMMY_ARTIST_INFO;
            return;
        }
        trackInfo = "{" + trackInfo.replaceFirst(Constants.BANDCAMP_TRACK_INFO_KEY, "\"" + Constants.BANDCAMP_TRACK_INFO_KEY + "\"") + "}";
        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        ObjectMapper mapper = new ObjectMapper(factory);
        JsonNode rootNode = mapper.readTree(trackInfo);
        JsonNode trackInfoVals = rootNode.get(Constants.BANDCAMP_TRACK_INFO_KEY);
        List<SongInfo> songsInfo = new ArrayList<>();
        for (JsonNode trackInfoVal : trackInfoVals) {
            String title = String.valueOf(trackInfoVal.get(Constants.BANDCAMP_TITLE_KEY)).replaceAll("\"", "");
            String downloadUrl = String.valueOf(trackInfoVal.get(Constants.BANDCAMP_FILE_KEY).get(Constants.BANDCAMP_MP3_KEY)).replaceAll("\"", "");
            if (StringUtils.isEmptyOrNull(title) || StringUtils.isEmptyOrNull(downloadUrl)) {
                continue;
            }
            SongInfo songInfo = new SongInfo(songsCount++, title, downloadUrl, albumName, defaultChecked);
            songsInfo.add(songInfo);
        }
        artistInfo.addSongsInfoToAlbum(songsInfo, albumName);
    }

    private String getTrackInfoForAlbum(Document document) {
        String scriptData = document.getElementsByTag("script").toString().replaceAll("\\s", " ");
        return RegexUtils.getFirstRegexResult(Constants.BANDCAMP_TRACK_INFO_REGEX, scriptData);
    }

    private String getBaseUrlForArtist() {
        String artistUrl = getUrl();
        if (artistUrl.endsWith("/music")) {
            return artistUrl.replace("/music", "");
        }
        return artistUrl;
    }
}
