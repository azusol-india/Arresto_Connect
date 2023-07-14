/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.network;

public class NFC_Listner {
    public interface Write_interface {
        void write_complete();
    }

    public interface Read_interface {
        void read_complete(String read_text);
    }
}
