/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

/*
 *  *The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.data.models;

import java.util.ArrayList;

public class Periodic_model {

    public String getPdm_id() {
        return pdm_id;
    }

    public void setPdm_id(String pdm_id) {
        this.pdm_id = pdm_id;
    }

    String pdm_id;
    String pdm_step;
    String pdm_process;

    public String getPdm_media() {
        return pdm_media;
    }

    public void setPdm_media(String pdm_media) {
        this.pdm_media = pdm_media;
    }

    String pdm_media;

    public String getPdm_step() {
        return pdm_step;
    }

    public void setPdm_step(String pdm_step) {
        this.pdm_step = pdm_step;
    }

    public String getPdm_process() {
        return pdm_process;
    }

    public void setPdm_process(String pdm_process) {
        this.pdm_process = pdm_process;
    }

    public ArrayList<Pdm_array> getPdm_observations() {
        return pdm_observations;
    }

    public void setPdm_observations(ArrayList<Pdm_array> pdm_observations) {
        this.pdm_observations = pdm_observations;
    }

    public ArrayList<Pdm_array> getPdm_expresults() {
        return pdm_expresults;
    }

    public void setPdm_expresults(ArrayList<Pdm_array> pdm_expresults) {
        this.pdm_expresults = pdm_expresults;
    }
    private ArrayList<Pdm_array> pdm_observations,pdm_expresults;
    public static class Pdm_array {
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType_name() {
            return type_name;
        }

        public void setType_name(String type_name) {
            this.type_name = type_name;
        }

        @Override
        public String toString() {
            return type_name;
        }

        String id,type_name;

    }

    public ArrayList<Media_array> getPdm_images() {
        return pdm_images;
    }

    public void setPdm_images(ArrayList<Media_array> pdm_images) {
        this.pdm_images = pdm_images;
    }

    public ArrayList<Media_array> getPdm_documents() {
        return pdm_documents;
    }

    public void setPdm_documents(ArrayList<Media_array> pdm_documents) {
        this.pdm_documents = pdm_documents;
    }

    private ArrayList<Media_array> pdm_images,pdm_documents;
    public class Media_array {
        public String getFile_type() {
            return file_type;
        }

        public void setFile_type(String file_type) {
            this.file_type = file_type;
        }

        public String getFile_url() {
            return file_url;
        }

        public void setFile_url(String file_url) {
            this.file_url = file_url;
        }
        @Override
        public String toString() {
            return file_url;
        }
        String file_type,file_url;
    }

}
