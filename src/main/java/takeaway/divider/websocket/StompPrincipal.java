package takeaway.divider.websocket;

import java.security.Principal;

/**
 * Created by Teodora.Toncheva on 01.07.2021
 */
public class StompPrincipal implements Principal {

    private String name;

    public StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
