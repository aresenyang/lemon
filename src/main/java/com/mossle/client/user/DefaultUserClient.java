package com.mossle.client.user;

import java.util.List;

import com.mossle.api.user.LocalUserConnector;
import com.mossle.api.user.RemoteUserConnector;
import com.mossle.api.user.UserDTO;

/**
 * default没用自动注入.
 */
public class DefaultUserClient implements UserClient {
    public static final String MODE_LOCAL = "local";
    public static final String MODE_CLIENT = "client";
    public static final String MODE_SERVER = "server";
    private LocalUserConnector localUserConnector;
    private RemoteUserConnector remoteUserConnector;

    public UserDTO findById(String userId, String userRepoRef) {
        UserDTO userDto = localUserConnector.findById(userId, userRepoRef);

        if (userDto != null) {
            return userDto;
        }

        return updateAndFindById(userId, userRepoRef);
    }

    public UserDTO findByUsername(String username, String userRepoRef) {
        UserDTO userDto = localUserConnector.findByUsername(username,
                userRepoRef);

        if (userDto != null) {
            return userDto;
        }

        return updateAndFindByUsername(username, userRepoRef);
    }

    public UserDTO updateAndFindById(String userId, String userRepoRef) {
        UserDTO userDto = remoteUserConnector.findById(userId, userRepoRef);

        if (userDto == null) {
            throw new IllegalStateException("cannot find user remote : "
                    + userId);
        }

        this.createOrUpdateLocalUser(userDto);

        return userDto;
    }

    public UserDTO updateAndFindByUsername(String username, String userRepoRef) {
        UserDTO userDto = remoteUserConnector.findByUsername(username,
                userRepoRef);

        if (userDto == null) {
            throw new IllegalStateException("cannot find user remote : "
                    + username);
        }

        this.createOrUpdateLocalUser(userDto);

        return userDto;
    }

    public String convertAlias(String alias, String userRepoRef) {
        if (alias == null) {
            throw new IllegalStateException(alias);
        }

        return alias.trim().toLowerCase();
    }

    public void createOrUpdateLocalUser(UserDTO userDto) {
        this.localUserConnector.createOrUpdateLocalUser(userDto);
    }

    public List<UserDTO> search(String query) {
        return this.remoteUserConnector.search(query);
    }

    // ~
    public void setLocalUserConnector(LocalUserConnector localUserConnector) {
        this.localUserConnector = localUserConnector;
    }

    public void setRemoteUserConnector(RemoteUserConnector remoteUserConnector) {
        this.remoteUserConnector = remoteUserConnector;
    }
}
