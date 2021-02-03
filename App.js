/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  Image,
  StatusBar,
  Pressable
} from 'react-native';
import ImagePath from './src/utils/ImagePath';
import GooglePayIndia from './src/GooglePayIndia'
import GooglePayButton from './src/Components/GooglePayButton'

const App = () => {
  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        {/* <Pressable onPress={() => {

          let googlePayObject = {
            merchantUpiId: "8299131345@okbizaxis", //your-remerchant-vpa@xxx 8299131345@okbizaxis
            merchantName: "Aemilia",
            merchantCode: "",
            transactionRefId: "",
            transactionNote: "Test Note",
            orderAmount: "5",
            transactionUrl: 'www.google.com'
          }

          GooglePayIndia.startPayment(googlePayObject)
            .then(value => {

            })
            .catch((err) => {
              console.log("ERR" + JSON.stringify(err))
            })
        }}>
          <Image
            source={ImagePath.BuyWithGooglePayGrey}
            style={{ width: 64, height: 64 }}
          />
        </Pressable> */}


        <View style={{ width: "100%", height: "100%", alignItems: 'center', justifyContent: 'center' }}>

          <GooglePayButton
            themeColor={'black'}
            onSuccess={(val) => {
              console.log(val)
            }}
            onFailure={(err) => {
              console.log("63", err)
            }}
          />

          <GooglePayButton
            themeColor={'white'}
            onSuccess={(val) => {
              console.log(val)
            }}
            onFailure={(err) => {
              console.log("63", err)
            }}
          />

          <GooglePayButton
            themeColor={'black'}
            buyWithTextRequired={false}
            onSuccess={(val) => {
              console.log(val)
            }}
            onFailure={(err) => {
              console.log("63", err)
            }}
          />

          <GooglePayButton
            themeColor={'white'}
            buyWithTextRequired={false}
            onSuccess={(val) => {
              console.log(val)
            }}
            onFailure={(err) => {
              console.log("63", err)
            }}
          />


        </View>
      </SafeAreaView>
    </>
  );
};


export default App;
