package com.gbc.dormio_mobile_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.gbc.dormio_mobile_app.network.TokenManager;
import com.gbc.dormio_mobile_app.ui.auth.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private final BroadcastReceiver sessionExpiredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            redirectToLogin();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {

                androidx.core.app.ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        IntentFilter filter = new IntentFilter(TokenManager.ACTION_SESSION_EXPIRED);
        registerReceiver(sessionExpiredReceiver, filter, Context.RECEIVER_NOT_EXPORTED);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, false)
                    .setLaunchSingleTop(true)
                    .build();

            if (itemId == R.id.homeFragment) {
                navController.popBackStack(R.id.homeFragment, false);
            } else {
                navController.navigate(itemId, null, navOptions);
            }
            return true;
        });

        Set<Integer> topLevelDestinations = new HashSet<>(Arrays.asList(
                R.id.homeFragment,
                R.id.notificationsFragment,
                R.id.scheduleFragment,
                R.id.accountSettingsFragment,
                R.id.choresFragment,
                R.id.mealsFragment,
                R.id.budgetFragment
        ));

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (topLevelDestinations.contains(destination.getId())) {
                bottomNav.setVisibility(View.VISIBLE);
            } else {
                bottomNav.setVisibility(View.GONE);
            }
        });

        handleNotificationIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationIntent(intent);
    }

    private void handleNotificationIntent(Intent intent) {
        if (intent == null) return;

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null) return;
        NavController navController = navHostFragment.getNavController();

        //Handle Maintenance Request Notifications
        if (intent.hasExtra("REQUEST_ID")) {
            String requestId = intent.getStringExtra("REQUEST_ID");
            if (requestId != null) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putInt("requestId", Integer.parseInt(requestId));
                    navController.navigate(R.id.maintenanceDetailFragment, bundle);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        //Handle Chore Notifications
        if (intent.hasExtra("CHORE_ID")) {
            String choreId = intent.getStringExtra("CHORE_ID");
            if (choreId != null) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putInt("choreId", Integer.parseInt(choreId));
                    navController.navigate(R.id.addChoreFragment, bundle);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sessionExpiredReceiver);
    }

    private void redirectToLogin() {
        TokenManager.INSTANCE.clearToken(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
