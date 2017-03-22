package de.inovex.testthingy;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.inovex.testthingy.api.OurApi;
import de.inovex.testthingy.api.SomeResponse;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textView)
    TextView textView;
    private OurApi ourApi;
    private Disposable subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setup();
        getData();
    }

    private void setup() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClientBuilder.addInterceptor(logging);

        Retrofit r = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BuildConfig.HOST)
                .client(okHttpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ourApi = r.create(OurApi.class);
    }

    private void getData() {
        Observable<SomeResponse> ob = ourApi.getSomeResponse()
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        
        subscription = ob.subscribe(new Consumer<SomeResponse>() {
            @Override
            public void accept(SomeResponse someResponse) throws Exception {
                String text = getString(R.string.android_testing_is_funny);
                textView.setText(String.format(text, someResponse.isAndroidTestingFunny()));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                getDataFail();
            }
        });
    }

    private void getDataFail() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_error)
                .setMessage("Some error occurred")
                .setPositiveButton(R.string.ok, null);

        dialog.show();
    }

    @Override
    protected void onStop() {
        if (subscription != null && !subscription.isDisposed())
            subscription.dispose();
        super.onStop();
    }
}
