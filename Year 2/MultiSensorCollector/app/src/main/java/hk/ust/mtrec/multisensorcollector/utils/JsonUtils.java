package hk.ust.mtrec.multisensorcollector.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by tanjiajie on 2/9/17.
 */
public class JsonUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    public static ObjectMapper getMapper() {
        return mapper;
    }

}
