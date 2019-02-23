package johanar.narinomusic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private Button loginRegBtn;

    private FirebaseAuth mAuth;

    private SignInButton mGoogleBtn;
    private GoogleSignInClient mGoogleSignInClient;

    private ProgressBar loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loginEmailText = findViewById(R.id.reg_email);
        loginPassText = findViewById(R.id.reg_confirm_pass);
        loginBtn = findViewById(R.id.login_btn);
        loginRegBtn = findViewById(R.id.login_reg_btn);
        loginProgress = findViewById(R.id.login_progress);

        //********************************************************************************************************
        // Configure Google Sign In
        mGoogleBtn = (SignInButton) findViewById(R.id.googleBtn);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);



        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        //********************************************************************************************************
        loginRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(regIntent);

            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String loginEmail = loginEmailText.getText().toString();
                final String loginPass = loginPassText.getText().toString();

                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)){
                    loginProgress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                sendToMain();

                            } else {
                                //String errorMessage = task.getException().getMessage();
                                //Toast.makeText(LoginActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_INVALID_CUSTOM_TOKEN":
                                        Toast.makeText(LoginActivity.this, "El formato de token personalizado es incorrecto. Consulte la documentación.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_CUSTOM_TOKEN_MISMATCH":
                                        Toast.makeText(LoginActivity.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_INVALID_CREDENTIAL":
                                        Toast.makeText(LoginActivity.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_INVALID_EMAIL":
                                        Toast.makeText(LoginActivity.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                                        loginEmailText.setError("Correo invaido");
                                        loginEmailText.requestFocus();
                                        break;

                                    case "ERROR_WRONG_PASSWORD":
                                        Toast.makeText(LoginActivity.this, "La contraseña no es válida.", Toast.LENGTH_LONG).show();
                                        loginPassText.setError("Contraseña incorrecta");
                                        loginPassText.requestFocus();
                                        loginPassText.setText("");
                                        break;

                                    case "ERROR_USER_MISMATCH":
                                        Toast.makeText(LoginActivity.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_REQUIRES_RECENT_LOGIN":
                                        Toast.makeText(LoginActivity.this, "Esta operación es confidencial y requiere una autenticación reciente. Vuelva a iniciar sesión antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                        Toast.makeText(LoginActivity.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero con diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        Toast.makeText(LoginActivity.this, "La dirección de correo electrónico ya está en uso por otra cuenta. ", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                        Toast.makeText(LoginActivity.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_USER_DISABLED":
                                        Toast.makeText(LoginActivity.this, "La cuenta de usuario ha sido desactivada por un administrador.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_USER_TOKEN_EXPIRED":
                                        Toast.makeText(LoginActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión de nuevo.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_USER_NOT_FOUND":
                                        Toast.makeText(LoginActivity.this, "No hay ningún registro de usuario con ese correo.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_INVALID_USER_TOKEN":
                                        Toast.makeText(LoginActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión de nuevo.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_OPERATION_NOT_ALLOWED":
                                        Toast.makeText(LoginActivity.this, "Esta operación no está permitida. Debe habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_WEAK_PASSWORD":
                                        Toast.makeText(LoginActivity.this, "La contraseña dada no es válida.", Toast.LENGTH_LONG).show();
                                        loginPassText.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                                        loginPassText.requestFocus();
                                        break;
                                }
                                Toast.makeText(getApplicationContext(), "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();


                            }

                            loginProgress.setVisibility(View.INVISIBLE);

                        }
                    });

                }else{
                    Toast.makeText(LoginActivity.this,"Digite su usuario y contraseña por favor",Toast.LENGTH_LONG).show();
                }


            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){

            sendToMain();

        }


    }

    private void sendToMain() {

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            sendToMain();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,"No se pudo iniciar sesión con Google! ",Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }
}
