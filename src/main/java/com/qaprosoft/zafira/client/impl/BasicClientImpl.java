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
package com.qaprosoft.zafira.client.impl;

import com.qaprosoft.zafira.client.BasicClient;
import com.qaprosoft.zafira.client.Path;
import com.qaprosoft.zafira.log.Log;
import com.qaprosoft.zafira.models.db.workitem.WorkItem;
import com.qaprosoft.zafira.models.dto.JobType;
import com.qaprosoft.zafira.models.dto.ProjectType;
import com.qaprosoft.zafira.models.dto.TestArtifactType;
import com.qaprosoft.zafira.models.dto.TestCaseType;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestSuiteType;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.dto.UploadResult;
import com.qaprosoft.zafira.models.dto.auth.AuthTokenType;
import com.qaprosoft.zafira.models.dto.auth.RefreshTokenType;
import com.qaprosoft.zafira.models.dto.UserType;
import com.qaprosoft.zafira.util.http.HttpClient;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qaprosoft.zafira.client.ClientDefaults.USER;

public class BasicClientImpl implements BasicClient {

    private static final String ERR_MSG_PING = "Unable to send ping";
    private static final String ERR_MSG_AUTHORIZE_USER = "Unable to authorize user";
    private static final String ERR_MSG_CREATE_USER = "Unable to create user";
    private static final String ERR_MSG_REFRESH_TOKEN = "Unable to refresh authorization token";
    private static final String ERR_MSG_CREATE_JOB = "Unable to create job";
    private static final String ERR_MSG_CREATE_TEST_SUITE = "Unable to create test suite";
    private static final String ERR_MSG_START_TEST_RUN = "Unable to start test run";
    private static final String ERR_MSG_UPDATE_TEST_RUN = "Unable to start test run";
    private static final String ERR_MSG_FINISH_TEST_RUN = "Unable to finish test run";
    private static final String ERR_MSG_FIND_TEST_RUN_BY_ID = "Unable to find test run by id";
    private static final String ERR_MSG_FIND_TEST_RUN_BY_CI_RUN_ID = "Unable find test run by CI run id";
    private static final String ERR_MSG_START_TEST = "Unable to start test";
    private static final String ERR_MSG_FINISH_TEST = "Unable to finish test";
    private static final String ERR_MSG_DELETE_TEST = "Unable to delete test";
    private static final String ERR_MSG_CREATE_TEST_WORK_ITEMS = "Unable to create test work items";
    private static final String ERR_MSG_CREATE_TEST_WORK_ITEM = "Unable to create test work item";
    private static final String ERR_MSG_GET_TEST_WORK_ITEMS = "Unable to get test work items";
    private static final String ERR_MSG_ADD_TEST_ARTIFACT = "Unable to add test artifact";
    private static final String ERR_MSG_CREATE_TEST_CASE = "Unable to create test case";
    private static final String ERR_MSG_CREATE_TEST_CASES_BATCH = "Unable to create test cases";
    private static final String ERR_MSG_FIND_TEST_RUN_RESULTS = "Unable to find test run results";
    private static final String ERR_MSG_ABORT_TEST_RUN = "Unable to abort test run";
    private static final String ERR_MSG_GET_PROJECT_BY_NAME = "Unable to get project by name";
    private static final String ERR_MSG_SEND_LOGS = "Unable to send logs";
    private static final String ERR_MSG_SEND_SCREENSHOT = "Unable to send screenshot";

    private final String serviceURL;

    private String authToken;
    private AuthTokenType authTokenType;
    private String project;

    public BasicClientImpl(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    @Override
    public void setAuthData(AuthTokenType authTokenType) {
        this.authTokenType = authTokenType;
        this.authToken = authTokenType.getAuthTokenType() + " " + authTokenType.getAuthToken();
    }

    @Override
    public boolean isAvailable() {
		HttpClient.Response<?> response = HttpClient.uri(Path.STATUS_PATH, serviceURL)
                                                 .onFailure(ERR_MSG_PING)
                                                 .get(String.class);
        return response.getStatus() == 200;
    }

    @Override
    public synchronized HttpClient.Response<UserType> getUserProfile() {
        return HttpClient.uri(Path.PROFILE_PATH, retrieveHost(), authTokenType.getUserId())
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_AUTHORIZE_USER)
                         .get(UserType.class);
    }

    @Override
    public synchronized HttpClient.Response<UserType> getUserProfile(String username) {
        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("username", username);
        return HttpClient.uri(Path.USERS_PATH, requestParameters, retrieveHost())
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_AUTHORIZE_USER)
                         .get(UserType.class);
    }

    @Override
    public synchronized HttpClient.Response<AuthTokenType> refreshToken(String token) {
        RefreshTokenType entity = new RefreshTokenType(token);
        return HttpClient.uri(Path.REFRESH_TOKEN_PATH, retrieveHost())
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_REFRESH_TOKEN)
                         .post(AuthTokenType.class, entity);
    }

    private String retrieveHost() {
        return serviceURL.replace("/api/reporting", "")
                         .replace("/reporting-service", "");
    }

    @Override
    public synchronized HttpClient.Response<JobType> createJob(JobType job) {
        return HttpClient.uri(Path.JOBS_PATH, serviceURL)
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_CREATE_JOB)
                         .post(JobType.class, job);
    }

    @Override
    public synchronized HttpClient.Response<TestSuiteType> createTestSuite(TestSuiteType testSuite) {
        return HttpClient.uri(Path.TEST_SUITES_PATH, serviceURL)
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_CREATE_TEST_SUITE)
                         .post(TestSuiteType.class, testSuite);
    }

    @Override
    public HttpClient.Response<TestRunType> startTestRun(TestRunType testRun) {
        return HttpClient.uri(Path.TEST_RUNS_PATH, serviceURL)
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_START_TEST_RUN)
                         .post(TestRunType.class, testRun);
    }

    @Override
    public HttpClient.Response<TestRunType> updateTestRun(TestRunType testRun) {
        return HttpClient.uri(Path.TEST_RUNS_PATH, serviceURL)
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_UPDATE_TEST_RUN)
                         .put(TestRunType.class, testRun);
    }

    @Override
    public HttpClient.Response<TestRunType> finishTestRun(long id) {
        return HttpClient.uri(Path.TEST_RUNS_FINISH_PATH, serviceURL, id)
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_FINISH_TEST_RUN)
                         .post(TestRunType.class, null);
    }

    @Override
    public HttpClient.Response<TestRunType> getTestRun(long id) {
        return HttpClient.uri(Path.TEST_RUN_BY_ID_PATH, serviceURL, id)
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_FIND_TEST_RUN_BY_ID)
                         .get(TestRunType.class);
    }

    @Override
    public HttpClient.Response<TestRunType> getTestRunByCiRunId(String ciRunId) {
        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("ciRunId", ciRunId);
        return HttpClient.uri(Path.TEST_RUNS_PATH, requestParameters, serviceURL)
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_FIND_TEST_RUN_BY_CI_RUN_ID)
                         .get(TestRunType.class);
    }

    @Override
    public HttpClient.Response<TestType> startTest(TestType test) {
        return HttpClient.uri(Path.TESTS_PATH, serviceURL)
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_START_TEST)
                         .post(TestType.class, test);
    }

    @Override
    public HttpClient.Response<TestType> finishTest(TestType test) {
        return HttpClient.uri(Path.TEST_FINISH_PATH, serviceURL, test.getId())
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_FINISH_TEST)
                         .post(TestType.class, test);
    }

    @Override
    public void deleteTest(long id) {
        HttpClient.uri(Path.TEST_BY_ID_PATH, serviceURL, id)
                  .withAuthorization(authToken, project)
                  .onFailure(ERR_MSG_DELETE_TEST)
                  .delete(Void.class);
    }

    @Override
    public HttpClient.Response<TestType> createTestWorkItems(long testId, List<String> workItems) {
        return HttpClient.uri(Path.TEST_WORK_ITEMS_PATH, serviceURL, testId)
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_CREATE_TEST_WORK_ITEMS)
                         .post(TestType.class, workItems);
    }

    @Override
    public HttpClient.Response<WorkItem> createOrUpdateTestWorkItem(long testId, WorkItem workItem) {
        return HttpClient.uri(Path.TEST_WORK_ITEM_PATH, serviceURL, testId)
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_CREATE_TEST_WORK_ITEM)
                         .post(WorkItem.class, workItem);
    }

    @Override
    public HttpClient.Response<WorkItem[]> getTestWorkItems(long testId, WorkItem.Type type) {
        return HttpClient.uri(Path.TEST_WORK_ITEM_BY_TYPE_PATH, serviceURL, testId, type.toString())
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_GET_TEST_WORK_ITEMS)
                         .get(WorkItem[].class);
    }
    
    @Override
    public void addTestArtifact(TestArtifactType artifact) {
        HttpClient.uri(Path.TEST_ARTIFACTS_PATH, serviceURL, artifact.getTestId())
                  .withAuthorization(authToken, project)
                  .onFailure(ERR_MSG_ADD_TEST_ARTIFACT)
                  .post(Void.class, artifact);
    }

    @Override
    public synchronized HttpClient.Response<TestCaseType> createTestCase(TestCaseType testCase) {
        return HttpClient.uri(Path.TEST_CASES_PATH, serviceURL)
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_CREATE_TEST_CASE)
                         .post(TestCaseType.class, testCase);
    }

    @Override
    public HttpClient.Response<TestCaseType[]> createTestCases(TestCaseType[] testCases) {
        return HttpClient.uri(Path.TEST_CASES_BATCH_PATH, serviceURL)
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_CREATE_TEST_CASES_BATCH)
                         .post(TestCaseType[].class, testCases);
    }

    @Override
    public HttpClient.Response<TestType[]> getTestRunResults(long id) {
        return HttpClient.uri(Path.TEST_RUNS_RESULTS_PATH, serviceURL, id)
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_FIND_TEST_RUN_RESULTS)
                         .get(TestType[].class);
    }

    @Override
    public boolean abortTestRun(long id) {
        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("id", String.valueOf(id));
        HttpClient.Response<?> response = HttpClient.uri(Path.TEST_RUNS_ABORT_PATH, requestParameters, serviceURL)
                                                 .withAuthorization(authToken, project)
                                                 .onFailure(ERR_MSG_ABORT_TEST_RUN)
                                                 .post(Void.class, null);
        return response.getStatus() == 200;
    }

    @Override
    public HttpClient.Response<ProjectType> getProjectByName(String name) {
        return HttpClient.uri(Path.PROJECTS_PATH, serviceURL, name)
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_GET_PROJECT_BY_NAME)
                         .get(ProjectType.class);
    }

    @Override
    public String getProject() {
        return project;
    }

    @Override
    public String initProject(String project) {
        if (!StringUtils.isEmpty(project)) {
            HttpClient.Response<ProjectType> rs = getProjectByName(project);
            if (rs.getStatus() == 200) {
                this.project = rs.getObject().getName();
            }
        }
        return this.project;
    }

    @Override
    public synchronized UserType getUserOrAnonymousIfNotFound(String username) {
        HttpClient.Response<UserType> response = getUserProfile(username);
        if (response.getStatus() != 200) {
            response = getUserProfile(USER);
        }
        return response.getObject();
    }

    @Override
    public void sendLogs(Collection<Log> logs, Long testRunId) {
        HttpClient.uri(Path.LOGS_PATH, serviceURL, testRunId)
                  .withAuthorization(authToken, project)
                  .onFailure(ERR_MSG_SEND_LOGS)
                  .post(Void.class, logs);
    }

    @Override
    public HttpClient.Response<UploadResult> sendScreenshot(byte[] screenshot, Long testRunId, Long testId, Long capturedAt) {
        return HttpClient.uri(Path.SCREENSHOTS_PATH, serviceURL, testRunId, testId)
                         .type("image/png")
                         .header("x-zbr-screenshot-captured-at", capturedAt.toString())
                         .withAuthorization(authToken, project)
                         .onFailure(ERR_MSG_SEND_SCREENSHOT)
                         .post(UploadResult.class, screenshot);
    }

    @Override
    public String getServiceUrl() {
        return this.serviceURL;
    }

    @Override
    public String getAuthToken() {
        return authToken;
    }

    @Override
    public AuthTokenType getAuthTokenType() {
        return authTokenType;
    }
}
