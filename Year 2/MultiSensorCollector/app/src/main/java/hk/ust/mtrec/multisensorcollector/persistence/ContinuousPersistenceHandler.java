package hk.ust.mtrec.multisensorcollector.persistence;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import hk.ust.mtrec.multisensorcollector.utils.JsonUtils;

/**
 * Created by tanjiajie on 2/10/17.
 */
public class ContinuousPersistenceHandler implements PersistenceHandler {

    private File file;
    private BufferedWriter writer;

    private static ObjectMapper mapper = JsonUtils.getMapper();

    ContinuousPersistenceHandler(File file) {
        this.file = file;
    }

    @Override
    public void handle(final Persistable persistable) throws IOException {
        if (writer == null) {
            if (!file.exists()) {
                Log.i("persistence", file.getAbsolutePath());
                file.createNewFile();
                Log.i("persistence", "Create new file " + file.getAbsolutePath());
            }
            writer = new BufferedWriter(new FileWriter(file, true));
        }
        try {
            writer.write(mapper.writeValueAsString(persistable));
            writer.newLine();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
            writer = null;
        }
    }

}
