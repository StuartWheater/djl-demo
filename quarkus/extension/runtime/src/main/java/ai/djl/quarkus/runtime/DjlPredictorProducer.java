package ai.djl.quarkus.runtime;

import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.Translator;
//import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

/**
 * An injectable dependency that creates a {@link Predictor} using the model described in the
 * Quarkus Configuration.
 */
//@ApplicationScoped
@Dependent
public class DjlPredictorProducer<InputClass, OutputClass>
{
    private volatile ZooModel<InputClass, OutputClass> model;

    void initialize(ZooModel<InputClass, OutputClass> model)
    {
        this.model = model;
    }

    @Produces
    public ZooModel<InputClass, OutputClass> model()
    {
        return model;
    }

    @Produces
    public Predictor<InputClass, OutputClass> predictor()
    {
        return model.newPredictor();
    }
}
