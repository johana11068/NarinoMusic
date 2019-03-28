package johanar.narinomusic;


import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class BlogPost extends BlogPostId {

    public String user_id, user_name,user_last,image_url, desc, image_thumb;
    public Date timestamp;

    public BlogPost() {}

    public BlogPost(String user_id, String user_name,String user_last,String image_url, String desc, String image_thumb, Date timestamp) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_last = user_last;
        this.image_url = image_url;
        this.desc = desc;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_last() { return user_last; }

    public void setUser_last(String user_last) { this.user_last = user_last; }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


}
