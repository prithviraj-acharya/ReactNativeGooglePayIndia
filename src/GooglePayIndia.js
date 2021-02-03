/**
 * This exposes the native GooglePayIndia module as a JS module. This has a
 * function 'startPayment' which takes the following parameters:
 *
 * 1. Object
 * 
 */
import { NativeModules } from 'react-native';
module.exports = NativeModules.GooglePayIndia;