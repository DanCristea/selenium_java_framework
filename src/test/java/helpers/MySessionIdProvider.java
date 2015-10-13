package helpers;

import com.saucelabs.common.SauceOnDemandSessionIdProvider;

public class MySessionIdProvider implements SauceOnDemandSessionIdProvider {

    /**
     * Instance variable which contains the Sauce Job Id.
     */
    private String sessionId;

    /**
     * Constructs a new instance of the MySessionIdProvider.
     */
    public MySessionIdProvider(String sessionId) {
        super();
        this.sessionId = sessionId;
    }

    /**
     *
     * @return the value of the Sauce Job id.
     */
    @Override
    public String getSessionId() {
        return sessionId;
    }
}
