package com.github.wshimaya.oursongmonitor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestClass {

  @Mock
  Context context;

  @Mock
  LambdaLogger logger;

  @Test
  void test() throws Throwable {
    doReturn(logger).when(context).getLogger();
    doAnswer(invocation -> {
      System.out.print(invocation.getArgument(0).toString());
      return null;
    }).when(logger).log(anyString());

    var target = new MonitorHandler();
    target.handleRequest(null, context);
  }

}
