/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.listener.adapter;

import java.util.Set;

public interface TestResultAdapter {

    Object getTestResult();

    String getName();

    Object[] getParameters();

    void setAttribute(String name, Object value);

    Throwable getThrowable();

    TestResultStatus getStatus();

    Set<TestResultAdapter> getFailedTestResults();

    Set<TestResultAdapter> getSkippedTestResults();

    Set<String> getKnownClassNames();

    RuntimeException getSkipExceptionInstance(String message);

    MethodAdapter getMethodAdapter();

    SuiteAdapter getSuiteAdapter();

}