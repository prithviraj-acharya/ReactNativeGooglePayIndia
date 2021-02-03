import { PixelRatio, Platform, Dimensions } from "react-native";

const scale = (Dimensions.get("window").width / 320);

export default normalize = (size) => {
  const newSize = size * scale
  if (Platform.OS === 'ios') {
    return Math.round(PixelRatio.roundToNearestPixel(newSize))
  } else {
    return Math.round(PixelRatio.roundToNearestPixel(newSize))
  }
}
