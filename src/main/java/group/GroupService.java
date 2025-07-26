package group;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class GroupService {
    private GroupDAO groupDAO;

    public GroupService() {
        groupDAO = new GroupDAO();
    }

    public Integer createGroup(String name) throws SQLException {
        return groupDAO.createGroup(name);
    }

    public void addUsersToGroup(Integer groupId, List<String> usernames) throws SQLException {
        groupDAO.addUsersToGroup(groupId, usernames);
    }

    public List<Integer> getUserGroups(String username) {
        try {
            return groupDAO.getUserGroups(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    public List<String> getGroupMembersByGroupId(Integer groupId) {
            return groupDAO.getGroupMembersByGroupId(groupId);
        }
    
    
    public boolean canUserViewGroup(String username, int groupId) {
        try {
            return groupDAO.isUserInGroup(username, groupId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
