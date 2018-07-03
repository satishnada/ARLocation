package ar.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.StateSet;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DecimalFormat;

import ar.location.compass.Compass;
import ar.location.model.CommonResponse;
import ar.location.retrofit.WebServiceCaller;
import ar.location.utility.Utility;
import ng.dat.ar.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ARActivity extends AppCompatActivity implements LocationListener {

    final static String TAG = "ARActivity";
    DecimalFormat formater = new DecimalFormat("#.######");
    private SurfaceView surfaceView;
    private FrameLayout cameraContainerLayout;
    private AROverlayView arOverlayView;
    private Camera camera;
    private ARCamera arCamera;
    private final static int REQUEST_CAMERA_PERMISSIONS_CODE = 11;
    public static final int REQUEST_LOCATION_PERMISSIONS_CODE = 0;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // define the display assembly compass picture
    private ImageView image, arrowView;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private LocationManager locationManager;
    public Location location;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    boolean locationServiceAvailable;
    private TextView txtLatitude, txtLongitude, txtBearing;
    private float currentAzimuth;
    private Compass compass;
    private boolean firstTimeApi = true;
    private boolean isRunning = false;
    private TextView txtAddress;
    private double degree = 0;
    private double previousDegree = 0;
    private double apiDegree = 0;
    public float gyroScope = 0;
    private TextView txtHouseNumber;
    private TextView txtAngle;
    private TextView txtDistance;
    private TextView txtVerticality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        cameraContainerLayout = (FrameLayout) findViewById(R.id.camera_container_layout);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        image = (ImageView) findViewById(R.id.main_image_dial);
        arrowView = (ImageView) findViewById(R.id.main_image_hands);
        arOverlayView = new AROverlayView(this);
        txtLatitude = (TextView) findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) findViewById(R.id.txtLongitude);
        txtBearing = (TextView) findViewById(R.id.txtBearing);
        txtAddress = findViewById(R.id.txtAddress);
        txtHouseNumber = findViewById(R.id.txtHouseNumber);
        txtAngle = findViewById(R.id.txtAngle);
        txtDistance = findViewById(R.id.txtDistance);
        txtVerticality = findViewById(R.id.txtVerticality);

        setupCompass();

    }

    private void setupCompass() {
        compass = new Compass(this);
        Compass.CompassListener cl = new Compass.CompassListener() {

            @Override
            public void onNewAzimuth(float azimuth,float magnetX) {
                adjustArrow(azimuth,magnetX);
            }
        };

        Compass.GyroScopeSensorListener gyroScopeListener = new Compass.GyroScopeSensorListener() {
            @Override
            public void onGyroScopeValueChange(float gyroScopeValue) {
                Log.i(TAG, "onGyroScopeValueChange: "+gyroScopeValue);

                if (gyroScopeValue > 0.5f){
                    Log.i(TAG, "onGyroScopeValueChange X : "+gyroScopeValue);
                }else if (gyroScopeValue < -0.5f){
                    Log.i(TAG, "onGyroScopeValueChange: "+gyroScopeValue);
                }
                gyroScope = gyroScopeValue;
                txtVerticality.setText(gyroScope+"");
            }

        };

        compass.setListener(cl);
        compass.setListenerGyroScope(gyroScopeListener);
    }

    private void adjustArrow(float azimuth,float magnetXValue) {
        try {
            Log.d(TAG, "will set rotation from " + currentAzimuth + " to "
                    + azimuth);
            Log.i(TAG, "Magnet Value X : "+magnetXValue);

            double verticality = Double.parseDouble(new DecimalFormat("#").format(Math.abs(magnetXValue)));

            //txtVerticality.setText("Verticality :"+verticality);

            Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            currentAzimuth = azimuth;

            degree = Double.parseDouble(new DecimalFormat("#").format(Math.abs(currentAzimuth)));

            if (degree > 0 && degree < 20) {
                txtBearing.setText(degree + " N");
            } else if (degree > 20 && degree < 60) {
                txtBearing.setText(degree + " NE");
            } else if (degree > 60 && degree < 110) {
                txtBearing.setText(degree + " E");
            } else if (degree > 110 && degree < 160) {
                txtBearing.setText(degree + " SE");
            } else if (degree > 160 && degree < 190) {
                txtBearing.setText(degree + " S");
            } else if (degree > 190 && degree < 260) {
                txtBearing.setText(degree + " SW");
            } else if (degree > 260 && degree < 280) {
                txtBearing.setText(degree + " W");
            } else if (degree > 280 && degree < 360) {
                txtBearing.setText(degree + " NW");
            }
            an.setDuration(500);
            an.setRepeatCount(0);
            an.setFillAfter(true);

            arrowView.startAnimation(an);

            if (location != null && !isRunning) {
                isRunning = true;
                if (isDistanceGreater()){
                    apiDegree = degree;
                }else {
                    apiDegree = previousDegree;
                }
                getAddressFromLatLong();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        compass.start();
        requestLocationPermission();
        requestCameraPermission();
        initAROverlayView();
    }

    @Override
    public void onPause() {
        releaseCamera();
        super.onPause();
        compass.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop compass");
        compass.stop();
    }

    public void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSIONS_CODE);
        } else {
            initARCameraView();
        }
    }

    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS_CODE);
        } else {
            initLocationService();
        }
    }

    public void initAROverlayView() {
        if (arOverlayView.getParent() != null) {
            ((ViewGroup) arOverlayView.getParent()).removeView(arOverlayView);
        }
        cameraContainerLayout.addView(arOverlayView);
    }

    public void initARCameraView() {
        reloadSurfaceView();

        if (arCamera == null) {
            arCamera = new ARCamera(this, surfaceView);
        }
        if (arCamera.getParent() != null) {
            ((ViewGroup) arCamera.getParent()).removeView(arCamera);
        }
        cameraContainerLayout.addView(arCamera);
        arCamera.setKeepScreenOn(true);
        initCamera();
    }

    private void initCamera() {
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                camera = Camera.open();
                camera.startPreview();
                arCamera.setCamera(camera);
            } catch (RuntimeException ex) {
                Toast.makeText(this, "Camera not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void reloadSurfaceView() {
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }

        cameraContainerLayout.addView(surfaceView);
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            arCamera.setCamera(null);
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "start compass");
        compass.start();
    }

    private void initLocationService() {

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            this.locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled && !isGPSEnabled) {
                // cannot get location
                this.locationServiceAvailable = false;
            }

            this.locationServiceAvailable = true;

            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    updateLatestLocation();
                }
            }

            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateLatestLocation();
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());

        }
    }

    private void updateLatestLocation() {
        if (arOverlayView != null && location != null) {
            arOverlayView.updateCurrentLocation(location);
            txtLatitude.setText(formater.format(location.getLatitude()));
            txtLongitude.setText(formater.format(location.getLongitude()));
            if (location != null && !isRunning) {
                isRunning = true;
                if (isDistanceGreater()){
                    apiDegree = degree;
                }else {
                    apiDegree = previousDegree;
                }
                getAddressFromLatLong();
            }
/*            tvCurrentLocation.setText(String.format("lat: %s \nlon: %s \naltitude: %s \n",
                    location.getLatitude(), location.getLongitude(), location.getAltitude()));*/
        }
    }

    //This is check degree distance is greater then 5 or not.
    private boolean isDistanceGreater(){
        Log.i(TAG, "isDistanceGreater: Previous D "+previousDegree + "Current D "+degree);
        if ((previousDegree - degree) > 5){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: LAT : "+location.getLatitude()+ " LONG : "+location.getLongitude());
        updateLatestLocation();
        if (location != null && !isRunning) {
            txtLatitude.setText(formater.format(location.getLatitude()));
            txtLongitude.setText(formater.format(location.getLongitude()));
            isRunning = true;
            getAddressFromLatLong();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void getAddressFromLatLong() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showError(getString(R.string.no_internet_connection));
            } else {
                if (firstTimeApi) {
                    firstTimeApi = false;
                    Utility.showProgress(this);
                }

                WebServiceCaller.ApiInterface service = WebServiceCaller.getClient();
                String GET_ADDRESS_URL = "";
                if (location != null) {
                    Log.i(TAG, "API with LOCATION ");
                    previousDegree = degree;
                    GET_ADDRESS_URL = "http://sherwin.retailscience.ca:5001/?lat=" + txtLatitude.getText().toString().trim() + "&lng=" + txtLongitude.getText().toString().trim() + "&dir=" + apiDegree;
                } else {
                    Log.i(TAG, "API without LOCATION ");
                    GET_ADDRESS_URL = "http://sherwin.retailscience.ca:5001/?lat=43.80471&lng=-79.366398&dir=150";
                }
                Call<ResponseBody> call = service.getAddressFromLatLong(GET_ADDRESS_URL);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        isRunning = false;
                        Log.i(TAG, "onResponse: " + response);

                        try {
                            JSONObject jsonResponse = new JSONObject(response.body().string());
                            JSONArray jsonArray = jsonResponse.getJSONArray("recordsets");

 /*                           for(int i=0;i<jsonArray.length();i++){

                            }*/

                            String addressString = "";

                            String centerID = jsonResponse.getJSONArray("recordsets").getJSONArray(0).getJSONObject(0).getString("CenterID");

                            String addressID =  jsonResponse.getJSONArray("recordsets").getJSONArray(1).getJSONObject(0).getString("ID");

                            String houseNumber = jsonResponse.getJSONArray("recordsets").getJSONArray(1).getJSONObject(0).getString("Address");

                            String street = jsonResponse.getJSONArray("recordsets").getJSONArray(1).getJSONObject(0).getString("Street");

                            String angel = jsonResponse.getJSONArray("recordsets").getJSONArray(1).getJSONObject(0).getString("angle");

                            String distance = jsonResponse.getJSONArray("recordsets").getJSONArray(1).getJSONObject(0).getString("dist");

                            txtHouseNumber.setText(houseNumber);
                            //"House Number : "+houseNumber+"\n"+

                     /*       addressString = "Street : "+street+"\n"+
                                            "Angle : "+angel+"\n"+
                                            "Distance : "+distance+"\n";*/

                            if (street.trim() != null && street.trim().length() > 0){
                                String[] address = street.trim().split(",");
                                if (address != null){}
                                for(int i=0;i<address.length;i++){
                                    if (address[i].length() > 0){
                                        if (i == address.length-1){
                                            addressString = addressString + address[i];
                                        }else{
                                            addressString = addressString + address[i] +"\n";
                                        }
                                    }
                                }
                            }

                            txtAddress.setText(addressString);
                            txtAngle.setText("Angle : "+angel.trim());
                            txtDistance.setText("Distance : "+distance.trim());

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        Utility.hideProgress();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Utility.log("" + t.getMessage());
                        Utility.hideProgress();
                        Utility.showError(t.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
