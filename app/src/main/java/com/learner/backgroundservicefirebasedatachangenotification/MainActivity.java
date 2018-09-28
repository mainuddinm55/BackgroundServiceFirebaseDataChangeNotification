package com.learner.backgroundservicefirebasedatachangenotification;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    public static final String USER_REF = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LinearLayout rootLayout = findViewById(R.id.root_layout);
        final TextInputEditText nameEditText = findViewById(R.id.name_edit_text);
        Button clickBtn = findViewById(R.id.click_button);

        clickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameEditText.getText().toString().isEmpty()){
                    nameEditText.setError(getResources().getString(R.string.name_error_text));
                    nameEditText.requestFocus();
                    return;
                }

                String name = nameEditText.getText().toString();
                User user = new User(name);
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(USER_REF);
                userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Snackbar.make(rootLayout,getResources().getString(R.string.upload_succes_text),Snackbar.LENGTH_SHORT).show();
                        nameEditText.setText("");
                    }
                });
            }
        });


    }
}
