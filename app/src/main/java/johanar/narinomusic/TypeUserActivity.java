package johanar.narinomusic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class TypeUserActivity extends AppCompatActivity {

    private ImageButton btnArtista, btnUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_user);

        btnArtista = findViewById(R.id.btn_artista);
        btnUsuario = findViewById(R.id.btn_usuario);

        btnArtista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent typeIntent = new Intent(TypeUserActivity.this, RegisterActivity.class);
                typeIntent.putExtra("typeUser","artista");
                startActivity(typeIntent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent typeIntent = new Intent(TypeUserActivity.this, RegisterActivity.class);
                typeIntent.putExtra("typeUser","usuario");
                startActivity(typeIntent);
            }
        });
    }


}
