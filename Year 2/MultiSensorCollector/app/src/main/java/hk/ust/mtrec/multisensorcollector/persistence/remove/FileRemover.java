package hk.ust.mtrec.multisensorcollector.persistence.remove;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import hk.ust.mtrec.multisensorcollector.utils.file.FileUtils;

/**
 * Created by tanjiajie on 2/20/17.
 */
public class FileRemover {

    private static final File DELETED_DATA_DIR = FileUtils.getDeletedDataDirectory();
    private Context context;

    public FileRemover(Context context) {
        this.context = context;
    }

    public void doRemoveJob() {
        new AlertDialog.Builder(context)
                .setMessage("ALL collected data will be deleted.")
                .setTitle("Warning")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        removeAllFilesInDir(FileUtils.getDataCollectionDirectory());
                        moveAllFilesToRecycled(FileUtils.getDataCollectionDirectory());
                        Toast.makeText(context, "All data files are deleted.", Toast.LENGTH_SHORT)
                                .show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    public void removeAllFilesInDir(File dir) {
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(dir);
            dir.mkdirs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveAllFilesToRecycled(File dir) {
        try {
            org.apache.commons.io.FileUtils.moveDirectory(
                    dir,
                    new File(DELETED_DATA_DIR, String.valueOf(System.currentTimeMillis())));
            dir.mkdirs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
