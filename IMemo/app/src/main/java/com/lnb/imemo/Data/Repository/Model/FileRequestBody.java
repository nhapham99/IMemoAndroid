package com.lnb.imemo.Data.Repository.Model;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class FileRequestBody extends RequestBody {

    private InputStream inputStream;

    private String type;

    public FileRequestBody(InputStream inputStream, String type) {
        this.inputStream = inputStream;
        this.type = type;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(type);
    }

    @Override
    public long contentLength() throws IOException {
        return inputStream.available();
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(inputStream);
            sink.writeAll(source);
        } catch (Exception e) {
            if (source != null) {
                source.close();
            }
        }
    }
}
