package hk.ust.mtrec.multisensorcollector.utils.file;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;

import hk.ust.mtrec.multisensorcollector.exception.AccessResourceException;
import hk.ust.mtrec.multisensorcollector.persistence.Persistable;

/**
 * Created by tanjiajie on 2/15/17.
 */
public class FileUtils {

    public static final String EXTERNAL_APP_FOLDER = "multi_sensor_collector";
    public static final String DATA_COLLECTION_FOLDER = "data";
    public static final String DATA_DELETED_FOLDER = "deleted_data";

    public static File getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory();
    }

    public static File getExternalAppDirectory() {
        return new File(getExternalStorageDirectory(), EXTERNAL_APP_FOLDER);
    }

    public static File getDataCollectionDirectory() {
        return new File(getExternalAppDirectory(), DATA_COLLECTION_FOLDER);
    }

    public static File getDeletedDataDirectory() {
        return new File(getExternalAppDirectory(), DATA_DELETED_FOLDER);
    }

    public static InputStream getStreamFromAssets(Context context, final String fileName) throws AccessResourceException {
        try {
            AssetManager assetMgt = context.getAssets();
            // only for test
            InputStream testInputStream = assetMgt.open("map/test.txt");
            try (BufferedReader testReader = new BufferedReader(new InputStreamReader(testInputStream))) {
                String testLine = testReader.readLine();
                System.out.println(testLine);
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream inputStream = assetMgt.open(fileName);
            return inputStream;
        } catch (IOException e) {
            throw new AccessResourceException("Can not access to the resource.", e);
        }
    }

    public static InputStream getStreamFromAppDirectory(String relativePath) throws AccessResourceException {
        try {
            File file = new File(getExternalAppDirectory(), relativePath);
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new AccessResourceException("Can not access to the resource.", e);
        }
    }

    public static String getRelativePath(File file, File baseDir) {
        Stack<File> fileStack = new Stack<>();
        File mFile = file;
        fileStack.push(mFile);
        while ((mFile = mFile.getParentFile()) != null) {
            if (mFile.equals(baseDir))
                break;
            fileStack.push(mFile);
        }

        StringBuilder pathBuilder = new StringBuilder();
        while (!fileStack.isEmpty()) {
            pathBuilder.append(fileStack.pop().getName()).append(File.separator);
        }
        if (pathBuilder.length() > 0)
            pathBuilder.deleteCharAt(pathBuilder.length() - 1);
        return pathBuilder.toString();
    }

//    public static File getFileFromSDCard(Context context, String fileName) {
//        File dir = new File(getMapFileDirectory());
//        File[] files = dir.listFiles(new FilenameFilter() {
//
//            @Override
//            public boolean accept(File dir, String filename) {
//                if (filename.toLowerCase(Locale.ENGLISH).contains(
//                        fileName.toLowerCase(Locale.ENGLISH))) {
//                    int index = filename.indexOf(".");
//                    if (index != -1) {
//                        String suffix = filename.substring(index);
//                        if (filename.length() == suffix.length()
//                                + fileName.length())
//                            return true;
//                    }
//                }
//                return false;
//            }
//        });
//        if (files == null || files.length == 0) {
//            return null;
//        }
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        return BitmapFactory.decodeFile(files[0].getAbsolutePath(), options);
//    }

    public static void main(String[] args) {
        File file = new File("/home/tanjiajie/Workspaces/server/test.dat");
        File base = new File("/home/tanjiajie/Workspaces2");
        System.out.println(getRelativePath(file, base));

    }
}
