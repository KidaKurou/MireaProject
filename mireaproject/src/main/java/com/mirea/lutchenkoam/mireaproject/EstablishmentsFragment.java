package com.mirea.lutchenkoam.mireaproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

import com.mirea.lutchenkoam.mireaproject.databinding.FragmentEstablishmentsBinding;

public class EstablishmentsFragment extends Fragment {

    private FragmentEstablishmentsBinding binding;

    private static class Establishment {
        GeoPoint location;
        String name;
        String description;
        String address;

        Establishment(GeoPoint location, String name, String description, String address) {
            this.location = location;
            this.name = name;
            this.description = description;
            this.address = address;
        }
    }

    private final List<Establishment> establishments = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public EstablishmentsFragment() {
        // Required empty public constructor
    }
    public static EstablishmentsFragment newInstance(String param1, String param2) {
        EstablishmentsFragment fragment = new EstablishmentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private MapView mapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue("com.mirea.lutchenkoam.mireaproject");

        // Initialize establishments with their locations, names, descriptions, and addresses
        establishments.add(new Establishment(new GeoPoint(55.753544, 37.621202), "Красная площадь", "Красная площадь. Красиво там)", "Москва, Красная пл."));
        establishments.add(new Establishment(new GeoPoint(55.735013, 37.605742), "Третьяковская галерея", "Государственная третьяковская галерея", "ул. Крымский вал, 10"));
        establishments.add(new Establishment(new GeoPoint(55.784822, 37.616914), "Музей ВС РФ", "Музей ВС РФ", "ул. Советской Армии, 2с1"));
        establishments.add(new Establishment(new GeoPoint(55.760186, 37.618711), "Большой театр", "Исторический театр", "Театральная площадь, 1"));
        establishments.add(new Establishment(new GeoPoint(55.751999, 37.618306), "Государственный исторический музей", "Музей русской истории", "Красная пл., 1"));
        establishments.add(new Establishment(new GeoPoint(55.757852, 37.615136), "Парк Зарядье", "Современный парк в центре Москвы", "ул. Варварка, 6"));
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEstablishmentsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.map.setTileSource(TileSourceFactory.MAPNIK);
        binding.map.setBuiltInZoomControls(true);
        binding.map.setMultiTouchControls(true);

        IMapController mapController = binding.map.getController();
        mapController.setZoom(10);
        GeoPoint startPoint = new GeoPoint(55.794229, 37.700772);
        mapController.setCenter(startPoint);

        addMarkersToMap();

        binding.searchField.setOnEditorActionListener((v, actionId, event) -> {
            String searchText = v.getText().toString();
            searchPlaces(searchText);
            return true;
        });

        // Long press listener to add custom marker
        binding.map.setOnLongClickListener(v -> {
            GeoPoint point = (GeoPoint) binding.map.getProjection().fromPixels((int) v.getX(), (int) v.getY());
            addCustomMarker(point);
            return true;
        });

        return view;
    }

    private void addMarkersToMap() {
        for (Establishment establishment : establishments) {
            addMarker(establishment.location, establishment.name, establishment.description, establishment.address);
        }
    }

    private void addMarker(GeoPoint point, String title, String description, String address) {
        Marker marker = new Marker(binding.map);
        marker.setPosition(point);
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title + "\n" + description + "\nАдрес: " + address);
        binding.map.getOverlays().add(marker);
    }

    private void searchPlaces(String query) {
        binding.map.getOverlays().clear();
        if (query.isEmpty()) {
            addMarkersToMap();
        } else {
            for (Establishment establishment : establishments) {
                if (establishment.name.toLowerCase().contains(query.toLowerCase()) ||
                        establishment.description.toLowerCase().contains(query.toLowerCase()) ||
                        establishment.address.toLowerCase().contains(query.toLowerCase())) {
                    addMarker(establishment.location, establishment.name, establishment.description, establishment.address);
                }
            }
        }
        binding.map.invalidate();
    }

    private void addCustomMarker(GeoPoint point) {
        Marker marker = new Marker(binding.map);
        marker.setPosition(point);
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Custom Marker\n" + point.getLatitude() + ", " + point.getLongitude());
        binding.map.getOverlays().add(marker);
        binding.map.invalidate();

        Toast.makeText(getContext(), "Custom marker added at: " + point.getLatitude() + ", " + point.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.map.onPause();
    }
}