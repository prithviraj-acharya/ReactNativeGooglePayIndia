import React, { useState, useEffect } from 'react';
import {
    Image,
    Text,
    TouchableOpacity,
} from 'react-native';
import Colors from '../assests/Colors';
import PropTypes from 'prop-types';
import GooglePayIndia from '../GooglePayIndia'
import ImagePath from '../utils/ImagePath'
import normalize from '../utils/helpers/Dimens'

export default function GooglePayButton(props) {

    function onInitiatePayment(){
        
        GooglePayIndia.startPayment(props.googlePayObject)
        .then(value => {
            console.log("SUCCESS" + JSON.stringify(value));
            
            props.onSuccess(value);

        })
        .catch((err) => {
          console.log("ERR" + JSON.stringify(err));

          props.onFailure(err);

        })
    }


    return (


        <TouchableOpacity 
        activeOpacity={0.8}
        style={{
            height: normalize(45), width: normalize(195),
            backgroundColor: props.themeColor === 'black' ? Colors.black : Colors.white,
            elevation: props.themeColor === 'black' ? 0 : normalize(5),
            marginTop: normalize(20), flexDirection: 'row', justifyContent: 'center',
            alignItems: 'center', borderRadius: normalize(5)
        }}
            onPress={() => {
                onInitiatePayment();
            }}
        >
            {props.buyWithTextRequired && <Text style={{
                color: props.themeColor === 'black' ? Colors.white : Colors.textGrey,
                fontSize: normalize(19), 
                fontFamily:'ProductSansRegular'
            }}>Buy with</Text>}

            <Image
                source={ImagePath.BuyWithGooglePayGrey}
                style={{
                    width: normalize(17),
                    height: normalize(18),
                    marginHorizontal: normalize(8)
                }}
            />

            <Text style={{
                color: props.themeColor === 'black' ? Colors.white : Colors.buttonTextGrey,
                fontSize: normalize(19),  fontFamily:'ProductSansRegular', fontWeight:'900'
            }}>Pay</Text>

        </TouchableOpacity>

    )

}


GooglePayButton.propTypes = {

    onSuccess: PropTypes.func,
    onFailure: PropTypes.func,
    themeColor: PropTypes.string,
    buyWithTextRequired: PropTypes.bool,
    googlePayObject: PropTypes.object,

}

GooglePayButton.defaultProps = {

    onPressed: null,
    onFailure: null,
    themeColor: 'black',
    buyWithTextRequired: true,
    googlePayObject: {}
}