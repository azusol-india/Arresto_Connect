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

import androidx.annotation.NonNull;

import java.util.List;

public class Advt_Model {
    String advt_image;
    List<VData> videos;

    public String getAdvt_image() {
        return advt_image;
    }

    public void setAdvt_image(String advt_image) {
        this.advt_image = advt_image;
    }

    public String getAdvt_url() {
        return advt_url;
    }

    public void setAdvt_url(String advt_url) {
        this.advt_url = advt_url;
    }

    String advt_url;

    public List<VData> getVideos() {
        return videos;
    }

    public void setVideos(List<VData> videos) {
        this.videos = videos;
    }

    public class VData {
        String section, page, video_url;

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public String getVideo_url() {
            return video_url;
        }

        public void setVideo_url(String video_url) {
            this.video_url = video_url;
        }

        @NonNull
        @Override
        public String toString() {
            return section;
        }
    }
}
