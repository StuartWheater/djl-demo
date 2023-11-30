package ai.djl.quarkus.runtime;

import java.io.IOException;

import ai.djl.Application;
import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

/**
 * A helper to initialize a {@link DjlPredictorProducer}.
 */
@Recorder
public class DjlPredictorRecorder<InputClass, OutputClass> {

    public RuntimeValue<ZooModel<InputClass, OutputClass>> initializePredictor(DjlModelConfiguration configuration)
        throws ClassNotFoundException, MalformedModelException, ModelNotFoundException, IOException
    {
        Class<?> inCls  = Class.forName(configuration.inputClass);
        Class<?> outCls = Class.forName(configuration.outputClass);

        Criteria<InputClass, OutputClass> criteria = (Criteria<InputClass, OutputClass>) Criteria.builder()
                .setTypes(inCls, outCls)
                .optFilters(configuration.filters)
                .build();

        configuration.application.ifPresent(application -> criteria.optApplication(Application.of(application)));
        configuration.engine.ifPresent(criteria::optEngine);
        configuration.groupId.ifPresent(criteria::optGroupId);
        configuration.artifactId.ifPresent(criteria::optArtifactId);
        configuration.modelName.ifPresent(criteria::optModelName);

        ZooModel<InputClass, OutputClass> model = ModelZoo.loadModel(criteria);
        return new RuntimeValue<>(model);
    }

    public void configureDjlPredictorProducer(BeanContainer beanContainer, RuntimeValue<ZooModel<InputClass, OutputClass>> modelHolder)
    {
        DjlPredictorProducer predictorProducer = beanContainer.beanInstance(DjlPredictorProducer.class);
        predictorProducer.initialize(modelHolder.getValue());
    }
}
