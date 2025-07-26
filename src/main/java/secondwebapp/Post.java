package secondwebapp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Post {
    private int id;
    private String name;
    private String content;
    private String postShared;
    private boolean commentsEnabled;
    private Integer groupId;
    private String attachmentFilename;  
    private String attachmentFiletype;
    private InputStream attachmentData;
    private List<String> attachments;

    public Post(int id, String name, String content, String shared, boolean commentsEnabled, Integer groupId) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.postShared = shared;
        this.commentsEnabled = commentsEnabled;
        this.groupId = groupId;
        this.attachments = new ArrayList<>();
    }
    
    public Post(int id, String name, String content, String postShared, boolean commentsEnabled, Integer groupId, String attachmentFilename, String attachmentFiletype, InputStream attachmentData) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.postShared = postShared;
        this.commentsEnabled = commentsEnabled;
        this.groupId = groupId;
        this.attachmentFilename = attachmentFilename;
        this.attachmentFiletype = attachmentFiletype;
        this.attachmentData = attachmentData;
        this.attachments = new ArrayList<>();
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
    public void setId(int id) {
        this.id=id;
    }

    public boolean isCommentsEnabled() {
        return commentsEnabled;
    }

    public void setCommentsEnabled(boolean commentsEnabled) {
        this.commentsEnabled = commentsEnabled;
    }
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getPostShared() {
        return postShared;
    }

    public String getAttachmentFilename() {
        return attachmentFilename;
    }

    public void setAttachmentFilename(String attachmentFilename) {
        this.attachmentFilename = attachmentFilename;
    }

    public String getAttachmentFiletype() {
        return attachmentFiletype;
    }

    public void setAttachmentFiletype(String attachmentFiletype) {
        this.attachmentFiletype = attachmentFiletype;
    }

    public InputStream getAttachmentData() {
        return attachmentData;
    }

    public void setAttachmentData(InputStream attachmentData) {
        this.attachmentData = attachmentData;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Post post = (Post) obj;
        return id == post.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
