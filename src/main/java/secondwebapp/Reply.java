package secondwebapp;

public class Reply {
	private int id;
    private int postId;
    private String username;
    private String content;
    private Integer parentReplyId;
    private Integer groupId;

    public Reply(int id, int postId, String username, String content, Integer parentReplyId, Integer groupId) {
        this.id = id;
        this.postId = postId;
        this.username = username;
        this.content = content;
        this.parentReplyId = parentReplyId;
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public int getPostId() {
        return postId;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }
    
    public Integer getParentReplyId() {
        return parentReplyId;
    }
    
    public void setParentReplyId(Integer parentReplyId) {
        this.parentReplyId = parentReplyId;
    }
    
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}