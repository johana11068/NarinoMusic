package johanar.narinomusic;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_confirm_pass_field;
    private Button reg_btn;
    private Button reg_login_btn;
    private ProgressBar reg_progress;
    String intenTypeUser;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //recibir valor de tipo de usuaio inten
        intenTypeUser = getIntent().getStringExtra("typeUser");

        mAuth = FirebaseAuth.getInstance();

        reg_email_field = findViewById(R.id.reg_email);
        reg_pass_field = findViewById(R.id.reg_pass);
        reg_confirm_pass_field = findViewById(R.id.reg_confirm_pass);
        reg_btn = findViewById(R.id.reg_btn);
        reg_login_btn = findViewById(R.id.reg_login_btn);
        reg_progress = findViewById(R.id.reg_progress);

        reg_login_btn.setOnClickListener(this);
        reg_btn.setOnClickListener(this);
    }

    private void loginUser() {
        Intent setupIntentLogin = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(setupIntentLogin);
        finish();
    }

    private void registerUsers(){
        String email = reg_email_field.getText().toString().trim();
        final String pass = reg_pass_field.getText().toString().trim();
        String confirm_pass = reg_confirm_pass_field.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this, "Por favor ingrese un correo valido", Toast.LENGTH_SHORT).show();
            reg_email_field.setError("Correo no valido");
            reg_email_field.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pass)){
            Toast.makeText(RegisterActivity.this, "Por favor ingrese una contraseña valida", Toast.LENGTH_SHORT).show();
            reg_pass_field.setError("Contraseña no valida");
            reg_pass_field.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirm_pass)){
            Toast.makeText(RegisterActivity.this, "Por favor verique su contraseña", Toast.LENGTH_SHORT).show();
            reg_confirm_pass_field.setError("Confirmación de contraseña no valida");
            reg_confirm_pass_field.requestFocus();
            return;
        }

        if(pass.equals(confirm_pass)){
            reg_progress.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        if (intenTypeUser != null){
                            if (intenTypeUser.equals("artista")){
                                Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                setupIntent.putExtra("typeUser","artista");
                                startActivity(setupIntent);
                                Toast.makeText(RegisterActivity.this, "Artista musical registrado con éxito, por favor completa los siguientes campos", Toast.LENGTH_LONG).show();
                                finish();
                            }else{
                                Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                setupIntent.putExtra("typeUser","usuario");
                                startActivity(setupIntent);
                                Toast.makeText(RegisterActivity.this, "Usuario registrado con éxito, por favor completa los siguientes campos", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    } else {
                        validation(task);
                    }
                    reg_progress.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden, por favor vuelva a intentarlo", Toast.LENGTH_LONG).show();
        }


    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.reg_btn:
                registerUsers();
                break;
            case R.id.reg_login_btn:
                loginUser();
                break;
        }

    }

    public void validation(@NonNull Task<AuthResult> task) {
        //String errorMessage = task.getException().getMessage();
        //Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
        switch (errorCode) {
            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(RegisterActivity.this, "El formato de token personalizado es incorrecto. Consulte la documentación.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(RegisterActivity.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(RegisterActivity.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(RegisterActivity.this, "La dirección de correo electrónico esta mal escrito.", Toast.LENGTH_LONG).show();
                reg_email_field.setError("Correo invalido");
                reg_email_field.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(RegisterActivity.this, "La contraseña no es válida.", Toast.LENGTH_LONG).show();
                reg_pass_field.setError("Contraseña incorrecta");
                reg_pass_field.requestFocus();
                reg_pass_field.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(RegisterActivity.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(RegisterActivity.this, "Esta operación es confidencial y requiere una autenticación reciente. Vuelva a iniciar sesión antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(RegisterActivity.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero con diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(RegisterActivity.this, "La dirección de correo electrónico ya está en uso por otro usuario. ", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(RegisterActivity.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(RegisterActivity.this, "La cuenta de usuario ha sido desactivada por un administrador.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(RegisterActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión de nuevo.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(RegisterActivity.this, "No hay ningún registro de usuario con ese correo.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(RegisterActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión de nuevo.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(RegisterActivity.this, "Esta operación no está permitida. Debe habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(RegisterActivity.this, "La contraseña no es válida.", Toast.LENGTH_LONG).show();
                reg_pass_field.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                reg_pass_field.requestFocus();
                break;
        }
            //Toast.makeText(getApplicationContext(), "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }*/
}
