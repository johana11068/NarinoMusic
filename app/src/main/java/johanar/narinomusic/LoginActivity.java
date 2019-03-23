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
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private Button loginRegBtn;
    private Button loginReset;

    private FirebaseAuth mAuth;

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
        loginReset = findViewById(R.id.login_reset);

        loginReset.setOnClickListener(this);

        loginRegBtn.setOnClickListener(this);

        loginBtn.setOnClickListener(this);
    }

    public void loginUser() {
        String loginEmail = loginEmailText.getText().toString();
        final String loginPass = loginPassText.getText().toString();

        if (TextUtils.isEmpty(loginEmail)){
            Toast.makeText(LoginActivity.this, "Por favor ingrese su correo", Toast.LENGTH_SHORT).show();
            loginEmailText.setError("Correo no valido");
            loginEmailText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(loginPass)){
            Toast.makeText(LoginActivity.this, "Por digite su contraseña", Toast.LENGTH_SHORT).show();
            loginPassText.setError("Contraseña no valido");
            loginPassText.requestFocus();
            loginPassText.setText("");
            return;
        }
        loginProgress.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    sendToMain();
                    Toast.makeText(LoginActivity.this, "Bienvenido al sistema!", Toast.LENGTH_LONG).show();
                } else {
                    validation(task);
                }
                loginProgress.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void typeUser() {
        Intent regIntent = new Intent(LoginActivity.this, TypeUserActivity.class);
        startActivity(regIntent);
        finish();
    }

    public void resetPass(){
        Intent resetIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(resetIntent);
        finish();
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.login_btn:
                loginUser();
                break;
            case R.id.login_reg_btn:
                typeUser();
                break;
            case R.id.login_reset:
                resetPass();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }
    }

    private void sendToMain() {
        String userId = mAuth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("type")){
                        String type_user = documentSnapshot.getString("type");
                        if (type_user.equals("artista")){
                            Intent mainIntentArtista = new Intent(LoginActivity.this, MainActivityArtista.class);
                            startActivity(mainIntentArtista);
                            finish();
                        }else if(type_user.equals("usuario")){
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void validation(@NonNull Task<AuthResult> task) {
        //String errorMessage = task.getException().getMessage();
        //Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
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
                Toast.makeText(LoginActivity.this, "La dirección de correo electrónico esta mal escrito.", Toast.LENGTH_LONG).show();
                loginEmailText.setError("Correo invalido");
                loginEmailText.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(LoginActivity.this, "La contraseña no es válida.", Toast.LENGTH_LONG).show();
                loginPassText.setError("Contraseña incorrecta");
                loginPassText.requestFocus();
                loginPassText.setText("");
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(LoginActivity.this, "La contraseña no es válida.", Toast.LENGTH_LONG).show();
                loginPassText.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                loginPassText.requestFocus();
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
                Toast.makeText(LoginActivity.this, "La dirección de correo electrónico ya está en uso por otro usuario. ", Toast.LENGTH_LONG).show();
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
        }
        //Toast.makeText(getApplicationContext(), "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
    }
}
