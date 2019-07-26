package graphql.spring.web.reactive.config;

/**
 * Contains default bean names for graphql beans
 */
public class BeanNames {

    private BeanNames() {
        //static field class
    }

    public static final String GRAPHQL = "graphql";

    public static final String EXECUTION_INPUT_MAPPER = "graphqlExecutionInputMapper";

    public static final String DATA_LOADER_REGISTRY = "graphqlDataLoaderRegistry";

    public static final String CONFIGURER = "graphqlConfigurer";

    public static final String HANDLER = "graphqlHandler";

    public static final String SCHEMA = "graphqlSchema";

    public static final String ROUTER_FUNCTION = "graphqlRouterFunction";
}