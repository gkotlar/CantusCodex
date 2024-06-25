package com.example.cantuscodex.maps;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cantuscodex.R;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment {

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            String eventLocation = "";
            Bundle bundle = getArguments();
            if (bundle != null) {
                eventLocation = bundle.getString("location");
            }

            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            Address address = null;
            String errorMessage;
            List<Address> addresses = null;

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    assert eventLocation != null;
                    addresses = geocoder.getFromLocationName(eventLocation,
                            // In this sample, get just a single address.
                            1);
                }
            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                errorMessage = "IOException>>" + ioException.getMessage();
                Log.e(TAG, "onMapReady: ioException : ", ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                errorMessage = "IllegalArgumentException>>" + illegalArgumentException.getMessage();
                Log.e(TAG, "onMapReady: illegalArgumentException :", illegalArgumentException);
            }
            if (addresses != null && !addresses.isEmpty()) {
                address = addresses.get(0);
            }

            LatLng loc = new LatLng(address.getLatitude(), address.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(loc).title("Marker in " + eventLocation));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}