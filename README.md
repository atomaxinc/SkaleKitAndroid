#What is Skale?
Skale is a bluetooth electronic kitchen scale, which provides bluetooth connectivity. You can connect Skale with your iPhone or android device, and get the weight value from it. 

You can know more detail from [official website](https://www.skale.cc/). 

#What is SkaleKit?
SkaleKit is a SDK to utilize Skale. It contain searching, connecting, and some basic function for an electronic scale.

#How to use SkaleKit?

##Setup
Add following dependency in build.gradle

`compile 'com.atomaxinc.android.skale:skale-utilities:0.1.0'`

##SkaleHelper
*Construct
`mSkaleHelper = new SkaleHelper(this);`

*Listener
`
mSkaleHelper.setListener(new SkaleHelper.Listener() {
            @Override
            public void onButtonClicked(int id) {
                
            }

            @Override
            public void onWeightUpdate(float weight) {

            }

            @Override
            public void onBindRequest() {

            }

            @Override
            public void onBond() {

            }

            @Override
            public void onConnectResult(boolean success) {

            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onBatteryLevelUpdate(int level) {

            }
        });
`
# SkaleKitAndroid
