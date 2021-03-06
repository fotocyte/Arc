package arc.ecs.fluid.generator;

import arc.ecs.fluid.generator.common.*;
import arc.ecs.fluid.generator.model.artemis.*;
import arc.ecs.fluid.generator.model.type.*;
import com.google.common.base.*;

import java.util.*;

/**
 * Transform artemis model to agnostic type model using strategy.
 * @author Daan van Yperen
 */
public class TypeModelGenerator{

    private List<BuilderModelStrategy> strategies = new LinkedList<BuilderModelStrategy>();

    /**
     * Add strategy used to convert components to agnostic builder model.
     */
    public void addStrategy(BuilderModelStrategy strategy){
        strategies.add(strategy);
    }

    /**
     * Generate a builder based on component model.
     */
    public TypeModel generate(ArtemisModel artemisModel){
        Preconditions.checkArgument(!strategies.isEmpty(), "No strategies registered to generate model.");

        TypeModel result = new TypeModel();

        for(BuilderModelStrategy strategy : strategies){
            strategy.apply(artemisModel, result);
        }

        return result;
    }
}