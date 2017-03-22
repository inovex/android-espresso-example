package de.inovex.testthingy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.inovex.testthingy.api.OurApi;
import de.inovex.testthingy.api.TokenRequest;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    @BindView(R.id.edittext_email)
    EditText mEmailView;
    @BindView(R.id.edittext_password)
    EditText mPasswordView;
    private Disposable loginSubscription;

    OurApi ourApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        setup();
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

    @Override
    protected void onStop() {
        if (loginSubscription != null && !loginSubscription.isDisposed())
            loginSubscription.dispose();
        super.onStop();
    }

    @OnClick(R.id.button_sign_in)
    void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            cancel = true;
        }

        if (!cancel)
            login(mEmailView.getText().toString(), mPasswordView.getText().toString());
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void login(final String email, final String password) {
        TokenRequest request = new TokenRequest(email, password);
        Completable completable = ourApi.login(request)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        
        loginSubscription = completable.subscribe(new Action() {
            @Override
            public void run() throws Exception {
                loginSuccessful();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                loginFailed();
            }
        });
    }

    private void loginFailed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_error)
                .setMessage(R.string.dialog_message_login_failed)
                .setPositiveButton(R.string.ok, null);

        dialog.show();
    }

    private void loginSuccessful() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}

