package hk.ust.mtrec.multisensorcollector.utils.file;

import java.io.*;
import hk.ust.mtrec.multisensorcollector.utils.GpsMappingUtils;

public class SerializerUtils {
    public static void serialize(Serializable obj) throws IOException{

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                new File(FileUtils.getDataCollectionDirectory() ,"gps_calib_data.txt")  ));
        oos.writeObject(obj);
        System.out.println("Serialized" + obj.toString());
        oos.close();

    }
    public static GpsMappingUtils deserialize() throws IOException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                new File(FileUtils.getDataCollectionDirectory() ,"gps_calib_data.txt")));
        GpsMappingUtils obj = null;
        try {
            obj = (GpsMappingUtils) ois.readObject();
        }catch(Exception e){};
        return obj;

    }
}
