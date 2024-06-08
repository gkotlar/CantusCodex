package com.example.cantuscodex.ui.login;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.cantuscodex.R;
import com.example.cantuscodex.data.users.model.User;
import com.example.cantuscodex.databinding.FragmentLoginBinding;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "LoginFragment";
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private FragmentLoginBinding binding;

    private SharedPreferences mPreferences;
    private final String sharedPrefsFile = "com.example.cantuscodex";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       // LoginViewModel loginViewModel =
        //        new ViewModelProvider(this).get(LoginViewModel.class);
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        mPreferences = requireActivity().getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Click listeners
        binding.buttonSignIn.setOnClickListener(this);
        binding.buttonSignUp.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getUid());
        }
    }

    private void signIn() {
        if (!validateForm()) {
            return;
        }

        String email = binding.fieldEmail.getText().toString();
        String password = binding.fieldPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        onAuthSuccess(task.getResult().getUser().getUid());
                    } else {
                        Toast.makeText(getContext(), "Sign In Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signUp() {
        if (!validateForm()) {
            return;
        }

        String email = binding.fieldEmail.getText().toString();
        String password = binding.fieldPassword.getText().toString();
        boolean admin = binding.checkboxAdmin.isChecked();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        String userId = task.getResult().getUser().getUid();
                        String username = usernameFromEmail(email);

                        onAuthSuccess(userId);
                        // Write new user
                        writeNewUser(userId, username, email, admin);
                    } else {
                        Toast.makeText(getContext(), "Sign Up Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onAuthSuccess(String userId) {
        // Go to MainFragment
        firestore.collection(User.FIELD_CLASSNAME).document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                    preferencesEditor.putBoolean(User.FIELD_IS_ADMIN, user.isAdmin());
                    preferencesEditor.putString(User.FIELD_USERNAME, user.getUsername());
                    preferencesEditor.putString(User.FIELD_EMAIL, user.getEmail());
                    preferencesEditor.apply();
                });

        NavHostFragment.findNavController(this).navigate(R.id.nav_home);
    }


    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(binding.fieldEmail.getText().toString())) {
            binding.fieldEmail.setError("Required");
            result = false;
        } else {
            binding.fieldEmail.setError(null);
        }

        if (TextUtils.isEmpty(binding.fieldPassword.getText().toString())) {
            binding.fieldPassword.setError("Required");
            result = false;
        } else {
            binding.fieldPassword.setError(null);
        }

        return result;
    }

    private void writeNewUser(String userId, String name, String email, boolean admin) {
        User user = new User(name, email, admin);
        Map<String, Object> userValues = user.toMap();
        firestore.collection(User.FIELD_CLASSNAME).document(userId).set(userValues);

    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonSignIn) {
            signIn();
        } else if (i == R.id.buttonSignUp) {
            signUp();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }
}