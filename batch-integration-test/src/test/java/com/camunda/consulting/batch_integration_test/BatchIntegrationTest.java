package com.camunda.consulting.batch_integration_test;

import static org.assertj.core.api.Assertions.*;
import java.util.ArrayList;
import java.util.Map;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.batch.Batch;
import org.camunda.bpm.engine.batch.history.HistoricBatch;
import org.camunda.community.process_test_coverage.spring_test.platform7.ProcessEngineCoverageConfiguration;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = {"camunda.bpm.job-execution.enabled=true"})
@Import(ProcessEngineCoverageConfiguration.class)
public class BatchIntegrationTest {
  
  private static final Logger LOG = LoggerFactory.getLogger(BatchIntegrationTest.class);

  @Autowired
  ProcessEngine engine;
  
  @Test
  public void test_UpdateVariableAsync_On_already_completed_processInstance() throws InterruptedException {
    // start many process instance and save the IDs in the list
    ArrayList<String> processInstanceIds = new ArrayList<>(1000);
    for(int i = 0; i < 7; i++) {
      processInstanceIds.add(engine.getRuntimeService().startProcessInstanceByKey("simpleSubProcess").getRootProcessInstanceId());
    }
    
    assertThat(engine.getRuntimeService().createProcessInstanceQuery().count()).isEqualTo(7);
    LOG.info("process instance IDs: {}", processInstanceIds);
    
    // create a batch to update a variable on all of them (using the same sequence of PI)
    Batch batch = engine.getRuntimeService().setVariablesAsync(processInstanceIds, Map.of("newVariable", "new value"));
    LOG.info("Batch: {}", batch);
    
    // complete the last process instance
    String lastTaskId = engine.getTaskService().createTaskQuery().processInstanceId(processInstanceIds.get(6)).singleResult().getId();
    engine.getTaskService().complete(lastTaskId);
    LOG.info("Completed User task {}", lastTaskId);
    
    // expectation: The batch will fail and show some errors. 
    
    Thread.sleep(5000);
    
    HistoricBatch historicBatch = engine.getHistoryService().createHistoricBatchQuery().batchId(batch.getId()).singleResult();
    LOG.info("Historic Batch: {}", historicBatch);
    LOG.info("Historic Batch Jobs: {}", engine.getHistoryService().createHistoricJobLogQuery().jobDefinitionId(batch.getBatchJobDefinitionId()).count());
    assertThat(historicBatch.getEndTime()).describedAs("Batch is not ended").isNotNull();
  }
  
}
