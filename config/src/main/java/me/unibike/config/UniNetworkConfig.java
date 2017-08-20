package me.unibike.config;

import static me.unibike.config.Config.DEPLOY_MODE;
import static me.unibike.config.Config.DEV;
import static me.unibike.config.Config.PROD;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

public abstract class UniNetworkConfig {

    public static final String PROTOCAL;

    public static final String DOMAIN;

    public static final String PORT;

    public static final String API_VERSION;

    public static final String BASE_URL;

    static {
        switch (DEPLOY_MODE) {
            default:
            case DEV:

                DOMAIN = "dev.unibike.me";

                PORT = "80";

                API_VERSION = "admin";

                break;
            case PROD:

                DOMAIN = "api.unibike.me";

                PORT = "80";

                API_VERSION = "admin";

                break;
        }

        PROTOCAL = "http";

        BASE_URL
                = UniNetworkConfig.PROTOCAL + "://"
                + UniNetworkConfig.DOMAIN + ":" + UniNetworkConfig.PORT
                + "/"
                + UniNetworkConfig.API_VERSION + "/";
    }
}
