/*
package ch.uzh.ifi.hase.soprafs24.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

public class PrincipalHandshake extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wshandler,
                                      Map<String, Object> attributes) {

        return new StompPrincipal(UUID.randomUUID().toString());
    }

}
 */
