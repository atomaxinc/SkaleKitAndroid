#What is Skale?
Skale is a bluetooth electronic kitchen scale, which provides bluetooth connectivity. You can connect Skale with your iPhone or android device, and get the weight value from it. 

You can know more detail from [official website](https://www.skale.cc/). 

#What is SkaleKit?
SkaleKit is a SDK to utilize Skale. It contain searching, connecting, and some basic function for an electronic scale.

#How to use SkaleKit?

##Setup
You can download aar file from jcenter by adding following dependency in build.gradle.

    compile 'com.atomaxinc.android.skale:skale-utilities:0.1.0@aar'

##SkaleHelper
* ####Constructor

    mSkaleHelper = new SkaleHelper(context);

* ####Listener

        mSkaleHelper.setListener(new SkaleHelper.Listener() {
            @Override
            public void onButtonClicked(int id) {
                // invoked when button on Skale is clicked
                // 1: circle button
                // 2: square button
            }

            @Override
            public void onWeightUpdate(float weight) {
                // invoked when weight value notified from skale
                // unit of gram.
            }

            @Override
            public void onBindRequest() {
                // if new skale was found, SkaleHelper will auto request bind.
                // this callback will be invoked.
            }

            @Override
            public void onBond() {
                // invoked when pairing completed.
            }

            @Override
            public void onConnectResult(boolean success) {
                // invoked when connection task done
            }

            @Override
            public void onDisconnected() {
                // invoked when skale disconnected
            }

            @Override
            public void onBatteryLevelUpdate(int level) {
                // invoked after request battery level
            }
        });
        
* Tare 
Call tare() when you would like to set skale weight to 0g.

    `mSkaleHelper.tare();`

* ####Permission
For android M, app have to request bluetooth permission at run time. Check if application has permission with

    `SkaleHelper.requestBluetoothPermission(this, REQUEST_BT_PERMISSION);`
    
  and implement onRequestPermissionsResult() as following in AppCompatActivity

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

            if (requestCode == REQUEST_BT_PERMISSION) {

                boolean result = SkaleHelper.checkPermissionRequest(requestCode, permissions, grantResults);

                if(result){
                    mSkaleHelper.resume();
                }else{
                    Toast.makeText(this, "No bluetooth permission", Toast.LENGTH_SHORT).show();
                }

                // END_INCLUDE(permission_result)

            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
