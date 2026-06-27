package br.imd.ufrn.core.config;

import br.imd.ufrn.core.anonymization.AnonymizationStrategy;
import br.imd.ufrn.core.anonymization.TransparentAnonymizationStrategy;
import br.imd.ufrn.core.conflict.ConflictOfInterestStrategy;
import br.imd.ufrn.core.conflict.NoConflictOfInterestStrategy;
import br.imd.ufrn.core.designation.DesignationStrategy;
import br.imd.ufrn.core.designation.ManualDesignationStrategy;
import br.imd.ufrn.core.workflow.DefaultWorkflowTemplate;
import br.imd.ufrn.core.workflow.WorkflowTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class CoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AnonymizationStrategy.class)
    public AnonymizationStrategy transparentAnonymizationStrategy() {
        return new TransparentAnonymizationStrategy();
    }

    @Bean
    @ConditionalOnMissingBean(WorkflowTemplate.class)
    public WorkflowTemplate defaultWorkflowTemplate() {
        return new DefaultWorkflowTemplate();
    }

    @Bean
    @ConditionalOnMissingBean(ConflictOfInterestStrategy.class)
    public ConflictOfInterestStrategy noConflictOfInterestStrategy() {
        return new NoConflictOfInterestStrategy();
    }

    @Bean
    @ConditionalOnMissingBean(DesignationStrategy.class)
    public DesignationStrategy manualDesignationStrategy() {
        return new ManualDesignationStrategy();
    }
}
