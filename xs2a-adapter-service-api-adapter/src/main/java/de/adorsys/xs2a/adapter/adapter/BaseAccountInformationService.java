/*
 * Copyright 2018-2018 adorsys GmbH & Co KG
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
 */

package de.adorsys.xs2a.adapter.adapter;

import de.adorsys.xs2a.adapter.http.ContentType;
import de.adorsys.xs2a.adapter.http.HttpClient;
import de.adorsys.xs2a.adapter.http.Request;
import de.adorsys.xs2a.adapter.http.StringUri;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import de.adorsys.xs2a.adapter.service.RequestParams;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.*;

import java.util.Map;
import java.util.function.Function;

import static de.adorsys.xs2a.adapter.http.ResponseHandlers.jsonResponseHandler;
import static de.adorsys.xs2a.adapter.http.ResponseHandlers.stringResponseHandler;
import static java.util.function.Function.identity;

public class BaseAccountInformationService extends AbstractService implements AccountInformationService {

    protected static final String V1 = "v1";
    protected static final String CONSENTS = "consents";
    protected static final String ACCOUNTS = "accounts";
    protected static final String TRANSACTIONS = "transactions";
    protected static final String BALANCES = "balances";

    protected final String baseUri;
    private final Request.Builder.Interceptor requestBuilderInterceptor;

    public BaseAccountInformationService(String baseUri, HttpClient httpClient) {
        this(baseUri, httpClient, null);
    }

    public BaseAccountInformationService(String baseUri,
                                         HttpClient httpClient,
                                         Request.Builder.Interceptor requestBuilderInterceptor) {
        super(httpClient);
        this.baseUri = baseUri;
        this.requestBuilderInterceptor = requestBuilderInterceptor;
    }

    @Override
    public Response<ConsentCreationResponse> createConsent(RequestHeaders requestHeaders, Consents body) {
        return createConsent(requestHeaders, body, ConsentCreationResponse.class, identity());
    }

    protected <T> Response<ConsentCreationResponse> createConsent(RequestHeaders requestHeaders, Consents body, Class<T> klass, Function<T, ConsentCreationResponse> mapper) {
        Map<String, String> headersMap = populatePostHeaders(requestHeaders.toMap());

        String bodyString = jsonMapper.writeValueAsString(jsonMapper.convertValue(body, Consents.class));

        Response<T> response = httpClient.post(getConsentBaseUri())
            .jsonBody(bodyString)
            .headers(headersMap)
            .send(requestBuilderInterceptor, jsonResponseHandler(klass));
        ConsentCreationResponse creationResponse = mapper.apply(response.getBody());
        return new Response<>(response.getStatusCode(), creationResponse, response.getHeaders());
    }

    @Override
    public Response<ConsentInformation> getConsentInformation(String consentId, RequestHeaders requestHeaders) {
        return getConsentInformation(consentId, requestHeaders, ConsentInformation.class, identity());
    }

    protected <T> Response<ConsentInformation> getConsentInformation(String consentId, RequestHeaders requestHeaders, Class<T> klass, Function<T, ConsentInformation> mapper) {
        String uri = StringUri.fromElements(getConsentBaseUri(), consentId);
        Map<String, String> headersMap = populateGetHeaders(requestHeaders.toMap());
        Response<T> response = httpClient.get(uri)
            .headers(headersMap)
            .send(requestBuilderInterceptor, jsonResponseHandler(klass));
        ConsentInformation consentInformation = mapper.apply(response.getBody());

        return new Response<>(response.getStatusCode(), consentInformation, response.getHeaders());
    }

    @Override
    public Response<Void> deleteConsent(String consentId, RequestHeaders requestHeaders) {
        String uri = StringUri.fromElements(getConsentBaseUri(), consentId);
        Map<String, String> headersMap = populateDeleteHeaders(requestHeaders.toMap());
        return httpClient.delete(uri)
            .headers(headersMap)
            .send(requestBuilderInterceptor, jsonResponseHandler(Void.class));
    }

    @Override
    public Response<ConsentStatusResponse> getConsentStatus(String consentId, RequestHeaders requestHeaders) {
        String uri = StringUri.fromElements(getConsentBaseUri(), consentId, STATUS);
        Map<String, String> headersMap = populateGetHeaders(requestHeaders.toMap());

        return httpClient.get(uri)
            .headers(headersMap)
            .send(requestBuilderInterceptor, jsonResponseHandler(ConsentStatusResponse.class));
    }

    @Override
    public Response<StartScaProcessResponse> startConsentAuthorisation(String consentId, RequestHeaders requestHeaders) {
        String uri = StringUri.fromElements(getConsentBaseUri(), consentId, AUTHORISATIONS);
        Map<String, String> headersMap = populatePostHeaders(requestHeaders.toMap());

        return httpClient.post(uri)
            .headers(headersMap)
            .emptyBody(true)
            .send(requestBuilderInterceptor, jsonResponseHandler(StartScaProcessResponse.class));
    }

    protected <T> Response<StartScaProcessResponse> startConsentAuthorisation(String consentId, RequestHeaders requestHeaders, Class<T> klass, Function<T, StartScaProcessResponse> mapper) {
        String uri = StringUri.fromElements(getConsentBaseUri(), consentId, AUTHORISATIONS);
        Map<String, String> headersMap = populatePostHeaders(requestHeaders.toMap());

        Response<T> response = httpClient.post(uri)
            .headers(headersMap)
            .send(requestBuilderInterceptor, jsonResponseHandler(klass));
        StartScaProcessResponse startScaProcessResponse = mapper.apply(response.getBody());
        return new Response<>(response.getStatusCode(), startScaProcessResponse, response.getHeaders());
    }

    @Override
    public Response<StartScaProcessResponse> startConsentAuthorisation(String consentId, RequestHeaders requestHeaders, UpdatePsuAuthentication updatePsuAuthentication) {
        return startConsentAuthorisation(consentId, requestHeaders, updatePsuAuthentication, StartScaProcessResponse.class, identity());
    }

    protected <T> Response<StartScaProcessResponse> startConsentAuthorisation(String consentId, RequestHeaders requestHeaders, UpdatePsuAuthentication updatePsuAuthentication, Class<T> klass, Function<T, StartScaProcessResponse> mapper) {
        String uri = StringUri.fromElements(getConsentBaseUri(), consentId, AUTHORISATIONS);
        Map<String, String> headersMap = populatePostHeaders(requestHeaders.toMap());
        String body = jsonMapper.writeValueAsString(updatePsuAuthentication);

        Response<T> response = httpClient.post(uri)
            .jsonBody(body)
            .headers(headersMap)
            .send(requestBuilderInterceptor, jsonResponseHandler(klass));
        StartScaProcessResponse startScaProcessResponse = mapper.apply(response.getBody());
        return new Response<>(response.getStatusCode(), startScaProcessResponse, response.getHeaders());
    }

    @Override
    public Response<UpdatePsuAuthenticationResponse> updateConsentsPsuData(String consentId, String authorisationId, RequestHeaders requestHeaders,
                                                                           UpdatePsuAuthentication updatePsuAuthentication) {
        return updateConsentsPsuData(consentId, authorisationId, requestHeaders, updatePsuAuthentication, UpdatePsuAuthenticationResponse.class, identity());
    }

    protected <T> Response<UpdatePsuAuthenticationResponse> updateConsentsPsuData(String consentId,
                                                                                  String authorisationId,
                                                                                  RequestHeaders requestHeaders,
                                                                                  UpdatePsuAuthentication updatePsuAuthentication,
                                                                                  Class<T> klass,
                                                                                  Function<T, UpdatePsuAuthenticationResponse> mapper) {
        String uri = StringUri.fromElements(getConsentBaseUri(), consentId, AUTHORISATIONS, authorisationId);
        Map<String, String> headersMap = populatePutHeaders(requestHeaders.toMap());
        String body = jsonMapper.writeValueAsString(updatePsuAuthentication);

        Response<T> response = httpClient.put(uri)
            .jsonBody(body)
            .headers(headersMap)
            .send(requestBuilderInterceptor, jsonResponseHandler(klass));
        UpdatePsuAuthenticationResponse updatePsuAuthenticationResponse = mapper.apply(response.getBody());
        return new Response<>(response.getStatusCode(), updatePsuAuthenticationResponse, response.getHeaders());
    }

    @Override
    public Response<SelectPsuAuthenticationMethodResponse> updateConsentsPsuData(String consentId, String authorisationId, RequestHeaders requestHeaders, SelectPsuAuthenticationMethod selectPsuAuthenticationMethod) {
        return updateConsentsPsuData(consentId, authorisationId, requestHeaders, selectPsuAuthenticationMethod, SelectPsuAuthenticationMethodResponse.class, identity());
    }

    protected <T> Response<SelectPsuAuthenticationMethodResponse> updateConsentsPsuData(String consentId,
                                                                                        String authorisationId,
                                                                                        RequestHeaders requestHeaders,
                                                                                        SelectPsuAuthenticationMethod selectPsuAuthenticationMethod,
                                                                                        Class<T> klass,
                                                                                        Function<T, SelectPsuAuthenticationMethodResponse> mapper) {
        String uri = StringUri.fromElements(getConsentBaseUri(), consentId, AUTHORISATIONS, authorisationId);
        Map<String, String> headersMap = populatePutHeaders(requestHeaders.toMap());
        String body = jsonMapper.writeValueAsString(selectPsuAuthenticationMethod);

        Response<T> response = httpClient.put(uri)
            .jsonBody(body)
            .headers(headersMap)
            .send(requestBuilderInterceptor, jsonResponseHandler(klass));
        SelectPsuAuthenticationMethodResponse selectPsuAuthenticationMethodResponse = mapper.apply(response.getBody());
        return new Response<>(response.getStatusCode(), selectPsuAuthenticationMethodResponse, response.getHeaders());
    }

    @Override
    public Response<ScaStatusResponse> updateConsentsPsuData(String consentId, String authorisationId, RequestHeaders requestHeaders, TransactionAuthorisation transactionAuthorisation) {
        return updateConsentsPsuData(consentId, authorisationId, requestHeaders, transactionAuthorisation, ScaStatusResponse.class, identity());
    }

    protected <T> Response<ScaStatusResponse> updateConsentsPsuData(String consentId,
                                                                    String authorisationId,
                                                                    RequestHeaders requestHeaders,
                                                                    TransactionAuthorisation transactionAuthorisation,
                                                                    Class<T> klass,
                                                                    Function<T, ScaStatusResponse> mapper) {
        String uri = getUpdateConsentPsuDataUri(consentId, authorisationId);
        Map<String, String> headersMap = populatePutHeaders(requestHeaders.toMap());
        String body = jsonMapper.writeValueAsString(transactionAuthorisation);

        Response<T> response = httpClient.put(uri)
            .jsonBody(body)
            .headers(headersMap)
            .send(requestBuilderInterceptor, jsonResponseHandler(klass));

        ScaStatusResponse scaStatusResponse = mapper.apply(response.getBody());
        return new Response<>(response.getStatusCode(), scaStatusResponse, response.getHeaders());
    }

    protected String getUpdateConsentPsuDataUri(String consentId, String authorisationId) {
        return StringUri.fromElements(getConsentBaseUri(), consentId, AUTHORISATIONS, authorisationId);
    }

    @Override
    public Response<AccountListHolder> getAccountList(RequestHeaders requestHeaders, RequestParams requestParams) {
        Map<String, String> headersMap = populateGetHeaders(requestHeaders.toMap());
        headersMap = addConsentIdHeader(headersMap);

        String uri = buildUri(getAccountsBaseUri(), requestParams);

        return httpClient.get(uri)
            .headers(headersMap)
            .send(requestBuilderInterceptor, jsonResponseHandler(AccountListHolder.class));
    }

    @Override
    public Response<TransactionsReport> getTransactionList(String accountId, RequestHeaders requestHeaders, RequestParams requestParams) {
        return getTransactionList(accountId, requestHeaders, requestParams, TransactionsReport.class, identity());
    }

    private String getTransactionListUri(String accountId, RequestParams requestParams) {
        String uri = StringUri.fromElements(getAccountsBaseUri(), accountId, TRANSACTIONS);
        uri = buildUri(uri, requestParams);
        return uri;
    }

    protected <T> Response<TransactionsReport> getTransactionList(String accountId, RequestHeaders requestHeaders, RequestParams requestParams, Class<T> klass, Function<T, TransactionsReport> mapper) {
        Map<String, String> headersMap = populateGetHeaders(requestHeaders.toMap());
        headersMap.put(ACCEPT_HEADER, ContentType.APPLICATION_JSON);

        String uri = getTransactionListUri(accountId, requestParams);

        Response<T> response = httpClient.get(uri)
            .headers(headersMap)
            .send(requestBuilderInterceptor, jsonResponseHandler(klass));
        TransactionsReport transactionsReport = mapper.apply(response.getBody());

        return new Response<>(response.getStatusCode(), transactionsReport, response.getHeaders());
    }

    @Override
    public Response<String> getTransactionListAsString(String accountId, RequestHeaders requestHeaders, RequestParams requestParams) {
        String uri = getTransactionListUri(accountId, requestParams);
        Map<String, String> headers = populateGetHeaders(requestHeaders.toMap());
        return httpClient.get(uri)
            .headers(headers)
            .send(requestBuilderInterceptor, stringResponseHandler());
    }

    @Override
    public Response<ScaStatusResponse> getConsentScaStatus(String consentId, String authorisationId, RequestHeaders requestHeaders) {
        String uri = StringUri.fromElements(getConsentBaseUri(), consentId, AUTHORISATIONS, authorisationId);
        Map<String, String> headers = populateGetHeaders(requestHeaders.toMap());
        return httpClient.get(uri)
            .headers(headers)
            .send(requestBuilderInterceptor, jsonResponseHandler(ScaStatusResponse.class));
    }

    @Override
    public Response<BalanceReport> getBalances(String accountId, RequestHeaders requestHeaders) {
        return getBalances(accountId, requestHeaders, BalanceReport.class, identity());
    }

    protected <T> Response<BalanceReport> getBalances(String accountId, RequestHeaders requestHeaders, Class<T> klass, Function<T, BalanceReport> mapper) {
        String uri = StringUri.fromElements(getAccountsBaseUri(), accountId, BALANCES);
        Map<String, String> headers = populateGetHeaders(requestHeaders.toMap());
        Response<T> response = httpClient.get(uri)
            .headers(headers)
            .send(requestBuilderInterceptor, jsonResponseHandler(klass));
        BalanceReport balanceReport = mapper.apply(response.getBody());
        return new Response<>(response.getStatusCode(), balanceReport, response.getHeaders());
    }


    protected String getConsentBaseUri() {
        return StringUri.fromElements(baseUri, V1, CONSENTS);
    }

    protected String getAccountsBaseUri() {
        return StringUri.fromElements(baseUri, V1, ACCOUNTS);
    }
}
