package com.ks.musicdownloader.songsprocessors.bandcamp;

import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.Utils.RegexUtils;
import com.ks.musicdownloader.songsprocessors.BaseParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class BandcampParser extends BaseParser {

    private static final String TAG = BandcampParser.class.getSimpleName();

    private ArtistInfo artistInfo;

    public BandcampParser(String url, DownloadCallback downloadCallback) {
        super(url, downloadCallback);
    }

    @Override
    public ArtistInfo parseArtistInfo() throws IOException {
        Log.d(TAG, "parseArtistInfo()");
        artistInfo = new ArtistInfo();
        Document document = fetchDocumentFromUrl(getUrl());
        if (isArtistUrl()) {
            handleArtist(document);
        } else {
            handleTrackOrAlbum(document);
        }
        return artistInfo;
    }

    private boolean isArtistUrl() {
        return RegexUtils.isRegexMatching(Constants.BANDCAMP_ARTIST_URL_REGEX, getUrl());
    }

    private void handleArtist(Document document) {
        Log.d(TAG, "handleArtist()");
        // get the list of album urls and call handleTrackOrAlbum with the document of every url
    }

    private void handleTrackOrAlbum(Document document) {
        Log.d(TAG, "handleTrackOrAlbum()");
        String trAlbumData = Constants.EMPTY_STRING;
        try {
            trAlbumData = getTrAlbumData(document);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "handleTrackOrAlbum(): Error found while getting tralbum data: " + e);
        }

        if (Constants.EMPTY_STRING.equals(trAlbumData)) {
            Log.d(TAG, "handleTrackOrAlbum(): No tralbum data found for url: " + getUrl());
            artistInfo = Constants.DUMMY_ARTIST_INFO;
            return;
        }

        String type = getUrlType(trAlbumData);
        if (Constants.EMPTY_STRING.equals(type)) {
            Log.d(TAG, "handleTrackOrAlbum(): No type found in tralbum data for url: " + getUrl());
            artistInfo = Constants.DUMMY_ARTIST_INFO;
            return;
        }

        if (Constants.BANDCAMP_TYPE_ALBUM.equals(type)) {
            handleAlbum();
        } else if (Constants.BANDCAMP_TYPE_TRACK.equals(type)) {
            handleTrack();
        } else {
            // this should not happen
            Log.d(TAG, "handleTrackOrAlbum(): Unknown type found: " + type);
            artistInfo = Constants.DUMMY_ARTIST_INFO;
        }
    }

    private static String getTrAlbumData(Document document) {
        String scriptData = document.getElementsByTag("script").toString().replaceAll("\\s", " ");
        return RegexUtils.getFirstRegexResult(Constants.BANDCAMP_TRALBUM_REGEX, scriptData);
    }

    private String getUrlType(String trAlbumData) {
        String type = Constants.EMPTY_STRING;
        try {
            type = RegexUtils.getFirstRegexResult(Constants.BANDCAMP_TYPE_REGEX, trAlbumData).replaceAll("\"", "").split(":")[1];
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getUrlType(): Error found while getting url type from tralbum data: " + e + " for url: " + getUrl());
        }
        return type;
    }

    private void handleAlbum() {
        // TODO: 04-09-2018 complete this
        Log.d(TAG, "handleAlbum() start");
    }

    private void handleTrack() {
        // TODO: 04-09-2018 complete this 
        Log.d(TAG, "handleTrack() start");
    }

    // TODO: 04-09-2018 Remove this 
    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect("https://ommosound.bandcamp.com/track/plutesc-in-aer").get();
        String trAlbumData = getTrAlbumData(document);
        parseData(trAlbumData);
        document = Jsoup.connect("https://ommosound.bandcamp.com/album/merkaba-ep").get();
        String trAlbumData1 = getTrAlbumData(document);
        if (trAlbumData.equals(trAlbumData1)) {
            return;
        }
    }

    private static void parseData(String json) throws IOException {
        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);

        ObjectMapper mapper = new ObjectMapper(factory);
        JsonNode rootNode = mapper.readTree(json);

        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
        while (fieldsIterator.hasNext()) {

            Map.Entry<String, JsonNode> field = fieldsIterator.next();
            Log.d(TAG, "Key: " + field.getKey() + "\tValue:" + field.getValue());
        }
    }
}
