package com.centit.framework.core.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.centit.support.algorithm.StringBaseOpt;

public class LobSerializer implements ObjectSerializer {
    protected Logger logger = LoggerFactory.getLogger(LobSerializer.class);
    public final static LobSerializer instance = new LobSerializer();
    
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.getWriter();

        if (object == null) {
            out.writeNull();
            return;
        }
        
        if (object instanceof Clob) {
            try {
                Clob clob = (Clob) object;
                Reader reader = clob.getCharacterStream();
                StringWriter writer = new StringWriter();
                char[] buf = new char[1024];
                int len = 0;
                while ((len = reader.read(buf)) != -1) {
                    writer.write(buf, 0, len);
                }
                reader.close();
                String text = writer.toString();
                serializer.write(text);
            } catch (SQLException e) {
                logger.error(e.getMessage(),e);//e.printStackTrace();
                throw new IOException("write clob error", e);
            }
        }else   if (object instanceof Blob) {
            try {
                Blob lobData = (Blob) object;
                InputStream is = lobData.getBinaryStream();
                byte[] readBytes = new byte[is.available()];
                int count = is.read(readBytes);
                if(count>0)
                    out.writeString(new String(Base64.encodeBase64(readBytes)));
            } catch (SQLException e) {
                logger.error(e.getMessage(),e);//e.printStackTrace();
                throw new IOException("write blob error", e);
            }
        } else {     
            out.writeString(StringBaseOpt.objectToString(object));
        }
    }
}
