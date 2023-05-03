package com.windypath.getgithubfriend;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import okhttp3.*;

import java.util.*;

/**
 * Azure Functions with HTTP Trigger.
 */

public class Function {


    @FunctionName("getGithubFriend")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        // Parse query parameter
        final String query = request.getQueryParameters().get("name");
        final String[] name = query.split(",");
        final String token = "Bearer " + System.getenv("GithubKeyForGetUser");
        try {
            String json = getRequestJson(name);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(
                            new DefaultContentTypeInterceptor("application/json"))
                    .build();

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), json);

            String url = "https://api.github.com/graphql";
            Request request2 = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", token)
                    .post(body)
                    .build();

            Call call = client.newCall(request2);
            Response response = call.execute();
            String res = response.body().string();

            return request.createResponseBuilder(HttpStatus.OK).body(res).build();

        } catch (Exception e) {
            context.getLogger().warning("Java HTTP trigger processed a request failure.");
            e.printStackTrace();
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        }
    }

    private String getRequestJson(String[] loginNameList) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"query\":\"query { ");
        for (int i = 0; i < loginNameList.length; i++) {
            sb.append("user").append(i);
            sb.append(": user(login: \\\"");
            sb.append(loginNameList[i]);
            sb.append("\\\") { ...UserFragment }");
        }
        sb.append("} fragment UserFragment on User { login name avatarUrl bioHTML websiteUrl }\"}");
        return sb.toString();
    }

}
