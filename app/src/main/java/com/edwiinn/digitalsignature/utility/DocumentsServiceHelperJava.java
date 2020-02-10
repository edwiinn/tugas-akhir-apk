package com.edwiinn.digitalsignature.utility;


import com.edwiinn.digitalsignature.BuildConfig;
import com.edwiinn.digitalsignature.model.DocumentsNameResponse;
import com.edwiinn.digitalsignature.service.DocumentService;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import java.util.concurrent.TimeUnit;

public class DocumentsServiceHelperJava {

    private final String ENDPOINT = "http://52.184.100.231/";
    private DocumentService documentService;

    private static DocumentsServiceHelperJava instance = null;

    public static DocumentsServiceHelperJava getInstance(){
        if (instance == null){
            instance = new DocumentsServiceHelperJava();
        }
        return instance;
    }

    private DocumentsServiceHelperJava() {
        Retrofit retrofit = createAdapter().build();
        this.documentService = retrofit.create(DocumentService.class);
    }


    private Retrofit.Builder createAdapter() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(
                    BuildConfig.DEBUG?HttpLoggingInterceptor.Level.BODY:HttpLoggingInterceptor.Level.NONE
                ))
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create());
    }

    public Call<DocumentsNameResponse> getAllDocuments() {
        return this.documentService.listDocuments();
    }

    public Call<ResponseBody> getDocument(String filename) {
        return this.documentService.getDocument(filename);
    }
}
