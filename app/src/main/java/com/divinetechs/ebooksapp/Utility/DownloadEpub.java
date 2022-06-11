package com.divinetechs.ebooksapp.Utility;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.divinetechs.ebooksapp.R;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.folioreader.Config;
import com.folioreader.FolioReader;
import com.folioreader.model.HighLight;
import com.folioreader.model.locators.ReadLocator;
import com.folioreader.ui.base.OnSaveHighlight;
import com.folioreader.util.AppUtil;
import com.folioreader.util.OnHighlightListener;
import com.folioreader.util.ReadLocatorListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class DownloadEpub implements OnHighlightListener, ReadLocatorListener, FolioReader.OnClosedListener {

    private Activity activity;
    FolioReader folioReader;
    ArrayList<HighLight> highLightslist;
    Config config;
    File pdfFile = null;

    public DownloadEpub(Activity activity) {
        this.activity = activity;
    }

    public void pathEpub(String path, String id, String type, boolean isDownlaoded) {
        // declare the dialog as a member field of your activity
        getHighlightAndSave();
        folioReader = FolioReader.get()
                .setOnHighlightListener(this)
                .setReadLocatorListener(this)
                .setOnClosedListener(this);

        config = AppUtil.getSavedConfig(activity);
        if (config == null)
            config = new Config();
        config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

        Log.e("path =>", "" + path);
        Log.e("id =>", "" + id);
        Log.e("type =>", "" + type);
        if (!isDownlaoded) {
            DownloadAndSave(path, id, type);
            //new DownloadTaskEpub().execute(path, id, type);
        } else {
            if (path != null) {
                FolioReader folioReader = FolioReader.get();
                folioReader.openBook(path);
            } else {
                //Toasty.info(activity, "" + activity.getResources().getString(R.string.this_file_is_not_available), Toasty.LENGTH_SHORT).show();
                DownloadAndSave(path, id, type);
            }
        }
    }

    private void getHighlightAndSave() {
        new Thread(() -> {
            highLightslist = null;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                highLightslist = objectMapper.readValue(loadAssetTextAsString("highlights/highlights_data.json"), new TypeReference<List<com.divinetechs.ebooksapp.Utility.HighlightData>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (highLightslist == null) {
                folioReader.saveReceivedHighLights(highLightslist, new OnSaveHighlight() {
                    @Override
                    public void onFinished() {
                        Log.i("LOG_TAG", "-> saveReadLocator -> " + highLightslist);
                    }
                });
            }
        }).start();
    }

    private String loadAssetTextAsString(String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = activity.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("HomeActivity", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("HomeActivity", "Error closing asset " + name);
                }
            }
        }
        return null;
    }

    @Override
    public void onFolioReaderClosed() {
        FolioReader.clear();
    }

    @Override
    public void onHighlight(HighLight highlight, HighLight.HighLightAction type) {

    }

    @Override
    public void saveReadLocator(ReadLocator readLocator) {
        Log.i("readLocator", "-> saveReadLocator -> " + readLocator.toJson());
    }

    /*========= Download START =========*/
    private void DownloadAndSave(String bookURL, String bookID, String docType) {
        try {
            if (bookURL != null) {
                Log.e("=> URL", "" + bookURL);
                String saveBookName = "";
                if (bookURL.contains(".epub") || bookURL.contains(".EPUB")) {
                    saveBookName = docType + bookID + ".epub";
                }

                String downloadDirectory;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (docType.equalsIgnoreCase("book")) {
                        downloadDirectory = com.divinetechs.ebooksapp.Utility.Functions.getAppFolder(activity) + activity.getResources().getString(R.string.books) + "/";
                    } else {
                        downloadDirectory = com.divinetechs.ebooksapp.Utility.Functions.getAppFolder(activity) + activity.getResources().getString(R.string.magazines) + "/";
                    }
                } else {
                    if (docType.equalsIgnoreCase("book")) {
                        downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + "/" + activity.getResources().getString(R.string.books) + "/";
                    } else {
                        downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + "/" + activity.getResources().getString(R.string.magazines) + "/";
                    }
                }

                File file = new File(downloadDirectory);
                if (!file.exists()) {
                    Log.e("DownloadAndSave", "Document directory created again");
                    file.mkdirs();
                }

                File checkFile;
                checkFile = new File(file, saveBookName);
                Log.e("DownloadAndSave", "checkFile => " + checkFile);

                if (!checkFile.exists()) {
                    com.divinetechs.ebooksapp.Utility.Functions.showDeterminentLoader(activity, false, false);
                    PRDownloader.initialize(activity);
                    DownloadRequest prDownloader = PRDownloader.download(bookURL, downloadDirectory, saveBookName)
                            .build()
                            .setOnProgressListener(new OnProgressListener() {
                                @Override
                                public void onProgress(Progress progress) {
                                    int prog = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                                    com.divinetechs.ebooksapp.Utility.Functions.showLoadingProgress(prog);
                                }
                            });

                    String finalDownloadDirectory = downloadDirectory;
                    String finalSaveBookName = saveBookName;
                    prDownloader.start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            com.divinetechs.ebooksapp.Utility.Functions.cancelDeterminentLoader();
                            Log.e("onDownloadComplete", "finalDownloadDirectory => " + finalDownloadDirectory);
                            Log.e("onDownloadComplete", "saveBookName => " + finalSaveBookName);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                downloadBook(finalDownloadDirectory, finalSaveBookName);
                            } else {
                                scanFile(finalDownloadDirectory, finalSaveBookName);
                            }
                        }

                        @Override
                        public void onError(Error error) {
                            com.divinetechs.ebooksapp.Utility.Functions.cancelDeterminentLoader();
                            Log.e("onError", "error => " + error.getServerErrorMessage());
                        }
                    });
                } else {
                    if (checkFile != null) {
                        FolioReader folioReader = FolioReader.get();
                        folioReader.openBook(checkFile.getPath());
                    } else {
                        Toasty.info(activity, "" + activity.getResources().getString(R.string.this_file_is_not_available), Toasty.LENGTH_SHORT).show();
                    }
                }

            }
        } catch (Exception e) {
            Log.e("DownloadBook", "Exception => " + e);
            e.printStackTrace();
        }
    }

    public void downloadBook(String path, String bookName) {
        Log.e("=>path", "" + path);

        ParcelFileDescriptor pfd;
        try {
            pfd = activity.getContentResolver().openFileDescriptor(Uri.fromFile(new File(path + bookName)), "w");
            Log.e("=>pfd", "" + pfd);

            FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());
            pdfFile = new File(path + bookName);
            FileInputStream in = new FileInputStream(pdfFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            pfd.close();
        } catch (Exception e) {
            Log.e("downloadBook", "Exception => " + e);
            e.printStackTrace();
            com.divinetechs.ebooksapp.Utility.Utils.ProgressbarHide();
        }

        Log.e("=>pdfFile", "" + pdfFile);
        if (pdfFile != null) {
            FolioReader folioReader = FolioReader.get();
            folioReader.openBook(pdfFile.getPath());
        } else {
            Toasty.info(activity, "" + activity.getResources().getString(R.string.this_file_is_not_available), Toasty.LENGTH_SHORT).show();
        }
    }

    public void scanFile(String downloadDirectory, String bookName) {
        MediaScannerConnection.scanFile(activity, new String[]{downloadDirectory + bookName}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("onScanCompleted", "path => " + path);
                        Log.e("onScanCompleted", "bookName => " + bookName);
                        Log.e("onScanCompleted", "uri => " + uri.toString());
                        pdfFile = new File(path);
                        if (pdfFile != null) {
                            FolioReader folioReader = FolioReader.get();
                            folioReader.openBook(pdfFile.getPath());
                        } else {
                            Toasty.info(activity, "" + activity.getResources().getString(R.string.this_file_is_not_available), Toasty.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    /*========= Download END =========*/

}
