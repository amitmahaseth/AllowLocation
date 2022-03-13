package com.example.currentlocation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.currentlocation.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient:FusedLocationProviderClient

    companion object{
        private const val PERMISSION_REQUEST_ACCESS_LOCATION=101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()
    }

   private fun getCurrentLocation(){
        if (checkPermissions())
        {
            if (isLocationsEnabled()){
                //latitude and Longitude

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){
                    var location: Location?=it.result
                    if (location==null){
                        Toast.makeText(this, "null Received", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "SuccessFully", Toast.LENGTH_SHORT).show()
                        binding.latitude.text=""+location.latitude
                        binding.longitude.text=""+location.longitude

                    }
                }

            }
            else{
                //Setting open here
                Toast.makeText(this, "Turn on Location", Toast.LENGTH_SHORT).show()
                var intent=Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            
        }
       else{
           //requestPermission here
            requestPermission()
        }
    }

    private fun isLocationsEnabled(): Boolean {
        var locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE)as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION
            ,android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_ACCESS_LOCATION)
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)
            ==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this,android.Manifest.permission.ACCESS_FINE_LOCATION)
        ==PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== PERMISSION_REQUEST_ACCESS_LOCATION){
            if (grantResults.isEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else{
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()

            }
        }
    }
}