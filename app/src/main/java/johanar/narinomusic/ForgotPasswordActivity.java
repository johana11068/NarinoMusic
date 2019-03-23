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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText resetEmail;
    private Button resetBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressBar resetProgress;
    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();

        resetEmail = findViewById(R.id.reset_email);
        resetBtn = findViewById(R.id.reset_btn);
        resetProgress = findViewById(R.id.reset_progress);
        resetBtn.setOnClickListener(this);
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.reset_btn:
                resetPass();
                break;
        }
    }

// juan carlos 318 382 0005 310 608 4319 maria augenia aranjo 1´100 **** 1´700

    public void resetPass() {
        final String email = resetEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Por favor ingrese su correo", Toast.LENGTH_SHORT).show();
            return;
        }
        resetProgress.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    if (count == 0){
                        count = 1;
                        Toast.makeText(ForgotPasswordActivity.this, "Se ha enviado un enlace de restablecimiento de contraseña a su correo", Toast.LENGTH_LONG).show();
                        //resetEmail.setText("");
                        viewLogin();
                    }else{
                        Toast.makeText(ForgotPasswordActivity.this, "El enlace de restablecimiento de contraseña ya ha sido enviado a tu correo", Toast.LENGTH_LONG).show();
                        viewLogin();
                    }
                }else{
                    Toast.makeText(ForgotPasswordActivity.this, "Datos incorrectos, por favor intente nuevamente", Toast.LENGTH_LONG).show();
                }
                resetProgress.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void viewLogin() {
        count = 0;
        Intent loginIntent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
