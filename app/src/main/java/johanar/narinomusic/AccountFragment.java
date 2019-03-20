package johanar.narinomusic;


import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private CircleImageView setupImage;
    private Uri mainImageURI = null;

    private String user_id;

    private boolean isChanged = false;

    private EditText setupName, setupLast, setupDate;
    private Button setupBtn;
    private ProgressBar setupProgress;
    private TextView setupType;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private Bitmap compressedImageFile;

    String intenTypeUser;

    //fecha de nacimiento
    private EditText etBirthday;
    Calendar calendario = Calendar.getInstance();


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        //fecha de nacimiento
        etBirthday = view.findViewById(R.id.setup_date);
        etBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), date, calendario
                        .get(Calendar.YEAR), calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)).show();
            }
        });//
        //recibir valor de tipo de usuaio inten
        intenTypeUser = getActivity().getIntent().getStringExtra("typeUser");
        //Toast.makeText(SetupActivity.this, "tipo de usuario: "+intenTypeUser, Toast.LENGTH_LONG).show();

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        setupImage = view.findViewById(R.id.setup_image);
        setupName = view.findViewById(R.id.setup_name);
        setupLast = view.findViewById(R.id.setup_last);
        setupDate = view.findViewById(R.id.setup_date);
        setupType = view.findViewById(R.id.setup_type);
        setupBtn = view.findViewById(R.id.setup_btn);
        setupProgress = view.findViewById(R.id.setup_progress);

        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);

        setupType.setText(getActivity().getIntent().getStringExtra("typeUser"));
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String last = task.getResult().getString("last");
                        String birthday = task.getResult().getString("birthday");
                        String image = task.getResult().getString("image");
                        String type = task.getResult().getString("type");

                        mainImageURI = Uri.parse(image);
                        setupName.setText(name);
                        setupLast.setText(last);
                        setupDate.setText(birthday);
                        setupType.setText(type);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.user);

                        Glide.with(getActivity()).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(getActivity(), "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);
            }
        });

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_name = setupName.getText().toString();
                final String user_last = setupLast.getText().toString();
                final String user_birthday = setupDate.getText().toString();
                final String user_type = setupType.getText().toString();

                if (!TextUtils.isEmpty(user_name) && mainImageURI != null && !TextUtils.isEmpty(user_last) && !TextUtils.isEmpty(user_birthday)) {
                    setupProgress.setVisibility(View.VISIBLE);
                    if (isChanged) {
                        user_id = firebaseAuth.getCurrentUser().getUid();
                        File newImageFile = new File(mainImageURI.getPath());
                        try {
                            compressedImageFile = new Compressor(getActivity())
                                    .setMaxHeight(125)
                                    .setMaxWidth(125)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] thumbData = baos.toByteArray();

                        UploadTask image_path = storageReference.child("profile_images").child(user_id + ".jpg").putBytes(thumbData);

                        image_path.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storeFirestore(task, user_name, user_last,user_birthday,user_type);
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                                    setupProgress.setVisibility(View.INVISIBLE);
                                }
                            }
                        });

                    } else {
                        storeFirestore(null, user_name,user_last,user_birthday,user_type);
                    }
                }else{
                    Toast.makeText(getActivity(), "Todos los campos son requeridos", Toast.LENGTH_LONG).show();
                }
            }
        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(getActivity(), "Permiso Denegado!", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        BringImagePicker();
                    }
                } else {
                    BringImagePicker();
                }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name , String user_last, String user_birthday, final String user_type) {
        Uri download_uri;
        if(task != null) {
            download_uri = task.getResult().getDownloadUrl();
        } else {
            download_uri = mainImageURI;
        }
        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user_name);
        userMap.put("last", user_last);
        userMap.put("birthday", user_birthday);
        userMap.put("image", download_uri.toString());
        userMap.put("type",user_type);

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Datos actualizados con éxito!", Toast.LENGTH_LONG).show();
                    if (user_type.equals("usuario")){
                        Intent setupIntent = new Intent(getActivity(), MainActivity.class);
                        startActivity(setupIntent);
                    }else if (user_type.equals("artista")){
                        Intent setupIntentArtista = new Intent(getActivity(), MainActivityArtista.class);
                        startActivity(setupIntentArtista);
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(getActivity(), "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(getActivity());
    }

    //fecha de cumpleaños
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendario.set(Calendar.YEAR, year);
            calendario.set(Calendar.MONTH, monthOfYear);
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            actualizarInput();
        }
    };

    private void actualizarInput() {
        String formatoDeFecha = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(formatoDeFecha, Locale.US);
        etBirthday.setText(sdf.format(calendario.getTime()));
    }

}
